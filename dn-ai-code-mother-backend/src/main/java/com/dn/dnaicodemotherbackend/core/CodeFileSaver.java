package com.dn.dnaicodemotherbackend.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.dn.dnaicodemotherbackend.ai.model.HtmlCodeResult;
import com.dn.dnaicodemotherbackend.ai.model.MultiFileCodeResult;
import com.dn.dnaicodemotherbackend.model.enums.CodeGenTypeEnum;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/10/23 16:09
 **/

@Service
public class CodeFileSaver {
    // 文件根目录
    private static final String FILE_SAVE_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_output";

    /**
     * 保存 HtmlCodeResult
     * @param htmlCodeResult 代码生成结果
     * @return 文件
     */
    public static File saveHtmlCodeResult(HtmlCodeResult htmlCodeResult) {
        String baseDirPath = buildUniqueDir(CodeGenTypeEnum.HTML.getValue());
        writeToFile(baseDirPath, "index.html", htmlCodeResult.getHtmlCode());
        return new File(baseDirPath);
    }

    /**
     * 保存 MultiFileCodeResult
     * @param multiFileCodeResult 代码生成结果
     * @return 文件
     */
    public static File saveMultiHtmlCodeResult(MultiFileCodeResult multiFileCodeResult) {
        String baseDirPath = buildUniqueDir(CodeGenTypeEnum.MULTI_FILE.getValue());
        writeToFile(baseDirPath, "index.html", multiFileCodeResult.getHtmlCode());
        writeToFile(baseDirPath, "style.css", multiFileCodeResult.getCssCode());
        writeToFile(baseDirPath, "script.js", multiFileCodeResult.getJsCode());
        return new File(baseDirPath);
    }

    /**
     * 构建唯一目录路径，构建方式：tmp/code_output/{bizType}_{雪花ID}
     * @param bizType 生成代码类型
     * @return 唯一路径
     */
    private static String buildUniqueDir(String bizType) {
        String uniqueDirName = String.format("%s_%s", bizType, IdUtil.getSnowflakeNextId());
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
    private static void writeToFile(String dirPath, String filename, String content) {
        String filePath = dirPath + File.separator + filename;
        FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
    }
}
