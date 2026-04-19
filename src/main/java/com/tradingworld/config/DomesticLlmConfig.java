package com.tradingworld.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * 扩展LLM提供商的配置。
 * 支持：MiniMax、DeepSeek、Qwen、ERNIE、GLM、豆包、xAI、OpenRouter、Azure。
 * 所有提供商都使用OpenAI兼容的API。
 */
@Configuration
public class DomesticLlmConfig {

    private static final Logger log = LoggerFactory.getLogger(DomesticLlmConfig.class);

    // MiniMax
    @Value("${langchain4j.minimax.api-key:}")
    private String minimaxApiKey;

    @Value("${langchain4j.minimax.base-url:https://api.minimaxi.chat/v1}")
    private String minimaxBaseUrl;

    @Value("${langchain4j.minimax.model-name:minimax-01}")
    private String minimaxModelName;

    // DeepSeek
    @Value("${langchain4j.deepseek.api-key:}")
    private String deepseekApiKey;

    @Value("${langchain4j.deepseek.base-url:https://api.deepseek.com/v1}")
    private String deepseekBaseUrl;

    @Value("${langchain4j.deepseek.model-name:deepseek-chat}")
    private String deepseekModelName;

    // Qwen (阿里巴巴)
    @Value("${langchain4j.qwen.api-key:}")
    private String qwenApiKey;

    @Value("${langchain4j.qwen.base-url:https://dashscope.aliyuncs.com/compatible-mode/v1}")
    private String qwenBaseUrl;

    @Value("${langchain4j.qwen.model-name:qwen-max}")
    private String qwenModelName;

    // ERNIE (百度)
    @Value("${langchain4j.ernie.api-key:}")
    private String ernieApiKey;

    @Value("${langchain4j.ernie.base-url:https://qianfan.baidubce.com/v2}")
    private String ernieBaseUrl;

    @Value("${langchain4j.ernie.model-name:ernie-4.0-8k-latest}")
    private String ernieModelName;

    // GLM (智谱)
    @Value("${langchain4j.glm.api-key:}")
    private String glmApiKey;

    @Value("${langchain4j.glm.base-url:https://open.bigmodel.cn/api/paas/v4}")
    private String glmBaseUrl;

    @Value("${langchain4j.glm.model-name:glm-4}")
    private String glmModelName;

    // 豆包 (字节跳动)
    @Value("${langchain4j.doubao.api-key:}")
    private String doubaoApiKey;

    @Value("${langchain4j.doubao.base-url:https://ark.cn-beijing.volces.com/api/v3}")
    private String doubaoBaseUrl;

    @Value("${langchain4j.doubao.model-name:doubao-pro-32k}")
    private String doubaoModelName;

    // xAI (Grok)
    @Value("${langchain4j.xai.api-key:}")
    private String xaiApiKey;

    @Value("${langchain4j.xai.base-url:https://api.x.ai/v1}")
    private String xaiBaseUrl;

    @Value("${langchain4j.xai.model-name:grok-4}")
    private String xaiModelName;

    // OpenRouter
    @Value("${langchain4j.openrouter.api-key:}")
    private String openrouterApiKey;

    @Value("${langchain4j.openrouter.base-url:https://openrouter.ai/api/v1}")
    private String openrouterBaseUrl;

    @Value("${langchain4j.openrouter.model-name:openai/gpt-4o}")
    private String openrouterModelName;

    // Azure OpenAI
    @Value("${langchain4j.azure.api-key:}")
    private String azureApiKey;

    @Value("${langchain4j.azure.base-url:}")
    private String azureBaseUrl;

    @Value("${langchain4j.azure.model-name:}")
    private String azureModelName;

