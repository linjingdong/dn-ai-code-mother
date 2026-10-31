package com.dn.dnaicodemotherbackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.dn.dnaicodemotherbackend.constant.AppConstant;
import com.dn.dnaicodemotherbackend.core.AiCodeGeneratorFacade;
import com.dn.dnaicodemotherbackend.core.builder.VueProjectBuilder;
import com.dn.dnaicodemotherbackend.core.handler.StreamHandlerExecutor;
import com.dn.dnaicodemotherbackend.exception.BusinessException;
import com.dn.dnaicodemotherbackend.exception.ErrorCode;
import com.dn.dnaicodemotherbackend.exception.ThrowUtils;
import com.dn.dnaicodemotherbackend.mapper.AppMapper;
import com.dn.dnaicodemotherbackend.model.dto.app.AppQueryRequest;
import com.dn.dnaicodemotherbackend.model.entity.App;
import com.dn.dnaicodemotherbackend.model.entity.User;
import com.dn.dnaicodemotherbackend.model.enums.ChatHistoryMessageTypeEnum;
import com.dn.dnaicodemotherbackend.model.enums.CodeGenTypeEnum;
import com.dn.dnaicodemotherbackend.model.vo.AppVO;
import com.dn.dnaicodemotherbackend.model.vo.UserVO;
import com.dn.dnaicodemotherbackend.service.AppService;
import com.dn.dnaicodemotherbackend.service.ChatHistoryService;
import com.dn.dnaicodemotherbackend.service.ScreenshotService;
import com.dn.dnaicodemotherbackend.service.UserService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用 服务层实现。
 *
 * @author Lin_jd
 */
@Service
@Slf4j
public class AppServiceImpl extends ServiceImpl<AppMapper, App>  implements AppService{

    @Resource
    private UserService userService;

    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Resource
    private ChatHistoryService chatHistoryService;

    @Resource
    private StreamHandlerExecutor streamHandlerExecutor;

    @Resource
    private VueProjectBuilder vueProjectBuilder;

    @Resource
    private ScreenshotService screenshotService;

    @Override
    public Flux<String> chatToGenCode(Long appId, String message, User loginUser) {
        // 1. 校验参数
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用参数错误");
        ThrowUtils.throwIf(StrUtil.isEmpty(message), ErrorCode.PARAMS_ERROR, "用户提示词为空");

        // 2. 查询应用信息
        App app = this.getById(appId);
        if (app == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        }

        // 3. 验证当前用户是否有权限访问该应用
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限访问该应用");
        }

