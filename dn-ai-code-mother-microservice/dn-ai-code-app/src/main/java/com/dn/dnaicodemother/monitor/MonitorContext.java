package com.dn.dnaicodemother.monitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 定义监听器中获取的业务维度上下文
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/11/9 23:40
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitorContext implements Serializable {

    @Serial
    private static final long serialVersionUID = -5585935872241691751L;

    private String userId;

    private String appId;
}
