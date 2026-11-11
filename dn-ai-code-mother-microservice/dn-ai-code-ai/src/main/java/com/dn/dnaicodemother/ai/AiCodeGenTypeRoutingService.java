package com.dn.dnaicodemother.ai;

import com.dn.dnaicodemother.model.enums.CodeGenTypeEnum;
import dev.langchain4j.service.SystemMessage;

/**
 * AI 代码生成类型智能路由服务
 * 使用结构化输出直接返回枚举类型
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/11/2 03:08
 **/

public interface AiCodeGenTypeRoutingService {

    /**
     * 根据用户需求智能选择代码类型
     * @param userPrompt 用户提示词
     * @return 代码生成类型枚举
     */
    @SystemMessage(fromResource = "prompt/codegen-routing-system-prompt.txt")
    CodeGenTypeEnum routeCodeGenType(String userPrompt);
}
