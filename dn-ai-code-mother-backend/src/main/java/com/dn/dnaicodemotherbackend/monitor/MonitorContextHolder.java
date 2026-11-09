package com.dn.dnaicodemotherbackend.monitor;

import lombok.extern.slf4j.Slf4j;

/**
 * 定义在 ThreadLocal 的监听器上下文持有者
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/11/9 23:42
 **/
@Slf4j
public class MonitorContextHolder {

    private static final ThreadLocal<MonitorContext> CONTEXT_HOLDER = new ThreadLocal<>();

    public static MonitorContext getContext() {
        return CONTEXT_HOLDER.get();
    }

    public static void setContext(MonitorContext context) {
        CONTEXT_HOLDER.set(context);
    }

    public static void clearContext() {
        CONTEXT_HOLDER.remove();
    }
}
