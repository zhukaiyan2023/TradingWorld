package com.tradingworld.config;

import dev.langchain4j.model.chat.ChatModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DomesticLlmConfig的单元测试 - 验证所有国内LLM提供商。
 */
class DomesticLlmConfigTest {

    private DomesticLlmConfig domesticLlmConfig;

    @BeforeEach
    void setUp() {
        domesticLlmConfig = new DomesticLlmConfig();

        // 设置所有提供商配置
        ReflectionTestUtils.setField(domesticLlmConfig, "minimaxApiKey", "test-minimax-key");
        ReflectionTestUtils.setField(domesticLlmConfig, "minimaxBaseUrl", "https://api.minimaxi.chat/v1");
        ReflectionTestUtils.setField(domesticLlmConfig, "minimaxModelName", "minimax-01");

        ReflectionTestUtils.setField(domesticLlmConfig, "deepseekApiKey", "test-deepseek-key");
        ReflectionTestUtils.setField(domesticLlmConfig, "deepseekBaseUrl", "https://api.deepseek.com/v1");
        ReflectionTestUtils.setField(domesticLlmConfig, "deepseekModelName", "deepseek-chat");

        ReflectionTestUtils.setField(domesticLlmConfig, "qwenApiKey", "test-qwen-key");
        ReflectionTestUtils.setField(domesticLlmConfig, "qwenBaseUrl", "https://dashscope.aliyuncs.com/compatible-mode/v1");
        ReflectionTestUtils.setField(domesticLlmConfig, "qwenModelName", "qwen-max");

        ReflectionTestUtils.setField(domesticLlmConfig, "ernieApiKey", "test-ernie-key");
        ReflectionTestUtils.setField(domesticLlmConfig, "ernieBaseUrl", "https://qianfan.baidubce.com/v2");
        ReflectionTestUtils.setField(domesticLlmConfig, "ernieModelName", "ernie-4.0-8k-latest");

        ReflectionTestUtils.setField(domesticLlmConfig, "glmApiKey", "test-glm-key");
        ReflectionTestUtils.setField(domesticLlmConfig, "glmBaseUrl", "https://open.bigmodel.cn/api/paas/v4");
        ReflectionTestUtils.setField(domesticLlmConfig, "glmModelName", "glm-4");

        ReflectionTestUtils.setField(domesticLlmConfig, "doubaoApiKey", "test-doubao-key");
        ReflectionTestUtils.setField(domesticLlmConfig, "doubaoBaseUrl", "https://ark.cn-beijing.volces.com/api/v3");
        ReflectionTestUtils.setField(domesticLlmConfig, "doubaoModelName", "doubao-pro-32k");

        // xAI
        ReflectionTestUtils.setField(domesticLlmConfig, "xaiApiKey", "test-xai-key");
        ReflectionTestUtils.setField(domesticLlmConfig, "xaiBaseUrl", "https://api.x.ai/v1");
        ReflectionTestUtils.setField(domesticLlmConfig, "xaiModelName", "grok-4");

        // OpenRouter
        ReflectionTestUtils.setField(domesticLlmConfig, "openrouterApiKey", "test-openrouter-key");
        ReflectionTestUtils.setField(domesticLlmConfig, "openrouterBaseUrl", "https://openrouter.ai/api/v1");
        ReflectionTestUtils.setField(domesticLlmConfig, "openrouterModelName", "openai/gpt-4o");

        // Azure
        ReflectionTestUtils.setField(domesticLlmConfig, "azureApiKey", "test-azure-key");
        ReflectionTestUtils.setField(domesticLlmConfig, "azureBaseUrl", "https://test.openai.azure.com/v1");
        ReflectionTestUtils.setField(domesticLlmConfig, "azureModelName", "gpt-4o-deployment");
    }

    @Test
    void testCreateMinimaxChatModel() {
        ChatModel model = domesticLlmConfig.createDomesticChatModel("minimax-01", "minimax");
        assertNotNull(model);
    }

    @Test
    void testCreateDeepSeekChatModel() {
        ChatModel model = domesticLlmConfig.createDomesticChatModel("deepseek-chat", "deepseek");
        assertNotNull(model);
    }

    @Test
    void testCreateQwenChatModel() {
        ChatModel model = domesticLlmConfig.createDomesticChatModel("qwen-max", "qwen");
        assertNotNull(model);
    }

    @Test
    void testCreateErnieChatModel() {
        ChatModel model = domesticLlmConfig.createDomesticChatModel("ernie-4.0-8k-latest", "ernie");
        assertNotNull(model);
    }

    @Test
    void testCreateGlmChatModel() {
        ChatModel model = domesticLlmConfig.createDomesticChatModel("glm-4", "glm");
        assertNotNull(model);
    }

    @Test
    void testCreateDoubaoChatModel() {
        ChatModel model = domesticLlmConfig.createDomesticChatModel("doubao-pro-32k", "doubao");
        assertNotNull(model);
    }

    @Test
    void testCreateXaiChatModel() {
        ChatModel model = domesticLlmConfig.createDomesticChatModel("grok-4", "xai");
        assertNotNull(model);
    }

    @Test
    void testCreateOpenRouterChatModel() {
        ChatModel model = domesticLlmConfig.createDomesticChatModel("openai/gpt-4o", "openrouter");
        assertNotNull(model);
    }

    @Test
    void testCreateAzureChatModel() {
        ChatModel model = domesticLlmConfig.createDomesticChatModel("gpt-4o-deployment", "azure");
        assertNotNull(model);
    }

    @Test
    void testCreateChatModelCaseInsensitive() {
        assertNotNull(domesticLlmConfig.createDomesticChatModel("deepseek-chat", "DeepSeek"));
        assertNotNull(domesticLlmConfig.createDomesticChatModel("deepseek-chat", "DEEPSEEK"));
        assertNotNull(domesticLlmConfig.createDomesticChatModel("deepseek-chat", "dEePsEeK"));
    }

    @Test
    void testCreateChatModelUnknownProvider() {
        assertThrows(IllegalArgumentException.class, () -> {
            domesticLlmConfig.createDomesticChatModel("some-model", "unknown");
        });
    }

    @Test
    void testCreateChatModelWithNullModelName() {
        ChatModel model = domesticLlmConfig.createDomesticChatModel(null, "minimax");
        assertNotNull(model);
    }

    @Test
    void testGetters() {
        assertEquals("test-minimax-key", domesticLlmConfig.getMinimaxApiKey());
        assertEquals("https://api.minimaxi.chat/v1", domesticLlmConfig.getMinimaxBaseUrl());
        assertEquals("minimax-01", domesticLlmConfig.getMinimaxModelName());

        assertEquals("test-deepseek-key", domesticLlmConfig.getDeepseekApiKey());
        assertEquals("test-xai-key", domesticLlmConfig.getXaiApiKey());
        assertEquals("test-openrouter-key", domesticLlmConfig.getOpenrouterApiKey());
        assertEquals("test-azure-key", domesticLlmConfig.getAzureApiKey());
    }
}
