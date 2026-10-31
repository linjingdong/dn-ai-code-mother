package com.dn.dnaicodemotherbackend.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.dn.dnaicodemotherbackend.exception.ErrorCode;
import com.dn.dnaicodemotherbackend.exception.ThrowUtils;
import com.dn.dnaicodemotherbackend.manage.CosManage;
import com.dn.dnaicodemotherbackend.service.ScreenshotService;
import com.dn.dnaicodemotherbackend.utils.WebScreenshotUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/10/31 02:33
 **/
@Service
@Slf4j
public class ScreenshotServiceImpl implements ScreenshotService {

    @Resource
    private CosManage cosManage;

    @Override
    public String generateAndSaveScreenshot(String webUrl) {
        ThrowUtils.throwIf(StrUtil.isBlank(webUrl), ErrorCode.PARAMS_ERROR, "网页地址不能为空");
        log.info("开始生成网页截图，URL：{}", webUrl);

        // 1. 生成本地截图
        String localScreenshotPath = WebScreenshotUtils.saveWebPageScreenshot(webUrl);
        ThrowUtils.throwIf(StrUtil.isBlank(localScreenshotPath), ErrorCode.SYSTEM_ERROR, "本地截图生成失败");

        try {
            // 2. 上传到对象存储
            String cosUrl = uploadScreenshotToCos(localScreenshotPath);
            ThrowUtils.throwIf(StrUtil.isBlank(cosUrl), ErrorCode.SYSTEM_ERROR, "本地截图上传失败");
            log.info("本地截图上传成功：{} -> {}", webUrl, cosUrl);
            return cosUrl;
        } finally {
            // 3. 清理本地文件
            cleanUpLocalFile(localScreenshotPath);
        }
    }

    /**
     * 上传本地截图到 COS
     * @param localScreenshotPath 本地截图路径
     * @return 截图访问地址
     */
    private String uploadScreenshotToCos(String localScreenshotPath) {
        if (StrUtil.isBlank(localScreenshotPath)) {
            return null;
        }

        File screenshotFile = FileUtil.file(localScreenshotPath);
        if (!screenshotFile.exists()) {
            log.error("截图文件不存在：{}", localScreenshotPath);
        }

        String fileName = UUID.randomUUID().toString().substring(0, 8) + "_compressed.jpg";
        String cosKey = generateScreenshotKey(fileName);
        return cosManage.uploadFile(cosKey, FileUtil.file(localScreenshotPath));
    }

    /**
     * 生成截图的对象存储键
     * 格式：/screenshots/年/日/月/文件名称.jpg
     * @param fileName 文件名称
     * @return 对象存储键
     */
    private String generateScreenshotKey(String fileName) {
        String dataPath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return String.format("/screenshot/%s/%s", dataPath, fileName);
    }

    /**
     * 清理本地截图文件
     * @param localFilePath 本地截图路径
     */
    private void cleanUpLocalFile(String localFilePath) {
        File localFile = new File(localFilePath);
        if (localFile.exists()) {
            File parentDir = localFile.getParentFile();
            FileUtil.del(parentDir);
            log.info("本地截图文件已清理：{}", localFilePath);
        }
    }
}