        // 4. 获取应用的代码生成类型
        String codeGenType = app.getCodeGenType();
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenType);
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的应用类型");
        }

        // 5. 通过校验后，添加用户消息到消息历史
        chatHistoryService.addChatHistory(appId, message, ChatHistoryMessageTypeEnum.USER.getValue(), loginUser.getId());

        // 6. 调用 AI 生成应用代码
        Flux<String> contentStream = aiCodeGeneratorFacade.generateAndSaveCodeStream(message, codeGenTypeEnum, appId);

        return streamHandlerExecutor.doExecute(contentStream, chatHistoryService, appId, loginUser, codeGenTypeEnum);
    }

    @Override
    public String deployApp(Long appId, User loginUser) {
        // 1. 参数校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 错误");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");

        // 2. 查询应用信息
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.PARAMS_ERROR, "应用不存在");

        // 3. 验证用户是否有权限部署该应用（仅本人可部署）
        ThrowUtils.throwIf(!app.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR, "无权限部署该应用");

        // 4. 检查是否存在deployKey
        String deployKey = app.getDeployKey();
        if (StrUtil.isBlank(deployKey)) {
            deployKey = RandomUtil.randomString(6);
        }

        // 5. 获取生成代码类型，构造源目录路径
        String codeGenType = app.getCodeGenType();
        String sourceDirName = String.format("%s_%s", codeGenType, appId);
        String sourceDirPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + sourceDirName;

        // 6. 检查源目录是否存在
        File sourceFile = new File(sourceDirPath);
        ThrowUtils.throwIf(!sourceFile.exists() || !sourceFile.isDirectory(), ErrorCode.SYSTEM_ERROR, "应用不存在，请先生成应用");

        // 7. Vue 项目特殊处理：执行构建
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenType);
        if (codeGenTypeEnum == CodeGenTypeEnum.VUE_PROJECT) {
            boolean buildSuccess = vueProjectBuilder.buildProject(sourceDirPath);
            ThrowUtils.throwIf(!buildSuccess, ErrorCode.SYSTEM_ERROR, "Vue 项目构建失败，请检查代码和依赖");
            // 检查 dist 目录是否存在
            File distDir = new File(sourceFile, "dist");
            ThrowUtils.throwIf(!distDir.exists(), ErrorCode.SYSTEM_ERROR, "Vue 项目构建完成但未生成 dist 目录");
            // 将 dist 目录作为部署源
            sourceFile = distDir;
            log.info("Vue 项目构建成功，将部署 dist 目录：{}", distDir.getAbsolutePath());
        }

        // 7. 复制文件到部署目录
        String deployDirPath = AppConstant.CODE_DEPLOY_ROOT_DIR + File.separator + deployKey;
        try {
            FileUtil.copyContent(sourceFile, new File(deployDirPath), true);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "部署失败：" + e.getMessage());
        }

        // 8. 更新应用的部署信息
        App updateApp = new App();
        updateApp.setId(appId);
        updateApp.setDeployKey(deployKey);
        updateApp.setDeployedTime(LocalDateTime.now());
        boolean updateResult = this.updateById(updateApp);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "更新应用部署信息失败");
        String deployUrl = String.format("%s/%s/", AppConstant.CODE_DEPLOY_HOST, deployKey);
        generateAppScreenshotAsync(appId, deployUrl);
        return deployUrl;
    }

    /**
     * 异步生成应用封面截图
     * @param appId 应用 id
     * @param appUrl 网页 URL
     */
    @Override
    public void generateAppScreenshotAsync(Long appId, String appUrl) {
        Thread.startVirtualThread(() -> {
            String coverUrl = screenshotService.generateAndSaveScreenshot(appUrl);
            App updateApp = new App();
            updateApp.setId(appId);
            updateApp.setCover(coverUrl);
            boolean updateResult = this.updateById(updateApp);
            ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "更新应用封面字段失败");
        });
    }

    @Override
    public AppVO getAppVO(App app) {
        if (app == null) {
            return null;
        }
        AppVO appVO = new AppVO();
        BeanUtil.copyProperties(app, appVO);
        // 关联查询用户信息
        Long userId = app.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            appVO.setUser(userVO);
        }
        return appVO;
    }

    @Override
    public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
        String cover = appQueryRequest.getCover();
        String initPrompt = appQueryRequest.getInitPrompt();
        String codeGenType = appQueryRequest.getCodeGenType();
        String deployKey = appQueryRequest.getDeployKey();
        Integer priority = appQueryRequest.getPriority();
        Long userId = appQueryRequest.getUserId();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .like("appName", appName)
                .like("cover", cover)
                .like("initPrompt", initPrompt)
                .eq("codeGenType", codeGenType)
                .eq("deployKey", deployKey)
                .eq("priority", priority)
                .eq("userId", userId)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }

    @Override
    public List<AppVO> getAppVOList(List<App> appList) {
        if (CollUtil.isEmpty(appList)) {
            return new ArrayList<>();
        }
        // 批量获取用户信息，避免 N+1 查询问题
        Set<Long> userIds = appList.stream()
                .map(App::getUserId)
                .collect(Collectors.toSet());
        Map<Long, UserVO> userVOMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, userService::getUserVO));
        return appList.stream().map(app -> {
            AppVO appVO = getAppVO(app);
            UserVO userVO = userVOMap.get(app.getUserId());
            appVO.setUser(userVO);
            return appVO;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean removeById(Serializable id) {
        if (id == null) {
            return false;
        }

        long appId = Long.parseLong(id.toString());
        if (appId <= 0) {
            return false;
        }

        try {
            chatHistoryService.deleteByAppId(appId);
        } catch (Exception e) {
            log.error("删除应用关联对话历史失败：{}", e.getMessage());
        }

        return super.removeById(id);
    }
}
