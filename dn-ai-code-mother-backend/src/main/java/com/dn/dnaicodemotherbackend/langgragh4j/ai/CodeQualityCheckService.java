package com.dn.dnaicodemotherbackend.langgragh4j.ai;

import com.dn.dnaicodemotherbackend.langgragh4j.model.QualityResult;
import com.mybatisflex.core.query.QueryWrapper;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * AI 检查代码质量服务
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/11/4 03:23
 **/
public interface CodeQualityCheckService {

    /**
     * AI 检查代码质量
     * @param userPrompt 用户提示词
     * @return 检查结果
     */
    @SystemMessage(fromResource = "prompt/code-quality-check-system-prompt.txt")
    QualityResult codeQualityCheck(@UserMessage String userPrompt);
}
