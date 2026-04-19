package com.tradingworld.agents;

import com.tradingworld.agents.analysts.MarketAnalyst;
import com.tradingworld.graph.state.AgentState;
import com.tradingworld.tools.MarketTools;
import com.tradingworld.tools.TechnicalTools;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MarketAnalyst代理的单元测试。
 */
class MarketAnalystTest {

    private MarketAnalyst analyst;
    private AgentState state;

    @BeforeEach
    void setUp() {
        MarketTools marketTools = new MarketTools();
        TechnicalTools technicalTools = new TechnicalTools();
        analyst = new MarketAnalyst(marketTools, technicalTools);
        state = AgentState.builder()
            .companyOfInterest("NVDA")
            .tradeDate("2026-01-15")
            .build();
    }

    @Test
    void testGetName() {
        assertEquals("MarketAnalyst", analyst.getName());
    }

    @Test
    void testGetSystemPrompt() {
        String prompt = analyst.getSystemPrompt();
        assertNotNull(prompt);
        assertTrue(prompt.contains("Market Analyst") || prompt.contains("technical"));
    }

    @Test
    void testExecute_updatesState() {
        String result = analyst.execute(state);
        assertNotNull(result);
        assertEquals("MarketAnalyst", state.getSender());
        assertNotNull(state.getMarketReport());
    }

    @Test
    void testExecute_containsMarketReport() {
        analyst.execute(state);
        assertNotNull(state.getMarketReport());
        assertTrue(state.getMarketReport().length() > 0);
    }

    @Test
    void testExecute_handlesException() {
        // 创建包含无效公司的状态
        AgentState badState = AgentState.builder()
            .companyOfInterest(null)
            .tradeDate("2026-01-15")
            .build();
        String result = analyst.execute(badState);
        assertNotNull(result);
        // 应优雅处理
    }
}
