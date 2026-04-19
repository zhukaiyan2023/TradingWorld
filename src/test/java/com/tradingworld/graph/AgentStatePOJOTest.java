package com.tradingworld.graph;

import com.tradingworld.graph.state.AgentState;
import com.tradingworld.graph.state.InvestDebateState;
import com.tradingworld.graph.state.RiskDebateState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AgentState及相关状态类的单元测试。
 */
class AgentStatePOJOTest {

    @Test
    void testAgentState_builder() {
        AgentState state = AgentState.builder()
            .companyOfInterest("NVDA")
            .tradeDate("2026-01-15")
            .sender("test")
            .build();

        assertEquals("NVDA", state.getCompanyOfInterest());
        assertEquals("2026-01-15", state.getTradeDate());
        assertEquals("test", state.getSender());
    }

    @Test
    void testAgentState_defaultValues() {
        AgentState state = new AgentState();
        assertNull(state.getCompanyOfInterest());
        assertNull(state.getTradeDate());
        assertNotNull(state.getMessages());
        assertTrue(state.getMessages().isEmpty());
    }

    @Test
    void testAgentState_settersAndGetters() {
        AgentState state = new AgentState();
        state.setCompanyOfInterest("AAPL");
        state.setTradeDate("2026-01-20");
        state.setMarketReport("Test report");

        assertEquals("AAPL", state.getCompanyOfInterest());
        assertEquals("2026-01-20", state.getTradeDate());
        assertEquals("Test report", state.getMarketReport());
    }

    @Test
    void testInvestDebateState_builder() {
        InvestDebateState state = InvestDebateState.builder()
            .bullHistory("Bull case")
            .bearHistory("Bear case")
            .count(5)
            .build();

        assertEquals("Bull case", state.getBullHistory());
        assertEquals("Bear case", state.getBearHistory());
        assertEquals(5, state.getCount());
    }

    @Test
    void testInvestDebateState_appendHistory() {
        InvestDebateState state = new InvestDebateState();
        state.appendBullHistory("First bull point");
        state.appendBullHistory("Second bull point");

        assertTrue(state.getBullHistory().contains("First bull point"));
        assertTrue(state.getBullHistory().contains("Second bull point"));
    }

    @Test
    void testInvestDebateState_incrementCount() {
        InvestDebateState state = new InvestDebateState();
        assertEquals(0, state.getCount());

        state.incrementCount();
        assertEquals(1, state.getCount());

        state.incrementCount();
        assertEquals(2, state.getCount());
    }

    @Test
    void testRiskDebateState_builder() {
        RiskDebateState state = RiskDebateState.builder()
            .aggressiveHistory("Aggressive view")
            .conservativeHistory("Conservative view")
            .neutralHistory("Neutral view")
            .count(3)
            .build();

        assertEquals("Aggressive view", state.getAggressiveHistory());
        assertEquals("Conservative view", state.getConservativeHistory());
        assertEquals("Neutral view", state.getNeutralHistory());
        assertEquals(3, state.getCount());
    }

    @Test
    void testRiskDebateState_appendHistories() {
        RiskDebateState state = new RiskDebateState();
        state.appendAggressiveHistory("Risk point 1");
        state.appendConservativeHistory("Risk point 2");
        state.appendNeutralHistory("Risk point 3");

        assertTrue(state.getAggressiveHistory().contains("Risk point 1"));
        assertTrue(state.getConservativeHistory().contains("Risk point 2"));
        assertTrue(state.getNeutralHistory().contains("Risk point 3"));
    }

    @Test
    void testChatMessage() {
        AgentState.ChatMessage msg = new AgentState.ChatMessage("user", "Hello");
        assertEquals("user", msg.getRole());
        assertEquals("Hello", msg.getContent());

        msg.setRole("assistant");
        msg.setContent("Response");
        assertEquals("assistant", msg.getRole());
        assertEquals("Response", msg.getContent());
    }

    @Test
    void testFullStateChain() {
        // 测试嵌套状态是否正常工作
        AgentState state = AgentState.builder()
            .companyOfInterest("NVDA")
            .tradeDate("2026-01-15")
            .investmentDebateState(InvestDebateState.builder().count(1).build())
            .riskDebateState(RiskDebateState.builder().count(2).build())
            .build();

        assertNotNull(state.getInvestmentDebateState());
        assertEquals(1, state.getInvestmentDebateState().getCount());
        assertNotNull(state.getRiskDebateState());
        assertEquals(2, state.getRiskDebateState().getCount());
    }
}
