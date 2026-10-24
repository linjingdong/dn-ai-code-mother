package com.dn.dnaicodemotherbackend.core.saver;

import cn.hutool.core.io.FileUtil;
import com.dn.dnaicodemotherbackend.constant.AppConstant;
import com.dn.dnaicodemotherbackend.exception.BusinessException;
import com.dn.dnaicodemotherbackend.exception.ErrorCode;
import com.dn.dnaicodemotherbackend.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 抽象代码保存期 - 模板方法模式
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/10/23 22:07
 **/
public abstract class CodeFileSaverTemplate<T> {
    // 文件保存根目录
    protected static final String FILE_SAVE_ROOT_DIR = AppConstant.CODE_OUTPUT_ROOT_DIR;

    public final File saveCode(T result, Long appId) {
        // 1. 校验输入
        validateInput(result);
        // 2. 构建唯一目录
        String baseDirPath = buildUniqueDir(appId);
        // 3. 保存文件（具体由子类实现）
        saveFile(result, baseDirPath);
        // 4. 返回目录文件对象
        return new File(baseDirPath);
    }

    /**
     * 校验输入参数
     * @param result 代码结果对象
     */
    protected void validateInput(T result) {
        if (result == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码结果不能为空");
        }
    }

    /**
     * 获取生成代码类型
     * @return 生成代码类型
     */
    protected abstract CodeGenTypeEnum getCodeType();

    /**
     * 保存文件
     * @param result 代码结果对象
     * @param baseDirPath 基础目录路径
     */
    protected abstract void saveFile(T result, String baseDirPath);

    /**
     * 构建唯一目录路径
     * @param appId 应用ID
     * @return 唯一路径
     */
    protected final String buildUniqueDir(Long appId) {
        String codeType = getCodeType().getValue();
        String uniqueDirName = String.format("%s_%s", codeType, appId);
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 写入单个文件
     * @param dirPath 文件路径
     * @param filename 文件名称
     * @param content 内容
     */
    protected static void writeToFile(String dirPath, String filename, String content) {
        String filePath = dirPath + File.separator + filename;
        FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
    }
}
