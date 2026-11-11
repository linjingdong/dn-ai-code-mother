package com.dn.dnaicodemother.core;

import cn.hutool.json.JSONUtil;

import com.dn.dnaicodemother.ai.AiCodeGeneratorService;
import com.dn.dnaicodemother.ai.AiCodeGeneratorServiceFactory;
import com.dn.dnaicodemother.ai.model.HtmlCodeResult;
import com.dn.dnaicodemother.ai.model.MultiFileCodeResult;
import com.dn.dnaicodemother.ai.model.message.AiResponseMessage;
import com.dn.dnaicodemother.ai.model.message.ToolExecutedMessage;
import com.dn.dnaicodemother.ai.model.message.ToolRequestMessage;
import com.dn.dnaicodemother.constant.AppConstant;
import com.dn.dnaicodemother.core.builder.VueProjectBuilder;
import com.dn.dnaicodemother.core.parser.CodeParserExecutor;
import com.dn.dnaicodemother.core.saver.CodeFileSaverExecutor;
import com.dn.dnaicodemother.exception.BusinessException;
import com.dn.dnaicodemother.exception.ErrorCode;
import com.dn.dnaicodemother.model.enums.CodeGenTypeEnum;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.TokenStream;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * AI 代码生成门面类，组合生成和保存功能
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/10/23 16:30
 **/
@Service
@Slf4j
public class AiCodeGeneratorFacade {

    @Resource
    private AiCodeGeneratorServiceFactory aiCodeGeneratorServiceFactory;

    @Resource
    private VueProjectBuilder vueProjectBuilder;

    /**
     * 根据类型生成和保存应用代码
     * @param userMessage 用户提示词
     * @param codeGenType 生成代码类型
     * @param appId 应用ID
     * @return 保存的文件目录
     */
    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenType, Long appId) {
        if (codeGenType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }

        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId, codeGenType);

        return switch (codeGenType) {
            case HTML -> {
                HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(userMessage);
                yield CodeFileSaverExecutor.executeSave(htmlCodeResult, codeGenType, appId);
            }
            case MULTI_FILE -> {
                MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generateMultiFileCode(userMessage);
                yield CodeFileSaverExecutor.executeSave(multiFileCodeResult, codeGenType, appId);
            }
            default -> {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, String.format("不支持的生成类型：%s", codeGenType.getValue()));
            }
        };
    }

    /**
     * 根据类型生成和保存应用代码（流式）
     * @param userMessage 用户提示词
     * @param codeGenType 生成代码类型
     * @param appId 应用ID
     * @return 流式响应
     */
    public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenType, Long appId) {
        if (codeGenType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }

        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId, codeGenType);

        return switch (codeGenType) {
            case HTML -> {
                Flux<String> codeStream = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
                yield processCodeStream(codeStream, codeGenType, appId);
            }
            case MULTI_FILE -> {
                Flux<String> codeStream = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
                yield processCodeStream(codeStream, codeGenType, appId);
            }
            case VUE_PROJECT -> {
                TokenStream codeStream = aiCodeGeneratorService.generateVueProjectCode(appId, userMessage);
                yield processTokenStream(codeStream, appId);
            }
            default -> {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, String.format("不支持的生成类型：%s", codeGenType.getValue()));
            }
        };
    }

    /**
     * 将 TokenStream 转换为 Flux<String>, 并传递工具调用信息
     * @param tokenStream TokenStream 对象
     * @return Flux<String> 对象
     */
    private Flux<String> processTokenStream(TokenStream tokenStream, Long appId) {
        return Flux.create(sink -> {
                    tokenStream
                            .onPartialResponse((String partialResponse) -> {
                                AiResponseMessage aiResponseMessage = new AiResponseMessage(partialResponse);
                                sink.next(JSONUtil.toJsonStr(aiResponseMessage));
                            })
                            .onPartialToolExecutionRequest((index, toolExecutionRequest) -> {
                                ToolRequestMessage toolRequestMessage = new ToolRequestMessage(toolExecutionRequest);
                                sink.next(JSONUtil.toJsonStr(toolRequestMessage));
                            })
                            .onToolExecuted(toolExecution -> {
                                ToolExecutedMessage toolExecutedMessage = new ToolExecutedMessage(toolExecution);
                                sink.next(JSONUtil.toJsonStr(toolExecutedMessage));
                            })
                            .onCompleteResponse((ChatResponse response) -> {
                                String projectPath = AppConstant.CODE_OUTPUT_ROOT_DIR + "/vue_project_" + appId;
                                vueProjectBuilder.buildProjectAsync(projectPath);
                                sink.complete();
                            })
                            .onError((Throwable error) -> {
                                error.printStackTrace();
                                sink.error(error);
                            })
                            .start();
                }
        );
    }

    /**
     * 通用流式代码处理方法
     * @param codeStream 代码流
     * @param codeGenTypeEnum 生成代码类型
     * @param appId 应用ID
     * @return 流式响应
     */
    private Flux<String> processCodeStream(Flux<String> codeStream, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        StringBuilder stringBuilder = new StringBuilder();
        return codeStream
                .doOnNext(chuck -> {
                    stringBuilder.append(chuck);
                })
                .doOnComplete(() -> {
                    try {
                        String completeContent = stringBuilder.toString();
                        // 使用执行器转换
                        Object resultObj = CodeParserExecutor.executeParser(completeContent, codeGenTypeEnum);
                        // 使用执行器保存
                        File file = CodeFileSaverExecutor.executeSave(resultObj, codeGenTypeEnum, appId);
                        log.info("保存成功，路径为：{}", file.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("保存失败：{}", e.getMessage());
                    }
                });
    }
}
