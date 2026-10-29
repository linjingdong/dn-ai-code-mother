package com.dn.dnaicodemotherbackend.ai;

import com.dn.dnaicodemotherbackend.ai.model.HtmlCodeResult;
import com.dn.dnaicodemotherbackend.ai.model.MultiFileCodeResult;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

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

    /**
     * 流式生成 HTML 代码
     * @param usePrompt 用户提示词
     * @return 响应
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    Flux<String> generateHtmlCodeStream(String usePrompt);

    /**
     * 流式生成多文件代码
     * @param usePrompt 用户提示词
     * @return 响应
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    Flux<String> generateMultiFileCodeStream(String usePrompt);

    /**
     * 流式生成 vue 项目代码
     * @param appId 应用 ID
     * @param userPrompt 用户提示词
     * @return 流式响应
     */
    @SystemMessage(fromResource = "prompt/codegen-vue-project-system-prompt.txt")
    Flux<String> generateVueProjectCode(@MemoryId long appId, @UserMessage String userPrompt);
}
