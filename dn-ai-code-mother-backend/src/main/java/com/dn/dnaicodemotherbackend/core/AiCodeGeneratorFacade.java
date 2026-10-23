package com.dn.dnaicodemotherbackend.core;

import com.dn.dnaicodemotherbackend.ai.AiCodeGeneratorService;
import com.dn.dnaicodemotherbackend.ai.model.HtmlCodeResult;
import com.dn.dnaicodemotherbackend.ai.model.MultiFileCodeResult;
import com.dn.dnaicodemotherbackend.exception.BusinessException;
import com.dn.dnaicodemotherbackend.exception.ErrorCode;
import com.dn.dnaicodemotherbackend.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * AI 代码生成门面类，组合生成和保存功能
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/10/23 16:30
 **/
@Service
public class AiCodeGeneratorFacade {

    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;

    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenType) {
        if (codeGenType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }

        return switch (codeGenType) {
            case HTML -> generateAndSaveHtmlCode(userMessage);
            case MULTI_FILE -> generateAndSaveMultiFIleCode(userMessage);
            default -> {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, String.format("不支持的生成类型：%s", codeGenType.getValue()));
            }
        };
    }

    /**
     * 生成并保存 HTML 代码
     * @param userMessage 用户提示词
     * @return 保存的目录
     */
    private File generateAndSaveHtmlCode(String userMessage) {
        HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(userMessage);
        return CodeFileSaver.saveHtmlCodeResult(htmlCodeResult);
    }

    /**
     * 生成并保存多文件代码
     * @param userMessage 用户提示词
     * @return 保存的目录
     */
    private File generateAndSaveMultiFIleCode(String userMessage) {
        MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generateMultiFileCode(userMessage);
        return CodeFileSaver.saveMultiHtmlCodeResult(multiFileCodeResult);
    }

}
