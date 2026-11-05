package com.dn.dnaicodemotherbackend.langgragh4j.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 代码质量检查 AI 服务工厂类
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/11/4 03:26
 **/

@Slf4j
@Configuration
public class CodeQualityCheckServiceFactory {

    @Resource(name = "openAiChatModel")
    private ChatModel chatModel;

    @Bean
    CodeQualityCheckService codeQualityCheckService() {
        return AiServices.builder(CodeQualityCheckService.class)
                .chatModel(chatModel)
                .build();
    }
}
