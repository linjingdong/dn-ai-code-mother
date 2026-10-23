package com.dn.dnaicodemotherbackend.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/10/23 14:23
 **/
@Configuration
public class AiCodeGeneratorServiceFactory {
    @Resource
    private ChatModel chatModel;

    @Bean
    public AiCodeGeneratorService aiCodeGeneratorService() {
        return AiServices.create(AiCodeGeneratorService.class, chatModel);
    }
}
