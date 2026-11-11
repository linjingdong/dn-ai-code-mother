package com.dn.dnaicodemother.service.impl;

import com.dn.dnaicodemother.innerservice.InnerScreenshotService;
import com.dn.dnaicodemother.service.ScreenshotService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/11/11 23:01
 **/
@DubboService
public class InnerScreenshotServiceImpl implements InnerScreenshotService {

    @Resource
    private ScreenshotService screenshotService;

    @Override
    public String generateAndUploadScreenshot(String webUrl) {
        return screenshotService.generateAndSaveScreenshot(webUrl);
    }
}
