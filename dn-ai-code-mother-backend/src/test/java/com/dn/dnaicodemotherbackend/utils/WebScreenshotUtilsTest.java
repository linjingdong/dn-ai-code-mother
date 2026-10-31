package com.dn.dnaicodemotherbackend.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/10/31 02:04
 **/
@Slf4j
@SpringBootTest
public class WebScreenshotUtilsTest {

    @Test
    void saveWebPageScreenshot() {
        String testUrl = "https://www.codefather.cn";
        String webPageScreenshot = WebScreenshotUtils.saveWebPageScreenshot(testUrl);
        Assertions.assertNotNull(webPageScreenshot);
    }
}
