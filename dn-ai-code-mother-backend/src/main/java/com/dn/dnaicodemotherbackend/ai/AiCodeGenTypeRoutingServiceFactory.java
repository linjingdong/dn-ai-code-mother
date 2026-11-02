package com.dn.dnaicodemotherbackend.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * AI 代码生成类型路由服务工厂
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/11/2 03:11
 **/

@Slf4j
@Component
public class AiCodeGenTypeRoutingServiceFactory {

    @Resource
    private ChatModel chatModel;

    /**
     * 创建 AI 代码生成类型路由服务
     * @return AI 服务
     */
    @Bean
    public AiCodeGenTypeRoutingService aiCodeGenTypeRoutingService() {
        return AiServices.builder(AiCodeGenTypeRoutingService.class)
                .chatModel(chatModel)
                .build();
    }
}
