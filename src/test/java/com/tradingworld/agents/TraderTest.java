package com.tradingworld.agents;

import com.tradingworld.agents.researchers.BullResearcher;
import com.tradingworld.graph.state.AgentState;
import com.tradingworld.graph.state.InvestDebateState;
import com.tradingworld.memory.FinancialSituationMemory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BullResearcher代理的单元测试。
 */
class BullResearcherTest {

    private BullResearcher bullResearcher;
    private AgentState state;

    @BeforeEach
    void setUp() {
        FinancialSituationMemory memory = new FinancialSituationMemory("test");
        bullResearcher = new BullResearcher(memory, 1);

        state = AgentState.builder()
            .companyOfInterest("NVDA")
            .tradeDate("2026-01-15")
            .marketReport("Strong momentum indicators")
            .sentimentReport("Positive sentiment")
            .fundamentalsReport("Strong earnings")
            .newsReport("New product launch")
            .investmentDebateState(new InvestDebateState())
            .build();
    }

    @Test
    void testGetName() {
        assertEquals("BullResearcher", bullResearcher.getName());
    }

    @Test
    void testGetSystemPrompt() {
        String prompt = bullResearcher.getSystemPrompt();
        assertNotNull(prompt);
        assertTrue(prompt.contains("Bullish") || prompt.contains("bull"));
    }

    @Test
    void testExecute_updatesDebateState() {
        String result = bullResearcher.execute(state);
        assertNotNull(result);
        assertNotNull(state.getInvestmentDebateState());
        assertNotNull(state.getInvestmentDebateState().getBullHistory());
    }

    @Test
    void testExecute_containsBullishContent() {
        String result = bullResearcher.execute(state);
        assertTrue(result.contains("BULLISH") || result.contains("bullish"));
    }

    @Test
    void testExecute_incrementsDebateCount() {
        InvestDebateState debateState = state.getInvestmentDebateState();
        assertEquals(0, debateState.getCount());

        bullResearcher.execute(state);
        assertEquals(1, debateState.getCount());
    }

    @Test
    void testExecute_setsCurrentResponse() {
        bullResearcher.execute(state);
        assertNotNull(state.getInvestmentDebateState().getCurrentResponse());
        assertTrue(state.getInvestmentDebateState().getCurrentResponse().length() > 0);
    }

    @Test
    void testExecute_setsSender() {
        bullResearcher.execute(state);
        assertEquals("BullResearcher", state.getSender());
    }
}
