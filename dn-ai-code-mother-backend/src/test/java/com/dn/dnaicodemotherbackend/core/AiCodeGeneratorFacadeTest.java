package com.dn.dnaicodemotherbackend.core;

import com.dn.dnaicodemotherbackend.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

/**
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/10/23 16:39
 **/
@SpringBootTest
class AiCodeGeneratorFacadeTest {

    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Test
    void generateAndSaveCode() {
        File file = aiCodeGeneratorFacade.generateAndSaveCode("生成一个登录界面，不超过 50 行", CodeGenTypeEnum.MULTI_FILE);
        Assertions.assertNotNull(file);
    }
}