    /**
     * 为指定的国内提供商创建ChatModel。
     */
    public ChatModel createDomesticChatModel(String modelName, String provider) {
        log.info("Creating domestic ChatModel for provider: {}, model: {}", provider, modelName);

        return switch (provider.toLowerCase()) {
            case "minimax" -> OpenAiChatModel.builder()
                    .apiKey(minimaxApiKey)
                    .baseUrl(minimaxBaseUrl)
                    .modelName(modelName != null ? modelName : minimaxModelName)
                    .temperature(0.7)
                    .timeout(Duration.ofSeconds(60))
                    .build();
            case "deepseek" -> OpenAiChatModel.builder()
                    .apiKey(deepseekApiKey)
                    .baseUrl(deepseekBaseUrl)
                    .modelName(modelName != null ? modelName : deepseekModelName)
                    .temperature(0.7)
                    .timeout(Duration.ofSeconds(60))
                    .build();
            case "qwen" -> OpenAiChatModel.builder()
                    .apiKey(qwenApiKey)
                    .baseUrl(qwenBaseUrl)
                    .modelName(modelName != null ? modelName : qwenModelName)
                    .temperature(0.7)
                    .timeout(Duration.ofSeconds(60))
                    .build();
            case "ernie" -> OpenAiChatModel.builder()
                    .apiKey(ernieApiKey)
                    .baseUrl(ernieBaseUrl)
                    .modelName(modelName != null ? modelName : ernieModelName)
                    .temperature(0.7)
                    .timeout(Duration.ofSeconds(60))
                    .build();
            case "glm" -> OpenAiChatModel.builder()
                    .apiKey(glmApiKey)
                    .baseUrl(glmBaseUrl)
                    .modelName(modelName != null ? modelName : glmModelName)
                    .temperature(0.7)
                    .timeout(Duration.ofSeconds(60))
                    .build();
            case "doubao" -> OpenAiChatModel.builder()
                    .apiKey(doubaoApiKey)
                    .baseUrl(doubaoBaseUrl)
                    .modelName(modelName != null ? modelName : doubaoModelName)
                    .temperature(0.7)
                    .timeout(Duration.ofSeconds(60))
                    .build();
            case "xai" -> OpenAiChatModel.builder()
                    .apiKey(xaiApiKey)
                    .baseUrl(xaiBaseUrl)
                    .modelName(modelName != null ? modelName : xaiModelName)
                    .temperature(0.7)
                    .timeout(Duration.ofSeconds(60))
                    .build();
            case "openrouter" -> OpenAiChatModel.builder()
                    .apiKey(openrouterApiKey)
                    .baseUrl(openrouterBaseUrl)
                    .modelName(modelName != null ? modelName : openrouterModelName)
                    .temperature(0.7)
                    .timeout(Duration.ofSeconds(60))
                    .build();
            case "azure" -> OpenAiChatModel.builder()
                    .apiKey(azureApiKey)
                    .baseUrl(azureBaseUrl)
                    .modelName(modelName != null ? modelName : azureModelName)
                    .temperature(0.7)
                    .timeout(Duration.ofSeconds(60))
                    .build();
            default -> throw new IllegalArgumentException("Unknown domestic LLM provider: " + provider);
        };
    }

    // 用于测试的getter方法
    public String getMinimaxApiKey() { return minimaxApiKey; }
    public String getMinimaxBaseUrl() { return minimaxBaseUrl; }
    public String getMinimaxModelName() { return minimaxModelName; }
    public String getDeepseekApiKey() { return deepseekApiKey; }
    public String getDeepseekBaseUrl() { return deepseekBaseUrl; }
    public String getDeepseekModelName() { return deepseekModelName; }
    public String getQwenApiKey() { return qwenApiKey; }
    public String getQwenBaseUrl() { return qwenBaseUrl; }
    public String getQwenModelName() { return qwenModelName; }
    public String getErnieApiKey() { return ernieApiKey; }
    public String getErnieBaseUrl() { return ernieBaseUrl; }
    public String getErnieModelName() { return ernieModelName; }
    public String getGlmApiKey() { return glmApiKey; }
    public String getGlmBaseUrl() { return glmBaseUrl; }
    public String getGlmModelName() { return glmModelName; }
    public String getDoubaoApiKey() { return doubaoApiKey; }
    public String getDoubaoBaseUrl() { return doubaoBaseUrl; }
    public String getDoubaoModelName() { return doubaoModelName; }
    public String getXaiApiKey() { return xaiApiKey; }
    public String getXaiBaseUrl() { return xaiBaseUrl; }
    public String getXaiModelName() { return xaiModelName; }
    public String getOpenrouterApiKey() { return openrouterApiKey; }
    public String getOpenrouterBaseUrl() { return openrouterBaseUrl; }
    public String getOpenrouterModelName() { return openrouterModelName; }
    public String getAzureApiKey() { return azureApiKey; }
    public String getAzureBaseUrl() { return azureBaseUrl; }
    public String getAzureModelName() { return azureModelName; }
}
