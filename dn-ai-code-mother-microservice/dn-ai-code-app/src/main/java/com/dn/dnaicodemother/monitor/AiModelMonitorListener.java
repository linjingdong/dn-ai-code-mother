package com.dn.dnaicodemother.monitor;

import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import dev.langchain4j.model.output.TokenUsage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

/**
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/11/9 23:56
 **/
@Slf4j
@Component
public class AiModelMonitorListener implements ChatModelListener {

    public static final String REQUEST_START_TIME_KEY = "request_start_time";

    public static final String MONITOR_CONTEXT_KEY = "monitor_context";

    @Resource
    private AiModelMetricsCollector metricsCollector;

    @Override
    public void onRequest(ChatModelRequestContext requestContext) {
        requestContext.attributes().put(REQUEST_START_TIME_KEY, Instant.now());
        MonitorContext context = MonitorContextHolder.getContext();
        String userId = context.getUserId();
        String appId = context.getAppId();
        requestContext.attributes().put(MONITOR_CONTEXT_KEY, context);
        String modelName = requestContext.chatRequest().modelName();

        metricsCollector.recordRequest(userId, appId, modelName, "started");
    }

    @Override
    public void onResponse(ChatModelResponseContext responseContext) {
        Map<Object, Object> attributes = responseContext.attributes();
        MonitorContext monitorContext = (MonitorContext) attributes.get(MONITOR_CONTEXT_KEY);
        String userId = monitorContext.getUserId();
        String appId = monitorContext.getAppId();
        String modelName = responseContext.chatResponse().modelName();
        metricsCollector.recordRequest(userId, appId, modelName, "success");
        recordResponseTime(attributes, userId, appId, modelName);
        recordTokenUsage(responseContext, userId, appId, modelName);
    }

    @Override
    public void onError(ChatModelErrorContext errorContext) {
        Map<Object, Object> attributes = errorContext.attributes();
        MonitorContext context = (MonitorContext) attributes.get(MONITOR_CONTEXT_KEY);
        String userId = context.getUserId();
        String appId = context.getAppId();
        String modelName = errorContext.chatRequest().modelName();
        String errorMessage = errorContext.error().getMessage();
        metricsCollector.recordRequest(userId, appId, modelName, "error");
        metricsCollector.recordError(userId, appId, modelName, errorMessage);
        recordResponseTime(attributes, userId, appId, modelName);
    }

    /**
     * 记录响应时间
     * @param attributes
     * @param userId
     * @param appId
     * @param modelName
     */
    private void recordResponseTime(Map<Object, Object> attributes, String userId, String appId, String modelName) {
        Instant start_time = (Instant) attributes.get(REQUEST_START_TIME_KEY);
        Duration responseTime = Duration.between(start_time, Instant.now());
        metricsCollector.recordResponseTime(userId, appId, modelName, responseTime);
    }

    /**
     * 记录 Token 使用量
     * @param responseContext
     * @param userId
     * @param appId
     * @param modelName
     */
    private void recordTokenUsage(ChatModelResponseContext responseContext, String userId, String appId, String modelName) {
        TokenUsage tokenUsage = responseContext.chatResponse().tokenUsage();
        if (null != tokenUsage) {
            metricsCollector.recordTokenUsage(userId, appId, modelName, "input", tokenUsage.inputTokenCount());
            metricsCollector.recordTokenUsage(userId, appId, modelName, "output", tokenUsage.outputTokenCount());
            metricsCollector.recordTokenUsage(userId, appId, modelName, "total", tokenUsage.totalTokenCount());
        }
    }

}
