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
 * LangChain4j ChatModel bean的配置。
 * 支持多种LLM提供商：OpenAI、Anthropic、Google AI、Ollama和国内中文提供商。
 */
@Configuration
public class LlmConfig {

    private static final Logger log = LoggerFactory.getLogger(LlmConfig.class);

    private final DomesticLlmConfig domesticLlmConfig;

    public LlmConfig(DomesticLlmConfig domesticLlmConfig) {
        this.domesticLlmConfig = domesticLlmConfig;
    }

    @Value("${langchain4j.open-ai.api-key:}")
    private String openAiApiKey;

    @Value("${langchain4j.open-ai.base-url:https://api.openai.com/v1}")
    private String openAiBaseUrl;

    @Value("${langchain4j.open-ai.model-name:gpt-4.5}")
    private String openAiModelName;

    @Value("${langchain4j.anthropic.api-key:}")
    private String anthropicApiKey;

    @Value("${langchain4j.anthropic.model-name:claude-3-5-sonnet-20241022}")
    private String anthropicModelName;

    @Value("${langchain4j.google-ai.api-key:}")
    private String googleApiKey;

    @Value("${langchain4j.google-ai.model-name:gemini-pro}")
    private String googleModelName;

    @Value("${langchain4j.ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${langchain4j.ollama.model-name:llama3.2}")
    private String ollamaModelName;

    /**
     * 根据配置创建深度思考ChatModel。
     */
    @Bean(name = "deepThinkChatModel")
    public ChatModel deepThinkChatModel(AppConfig appConfig) {
        return createChatModel(appConfig.getLlm().getDeepThinkModel(), appConfig.getLlm().getProvider());
    }

    /**
     * 根据配置创建快速思考ChatModel。
     */
    @Bean(name = "quickThinkChatModel")
    public ChatModel quickThinkChatModel(AppConfig appConfig) {
        return createChatModel(appConfig.getLlm().getQuickThinkModel(), appConfig.getLlm().getProvider());
    }

    private ChatModel createChatModel(String modelName, String provider) {
        log.info("Creating ChatModel for provider: {}, model: {}", provider, modelName);

        return switch (provider.toLowerCase()) {
            case "openai" -> OpenAiChatModel.builder()
                    .apiKey(openAiApiKey)
                    .baseUrl(openAiBaseUrl)
                    .modelName(modelName)
                    .temperature(0.7)
                    .timeout(Duration.ofSeconds(60))
                    .build();
            case "anthropic" -> {
                // 对于Anthropic，需要langchain4j-anthropic模块
                // 目前返回OpenAI作为占位符
                log.warn("Anthropic not fully configured, falling back to OpenAI");
                yield OpenAiChatModel.builder()
                        .apiKey(openAiApiKey)
                        .baseUrl(openAiBaseUrl)
                        .modelName(modelName)
                        .temperature(0.7)
                        .build();
            }
            case "google" -> {
                log.warn("Google AI not fully configured, falling back to OpenAI");
                yield OpenAiChatModel.builder()
                        .apiKey(openAiApiKey)
                        .baseUrl(openAiBaseUrl)
                        .modelName(modelName)
                        .temperature(0.7)
                        .build();
            }
            case "ollama" -> {
                log.warn("Ollama not fully configured, falling back to OpenAI");
                yield OpenAiChatModel.builder()
                        .apiKey(openAiApiKey)
                        .baseUrl(openAiBaseUrl)
                        .modelName(modelName)
                        .temperature(0.7)
                        .build();
            }
            case "minimax", "deepseek", "qwen", "ernie", "glm", "doubao",
                 "xai", "openrouter", "azure" -> {
                log.info("Using extended LLM provider: {}", provider);
                yield domesticLlmConfig.createDomesticChatModel(modelName, provider);
            }
            default -> OpenAiChatModel.builder()
                    .apiKey(openAiApiKey)
                    .baseUrl(openAiBaseUrl)
                    .modelName(modelName)
                    .temperature(0.7)
                    .build();
        };
    }
}
