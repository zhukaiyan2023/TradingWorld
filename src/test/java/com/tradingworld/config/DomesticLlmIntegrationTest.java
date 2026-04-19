package com.tradingworld.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 国内LLM提供商的集成测试。
 * 这些测试验证配置和模型创建流程。
 * 注意：实际API调用需要有效的API密钥。
 */
class DomesticLlmIntegrationTest {

    private DomesticLlmConfig domesticLlmConfig;

    @BeforeEach
    void setUp() {
        domesticLlmConfig = new DomesticLlmConfig();
    }

    @Test
    void testMiniMaxProvider_withValidConfig() {
        // 模拟MiniMax配置
        ReflectionTestUtils.setField(domesticLlmConfig, "minimaxApiKey", "sk-test-minimax");
        ReflectionTestUtils.setField(domesticLlmConfig, "minimaxBaseUrl", "https://api.minimaxi.chat/v1");
        ReflectionTestUtils.setField(domesticLlmConfig, "minimaxModelName", "minimax-01");

        ChatModel model = domesticLlmConfig.createDomesticChatModel("minimax-01", "minimax");
        assertNotNull(model);

        // 验证模型是OpenAI兼容模型
        assertTrue(model instanceof OpenAiChatModel);
    }

    @Test
    void testDeepSeekProvider_withValidConfig() {
        ReflectionTestUtils.setField(domesticLlmConfig, "deepseekApiKey", "sk-test-deepseek");
        ReflectionTestUtils.setField(domesticLlmConfig, "deepseekBaseUrl", "https://api.deepseek.com/v1");
        ReflectionTestUtils.setField(domesticLlmConfig, "deepseekModelName", "deepseek-chat");

        ChatModel model = domesticLlmConfig.createDomesticChatModel("deepseek-chat", "deepseek");
        assertNotNull(model);
        assertTrue(model instanceof OpenAiChatModel);
    }

    @Test
    void testQwenProvider_withValidConfig() {
        ReflectionTestUtils.setField(domesticLlmConfig, "qwenApiKey", "sk-test-qwen");
        ReflectionTestUtils.setField(domesticLlmConfig, "qwenBaseUrl", "https://dashscope.aliyuncs.com/compatible-mode/v1");
        ReflectionTestUtils.setField(domesticLlmConfig, "qwenModelName", "qwen-max");

        ChatModel model = domesticLlmConfig.createDomesticChatModel("qwen-max", "qwen");
        assertNotNull(model);
        assertTrue(model instanceof OpenAiChatModel);
    }

    @Test
    void testErnieProvider_withValidConfig() {
        ReflectionTestUtils.setField(domesticLlmConfig, "ernieApiKey", "sk-test-ernie");
        ReflectionTestUtils.setField(domesticLlmConfig, "ernieBaseUrl", "https://qianfan.baidubce.com/v2");
        ReflectionTestUtils.setField(domesticLlmConfig, "ernieModelName", "ernie-4.0-8k-latest");

        ChatModel model = domesticLlmConfig.createDomesticChatModel("ernie-4.0-8k-latest", "ernie");
        assertNotNull(model);
        assertTrue(model instanceof OpenAiChatModel);
    }

    @Test
    void testGlmProvider_withValidConfig() {
        ReflectionTestUtils.setField(domesticLlmConfig, "glmApiKey", "sk-test-glm");
        ReflectionTestUtils.setField(domesticLlmConfig, "glmBaseUrl", "https://open.bigmodel.cn/api/paas/v4");
        ReflectionTestUtils.setField(domesticLlmConfig, "glmModelName", "glm-4");

        ChatModel model = domesticLlmConfig.createDomesticChatModel("glm-4", "glm");
        assertNotNull(model);
        assertTrue(model instanceof OpenAiChatModel);
    }

    @Test
    void testDoubaoProvider_withValidConfig() {
        ReflectionTestUtils.setField(domesticLlmConfig, "doubaoApiKey", "sk-test-doubao");
        ReflectionTestUtils.setField(domesticLlmConfig, "doubaoBaseUrl", "https://ark.cn-beijing.volces.com/api/v3");
        ReflectionTestUtils.setField(domesticLlmConfig, "doubaoModelName", "doubao-pro-32k");

        ChatModel model = domesticLlmConfig.createDomesticChatModel("doubao-pro-32k", "doubao");
        assertNotNull(model);
        assertTrue(model instanceof OpenAiChatModel);
    }

    @Test
    void testAllDomesticProviders_canBeCreated() {
        // MiniMax
        ReflectionTestUtils.setField(domesticLlmConfig, "minimaxApiKey", "key");
        ReflectionTestUtils.setField(domesticLlmConfig, "minimaxBaseUrl", "https://api.minimaxi.chat/v1");
        ReflectionTestUtils.setField(domesticLlmConfig, "minimaxModelName", "minimax-01");
        assertNotNull(domesticLlmConfig.createDomesticChatModel("minimax-01", "minimax"));

        // DeepSeek
        ReflectionTestUtils.setField(domesticLlmConfig, "deepseekApiKey", "key");
        ReflectionTestUtils.setField(domesticLlmConfig, "deepseekBaseUrl", "https://api.deepseek.com/v1");
        ReflectionTestUtils.setField(domesticLlmConfig, "deepseekModelName", "deepseek-chat");
        assertNotNull(domesticLlmConfig.createDomesticChatModel("deepseek-chat", "deepseek"));

        // Qwen
        ReflectionTestUtils.setField(domesticLlmConfig, "qwenApiKey", "key");
        ReflectionTestUtils.setField(domesticLlmConfig, "qwenBaseUrl", "https://dashscope.aliyuncs.com/compatible-mode/v1");
        ReflectionTestUtils.setField(domesticLlmConfig, "qwenModelName", "qwen-max");
        assertNotNull(domesticLlmConfig.createDomesticChatModel("qwen-max", "qwen"));

        // ERNIE
        ReflectionTestUtils.setField(domesticLlmConfig, "ernieApiKey", "key");
        ReflectionTestUtils.setField(domesticLlmConfig, "ernieBaseUrl", "https://qianfan.baidubce.com/v2");
        ReflectionTestUtils.setField(domesticLlmConfig, "ernieModelName", "ernie-4.0-8k-latest");
        assertNotNull(domesticLlmConfig.createDomesticChatModel("ernie-4.0-8k-latest", "ernie"));

        // GLM
        ReflectionTestUtils.setField(domesticLlmConfig, "glmApiKey", "key");
        ReflectionTestUtils.setField(domesticLlmConfig, "glmBaseUrl", "https://open.bigmodel.cn/api/paas/v4");
        ReflectionTestUtils.setField(domesticLlmConfig, "glmModelName", "glm-4");
        assertNotNull(domesticLlmConfig.createDomesticChatModel("glm-4", "glm"));

        // Doubao
        ReflectionTestUtils.setField(domesticLlmConfig, "doubaoApiKey", "key");
        ReflectionTestUtils.setField(domesticLlmConfig, "doubaoBaseUrl", "https://ark.cn-beijing.volces.com/api/v3");
        ReflectionTestUtils.setField(domesticLlmConfig, "doubaoModelName", "doubao-pro-32k");
        assertNotNull(domesticLlmConfig.createDomesticChatModel("doubao-pro-32k", "doubao"));
    }
}
