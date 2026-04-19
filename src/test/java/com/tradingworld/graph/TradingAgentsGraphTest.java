package com.tradingworld.graph;

import com.tradingworld.config.AppConfig;
import com.tradingworld.graph.state.AgentState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TradingAgentsGraph的单元测试。
 * 注意：由于完整的LLM集成需要API密钥，此测试使用模拟组件。
 */
class TradingAgentsGraphTest {

    private AppConfig config;

    @BeforeEach
    void setUp() {
        config = new AppConfig();
        config.setAgents(new AppConfig.AgentsConfig());
        config.setPaths(new AppConfig.PathsConfig());
    }

    @Test
    void testBuilder_createsInstance() {
        TradingAgentsGraph graph = TradingAgentsGraph.builder()
            .config(config)
            .build();
        assertNotNull(graph);
    }

    @Test
    void testAppConfig_defaults() {
        AppConfig.AgentsConfig agentsConfig = new AppConfig.AgentsConfig();
        assertEquals(1, agentsConfig.getMaxDebateRounds());
        assertEquals(1, agentsConfig.getMaxRiskDiscussRounds());
        assertEquals(100, agentsConfig.getMaxRecurLimit());
    }

    @Test
    void testAppConfig_setters() {
        AppConfig.AgentsConfig agentsConfig = new AppConfig.AgentsConfig();
        agentsConfig.setMaxDebateRounds(3);
        agentsConfig.setMaxRiskDiscussRounds(2);

        assertEquals(3, agentsConfig.getMaxDebateRounds());
        assertEquals(2, agentsConfig.getMaxRiskDiscussRounds());
    }

    @Test
    void testAppConfig_llmConfig() {
        AppConfig.LlmConfig llmConfig = new AppConfig.LlmConfig();
        llmConfig.setProvider("openai");
        llmConfig.setDeepThinkModel("gpt-4.5");
        llmConfig.setQuickThinkModel("gpt-4.5-mini");

        assertEquals("openai", llmConfig.getProvider());
        assertEquals("gpt-4.5", llmConfig.getDeepThinkModel());
    }

    @Test
    void testAppConfig_memoryConfig() {
        AppConfig.MemoryConfig memoryConfig = new AppConfig.MemoryConfig();
        memoryConfig.setBm25K1(2.0);
        memoryConfig.setBm25B(0.5);

        assertEquals(2.0, memoryConfig.getBm25K1());
        assertEquals(0.5, memoryConfig.getBm25B());
    }
}
