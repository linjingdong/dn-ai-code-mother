package com.dn.dnaicodemotherbackend.langgragh4j.ai;

import com.dn.dnaicodemotherbackend.langgragh4j.model.ImageCollectionPlan;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/11/5 21:58
 **/
public interface ImageCollectionPlanService {
    /**
     * 根据提示词分析需要收集的图片类型和参数
     */
    @SystemMessage(fromResource = "prompt/image-collection-plan-system-prompt")
    ImageCollectionPlan planImageCollection(@UserMessage String userPrompt);
}
