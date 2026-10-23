package com.dn.dnaicodemotherbackend.ai;

import com.dn.dnaicodemotherbackend.ai.model.HtmlCodeResult;
import com.dn.dnaicodemotherbackend.ai.model.MultiFileCodeResult;
import dev.langchain4j.service.SystemMessage;

/**
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/10/23 14:16
 **/
public interface AiCodeGeneratorService {

    /**
     * 生成 HTML 代码
     * @param usePrompt 用户提示词
     * @return 响应
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    HtmlCodeResult generateHtmlCode(String usePrompt);

    /**
     * 生成多文件代码
     * @param usePrompt 用户提示词
     * @return 响应
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    MultiFileCodeResult generateMultiFileCode(String usePrompt);
}
