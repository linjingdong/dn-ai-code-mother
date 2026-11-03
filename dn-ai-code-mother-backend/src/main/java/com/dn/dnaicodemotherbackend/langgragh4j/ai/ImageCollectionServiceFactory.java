package com.dn.dnaicodemotherbackend.langgragh4j.ai;

import com.dn.dnaicodemotherbackend.langgragh4j.tools.ImageSearchTool;
import com.dn.dnaicodemotherbackend.langgragh4j.tools.LogoGeneratorTool;
import com.dn.dnaicodemotherbackend.langgragh4j.tools.MermaidDiagramTool;
import com.dn.dnaicodemotherbackend.langgragh4j.tools.UndrawIllustrationTool;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/11/4 02:16
 **/
@Component
public class ImageCollectionServiceFactory {

    @Resource
    private ChatModel chatModel;

    @Resource
    private ImageSearchTool imageSearchTool;

    @Resource
    private LogoGeneratorTool logoGeneratorTool;

    @Resource
    private MermaidDiagramTool mermaidDiagramTool;

    @Resource
    private UndrawIllustrationTool undrawIllustrationTool;

    @Bean
    public ImageCollectionService imageCollectionService() {
        return AiServices.builder(ImageCollectionService.class)
                .chatModel(chatModel)
                .tools(imageSearchTool, logoGeneratorTool, mermaidDiagramTool, undrawIllustrationTool)
                .build();
    }
}
