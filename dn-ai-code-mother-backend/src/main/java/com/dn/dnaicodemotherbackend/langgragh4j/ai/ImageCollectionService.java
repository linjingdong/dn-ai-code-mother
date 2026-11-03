package com.dn.dnaicodemotherbackend.langgragh4j.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * 图片收集 AI 服务接口
 * 使用 AI 调用工具收集不同类型的图片资源
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/11/4 02:12
 **/

public interface ImageCollectionService {

    /**
     * 根据用户的需求收集所需的图片资源
     * @param userPrompt
     * @return
     */
    @SystemMessage(fromResource = "prompt/image-collection-system-prompt.txt")
    String collectImages(@UserMessage String userPrompt);
}
