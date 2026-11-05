package com.dn.dnaicodemotherbackend.ai;

import com.dn.dnaicodemotherbackend.langgragh4j.utils.SpringContextUtil;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
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

    /**
     * 创建 AI 代码生成类型路由服务实例
     * @return AI 代码生成类型路由服务
     */
    public AiCodeGenTypeRoutingService createAiCodeGenTypeRoutingService() {
        ChatModel routingChatModel = SpringContextUtil.getBean("routingChatModelPrototype", ChatModel.class);
        return AiServices.builder(AiCodeGenTypeRoutingService.class)
                .chatModel(routingChatModel)
                .build();
    }

    /**
     * 创建 AI 代码生成类型路由服务
     * @return AI 服务
     */
    @Bean
    public AiCodeGenTypeRoutingService aiCodeGenTypeRoutingService() {
        return createAiCodeGenTypeRoutingService();
    }
}
