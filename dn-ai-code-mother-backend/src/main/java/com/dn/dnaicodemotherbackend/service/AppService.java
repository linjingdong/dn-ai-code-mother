package com.dn.dnaicodemotherbackend.service;

import com.dn.dnaicodemotherbackend.model.dto.app.AppQueryRequest;
import com.dn.dnaicodemotherbackend.model.entity.App;
import com.dn.dnaicodemotherbackend.model.entity.User;
import com.dn.dnaicodemotherbackend.model.vo.AppVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author Lin_jd
 */
public interface AppService extends IService<App> {

    /**
     * 通过对话生成应用代码
     * @param appId 应用ID
     * @param message 用户提示词
     * @param loginUser 登录用户
     * @return 流式响应
     */
    Flux<String> chatToGenCode(Long appId, String message, User loginUser);

    /**
     * 应用部署
     * @param appId 应用 ID
     * @param loginUser 登录用户
     * @return 部署 URL
     */
    String deployApp(Long appId, User loginUser);

    /**
     * 应用脱敏信息
     * @param app 应用
     * @return 脱敏信息
     */
     AppVO getAppVO(App app);

    /**
     * 根据查询参数构造查询条件
     * @param appQueryRequest 查询参数
     * @return 查询条件
     */
    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    /**
     * 应用列表脱敏数据
     * @param appList 应用列表
     * @return 应用列表脱敏数据
     */
    List<AppVO> getAppVOList(List<App> appList);
}
