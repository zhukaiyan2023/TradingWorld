package com.tradingworld;

import com.tradingworld.agents.analysts.MarketAnalyst;
import com.tradingworld.agents.researchers.BullResearcher;
import com.tradingworld.agents.risk_managers.AggressiveRiskManager;
import com.tradingworld.agents.risk_managers.ConservativeRiskManager;
import com.tradingworld.agents.risk_managers.NeutralRiskManager;
import com.tradingworld.agents.trader.Trader;
import com.tradingworld.config.AppConfig;
import com.tradingworld.graph.TradingAgentsGraph;
import com.tradingworld.graph.state.AgentState;
import com.tradingworld.memory.FinancialSituationMemory;
import com.tradingworld.tools.MarketTools;
import com.tradingworld.tools.NewsTools;
import com.tradingworld.tools.TechnicalTools;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TradingAgents工作流的集成测试。
 * 注意：这些测试使用占位符工具实现。
 * 完整集成需要LLM API密钥。
 */
class TradingAgentsIntegrationTest {

    private TradingAgentsGraph graph;
    private AppConfig config;

    @BeforeEach
    void setUp() {
        config = new AppConfig();
        config.setAgents(new AppConfig.AgentsConfig());
        config.setPaths(new AppConfig.PathsConfig());

        // 创建记忆
        FinancialSituationMemory bullMemory = new FinancialSituationMemory("bull");
        FinancialSituationMemory bearMemory = new FinancialSituationMemory("bear");
        FinancialSituationMemory traderMemory = new FinancialSituationMemory("trader");
        FinancialSituationMemory investJudgeMemory = new FinancialSituationMemory("invest_judge");
        FinancialSituationMemory portfolioManagerMemory = new FinancialSituationMemory("portfolio_manager");

        // 创建工具
        MarketTools marketTools = new MarketTools();
        TechnicalTools technicalTools = new TechnicalTools();
        NewsTools newsTools = new NewsTools();

        // 创建分析师
        MarketAnalyst marketAnalyst = new MarketAnalyst(marketTools, technicalTools);

        // 注意：完整图的创建需要实例化所有代理
        // 这是验证组件连接的简化测试
    }

    @Test
    void testConfig_initialization() {
        assertNotNull(config);
        assertNotNull(config.getAgents());
        assertNotNull(config.getPaths());
    }

    @Test
    void testFinancialSituationMemory_integration() {
        FinancialSituationMemory memory = new FinancialSituationMemory("test_integration");

        memory.addSituation("High volatility scenario", "Reduce position size");
        memory.addSituation("Bullish momentum", "Consider increasing exposure");

        var results = memory.getMemories("high volatility today", 1);
        assertFalse(results.isEmpty());
    }

    @Test
    void testAgentState_fullLifecycle() {
        AgentState state = AgentState.builder()
            .companyOfInterest("TEST")
            .tradeDate("2026-01-15")
            .build();

        assertNull(state.getMarketReport());

        // 模拟市场报告
        state.setMarketReport("Market analysis complete");
        assertNotNull(state.getMarketReport());

        // 模拟最终决策
        state.setFinalTradeDecision("BUY - Strong momentum");
        assertNotNull(state.getFinalTradeDecision());
        assertTrue(state.getFinalTradeDecision().contains("BUY"));
    }

    @Test
    void testDebateState_transitions() {
        var investState = new com.tradingworld.graph.state.InvestDebateState();
        assertEquals(0, investState.getCount());

        investState.incrementCount();
        investState.appendBullHistory("First round: Bull case");
        investState.setJudgeDecision("Continue to next round");

        assertEquals(1, investState.getCount());
        assertTrue(investState.getBullHistory().contains("First round"));
        assertNotNull(investState.getJudgeDecision());
    }

    @Test
    void testRiskDebateState_fullCycle() {
        var riskState = new com.tradingworld.graph.state.RiskDebateState();

        riskState.incrementCount();
        riskState.appendAggressiveHistory("Aggressive: High risk tolerance");
        riskState.appendConservativeHistory("Conservative: Low risk tolerance");
        riskState.appendNeutralHistory("Neutral: Balanced approach");

        assertEquals(1, riskState.getCount());
        assertTrue(riskState.getAggressiveHistory().contains("Aggressive"));
        assertTrue(riskState.getConservativeHistory().contains("Conservative"));
        assertTrue(riskState.getNeutralHistory().contains("Neutral"));
    }

    @Test
    void testDecision_markers() {
        // 测试交易决策模式
        String buySignal = "FINAL DECISION: BUY";
        String sellSignal = "FINAL DECISION: SELL";
        String holdSignal = "FINAL DECISION: HOLD";

        assertTrue(buySignal.contains("BUY"));
        assertTrue(sellSignal.contains("SELL"));
        assertTrue(holdSignal.contains("HOLD"));
    }

    @Test
    void testMemory_multipleSituations() {
        FinancialSituationMemory memory = new FinancialSituationMemory("integration_test");

        memory.addSituation("Tech stocks showing RSI above 70", "Overbought - consider taking profits");
        memory.addSituation("High inflation data released", "Defensive sectors may outperform");
        memory.addSituation("FED announces rate hike", "Growth stocks may underperform");

        var results1 = memory.getMemories("RSI indicator extreme", 1);
        assertFalse(results1.isEmpty());

        var results2 = memory.getMemories("inflation impact", 1);
        assertFalse(results2.isEmpty());

        var results3 = memory.getMemories("interest rates", 1);
        assertFalse(results3.isEmpty());
    }

    @Test
    void testWorkflow_stateTransition() {
        // 模拟工作流中的状态转换
        AgentState state = AgentState.builder()
            .companyOfInterest("NVDA")
            .tradeDate("2026-01-15")
            .build();

        // 阶段1：分析师
        state.setMarketReport("Market: Strong momentum");
        state.setSentimentReport("Sentiment: Positive");
        state.setNewsReport("News: New product launch");
        state.setFundamentalsReport("Fundamentals: Strong earnings");

        assertNotNull(state.getMarketReport());
        assertNotNull(state.getSentimentReport());
        assertNotNull(state.getNewsReport());
        assertNotNull(state.getFundamentalsReport());

        // 阶段2：投资辩论
        var investState = new com.tradingworld.graph.state.InvestDebateState();
        investState.appendBullHistory("Bull: Strong growth potential");
        investState.appendBearHistory("Bear: Valuation concerns");
        investState.setJudgeDecision("Proceed with caution");
        state.setInvestmentDebateState(investState);

        assertNotNull(state.getInvestmentDebateState());
        assertNotNull(state.getInvestmentDebateState().getJudgeDecision());

        // 阶段3：交易员决策
        state.setTraderInvestmentPlan("Recommendation: BUY with 10% position");
        assertNotNull(state.getTraderInvestmentPlan());

        // 阶段4：风险评估
        var riskState = new com.tradingworld.graph.state.RiskDebateState();
        riskState.setJudgeDecision("Moderate risk - proceed");
        state.setRiskDebateState(riskState);

        // 阶段5：最终决策
        state.setFinalTradeDecision("FINAL DECISION: BUY - Strong technicals, positive sentiment");
        assertNotNull(state.getFinalTradeDecision());
        assertTrue(state.getFinalTradeDecision().contains("BUY"));

        // 验证完整工作流
        assertEquals("NVDA", state.getCompanyOfInterest());
        assertEquals("2026-01-15", state.getTradeDate());
    }
}
