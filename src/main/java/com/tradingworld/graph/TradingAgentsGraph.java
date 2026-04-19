package com.tradingworld.graph;

import com.tradingworld.agents.analysts.FundamentalsAnalyst;
import com.tradingworld.agents.analysts.MarketAnalyst;
import com.tradingworld.agents.analysts.NewsAnalyst;
import com.tradingworld.agents.analysts.SentimentAnalyst;
import com.tradingworld.agents.researchers.BearResearcher;
import com.tradingworld.agents.researchers.BullResearcher;
import com.tradingworld.agents.risk_managers.AggressiveRiskManager;
import com.tradingworld.agents.risk_managers.ConservativeRiskManager;
import com.tradingworld.agents.risk_managers.NeutralRiskManager;
import com.tradingworld.agents.trader.Trader;
import com.tradingworld.config.AppConfig;
import com.tradingworld.graph.state.AgentState;
import com.tradingworld.graph.state.InvestDebateState;
import com.tradingworld.graph.state.RiskDebateState;
import com.tradingworld.memory.FinancialSituationMemory;
import dev.langchain4j.model.chat.ChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 主编排多智能体交易工作流程的TradingAgents图形。
 * 在基于辩论的工作流程中协调分析师、研究员、交易员和风险管理器。
 */
public class TradingAgentsGraph {

    private static final Logger log = LoggerFactory.getLogger(TradingAgentsGraph.class);

    private final AppConfig config;
    private final ChatModel deepThinkModel;
    private final ChatModel quickThinkModel;

    // 智能体实例
    private final MarketAnalyst marketAnalyst;
    private final SentimentAnalyst sentimentAnalyst;
    private final NewsAnalyst newsAnalyst;
    private final FundamentalsAnalyst fundamentalsAnalyst;

    private final BullResearcher bullResearcher;
    private final BearResearcher bearResearcher;

    private final Trader trader;

    private final AggressiveRiskManager aggressiveRiskManager;
    private final ConservativeRiskManager conservativeRiskManager;
    private final NeutralRiskManager neutralRiskManager;

    // 内存
    private final FinancialSituationMemory bullMemory;
    private final FinancialSituationMemory bearMemory;
    private final FinancialSituationMemory traderMemory;
    private final FinancialSituationMemory investJudgeMemory;
    private final FinancialSituationMemory portfolioManagerMemory;

    private TradingAgentsGraph(Builder builder) {
        this.config = builder.config;
        this.deepThinkModel = builder.deepThinkModel;
        this.quickThinkModel = builder.quickThinkModel;
        this.marketAnalyst = builder.marketAnalyst;
        this.sentimentAnalyst = builder.sentimentAnalyst;
        this.newsAnalyst = builder.newsAnalyst;
        this.fundamentalsAnalyst = builder.fundamentalsAnalyst;
        this.bullResearcher = builder.bullResearcher;
        this.bearResearcher = builder.bearResearcher;
        this.trader = builder.trader;
        this.aggressiveRiskManager = builder.aggressiveRiskManager;
        this.conservativeRiskManager = builder.conservativeRiskManager;
        this.neutralRiskManager = builder.neutralRiskManager;
        this.bullMemory = builder.bullMemory;
        this.bearMemory = builder.bearMemory;
        this.traderMemory = builder.traderMemory;
        this.investJudgeMemory = builder.investJudgeMemory;
        this.portfolioManagerMemory = builder.portfolioManagerMemory;
    }

    /**
     * 执行公司给定日期的完整交易工作流程。
     *
     * @param company 公司股票代码
     * @param tradeDate 交易日期，格式为YYYY-MM-DD
     * @return 包含所有报告和决策的最终智能体状态
     */
    public AgentState propagate(String company, String tradeDate) {
        log.info("Starting TradingAgents workflow for {} on {}", company, tradeDate);

        // 初始化状态
        AgentState state = createInitialState(company, tradeDate);

        try {
            // 步骤1：并行运行所有分析师
            log.info("Step 1: Running analysts...");
            runAnalysts(state);

            // 步骤2：牛市和熊市研究员之间的投资辩论
            log.info("Step 2: Running investment debate...");
            runInvestmentDebate(state);

            // 步骤3：交易员做出投资决策
            log.info("Step 3: Trader making decision...");
            trader.execute(state);

            // 步骤4：风险管理器之间的风险辩论
            log.info("Step 4: Running risk debate...");
            runRiskDebate(state);

            // 步骤5：做出最终交易决策
            log.info("Step 5: Finalizing trade decision...");
            makeFinalDecision(state);

            log.info("TradingAgents workflow completed for {}", company);
            return state;

        } catch (Exception e) {
            log.error("Error during workflow execution: {}", e.getMessage(), e);
            state.setFinalTradeDecision("ERROR: " + e.getMessage());
            return state;
        }
    }

    private AgentState createInitialState(String company, String tradeDate) {
        return AgentState.builder()
                .companyOfInterest(company)
                .tradeDate(tradeDate)
                .sender("system")
                .investmentDebateState(new InvestDebateState())
                .riskDebateState(new RiskDebateState())
                .build();
    }

    private void runAnalysts(AgentState state) {
        // 运行分析师 - 在实际实现中这些将并行运行
        // 目前，我们按顺序运行
        log.debug("Running Market Analyst...");
        marketAnalyst.execute(state);

        log.debug("Running Sentiment Analyst...");
        sentimentAnalyst.execute(state);

        log.debug("Running News Analyst...");
        newsAnalyst.execute(state);

        log.debug("Running Fundamentals Analyst...");
        fundamentalsAnalyst.execute(state);
    }

    private void runInvestmentDebate(AgentState state) {
        int maxRounds = config.getAgents().getMaxDebateRounds();
        InvestDebateState debateState = state.getInvestmentDebateState();

        for (int round = 0; round < maxRounds; round++) {
            log.debug("Investment debate round {}", round + 1);
            debateState.incrementCount();

            // 牛市研究员提出论点
            bullResearcher.execute(state);

            // 熊市研究员提出论点
            bearResearcher.execute(state);

            // 裁判做出决策（占位符 - 将使用LLM）
            makeInvestmentJudgeDecision(state);
        }
    }

    private void makeInvestmentJudgeDecision(AgentState state) {
        InvestDebateState debateState = state.getInvestmentDebateState();

        // 简单的基于规则的裁判决策
        // 在实际实现中，这将使用LLM进行评估
        String decision = String.format("""
            Investment Judge Decision (Round %d):
            After reviewing both bullish and bearish arguments:
            - Bull case highlights growth potential and favorable trends
            - Bear case identifies risks and potential headwinds

            Recommendation: Based on risk-reward assessment, proceeding with evaluation.
            """, debateState.getCount());

        debateState.setJudgeDecision(decision);

        // 如果辩论结束，也设置投资计划
        if (debateState.getCount() >= config.getAgents().getMaxDebateRounds()) {
            state.setInvestmentPlan(decision);
        }
    }

    private void runRiskDebate(AgentState state) {
        int maxRounds = config.getAgents().getMaxRiskDiscussRounds();
        RiskDebateState debateState = state.getRiskDebateState();

        for (int round = 0; round < maxRounds; round++) {
            log.debug("Risk debate round {}", round + 1);
            debateState.incrementCount();

            // 所有风险管理器提出论点
            aggressiveRiskManager.execute(state);
            conservativeRiskManager.execute(state);
            neutralRiskManager.execute(state);

            // 裁判做出决策
            makeRiskJudgeDecision(state);
        }
    }

    private void makeRiskJudgeDecision(AgentState state) {
        RiskDebateState debateState = state.getRiskDebateState();

        // 简单的聚合 - 在实际实现中将使用LLM
        String decision = String.format("""
            Risk Judge Decision (Round %d):
            After reviewing risk assessments from all perspectives:
            - Aggressive manager favors higher risk/reward
            - Conservative manager prioritizes capital preservation
            - Neutral manager seeks balanced approach

            Synthesized Risk Assessment: MODERATE RISK
            Final Recommendation: Proceed with balanced position sizing
            """, debateState.getCount());

        debateState.setJudgeDecision(decision);
    }

    private void makeFinalDecision(AgentState state) {
        RiskDebateState riskState = state.getRiskDebateState();
        InvestDebateState investState = state.getInvestmentDebateState();

        String finalDecision = String.format("""
            FINAL TRADE DECISION for %s
            ============================

            Date: %s

            INVESTMENT SUMMARY:
            %s

            RISK ASSESSMENT:
            %s

            FINAL DECISION:
            Based on comprehensive analysis by the investment team and risk management
            committee, the following trading action is recommended:

            ACTION: HOLD
            REASONING: Awaiting clearer signals from market conditions.

            This decision integrates insights from:
            - Market Analyst
            - Sentiment Analyst
            - News Analyst
            - Fundamentals Analyst
            - Bull and Bear Researchers
            - Risk Management Team

            CONFIDENCE: [To be evaluated]
            """,
            state.getCompanyOfInterest(),
            state.getTradeDate(),
            state.getTraderInvestmentPlan(),
            riskState.getJudgeDecision());

        state.setFinalTradeDecision(finalDecision);
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private AppConfig config;
        private ChatModel deepThinkModel;
        private ChatModel quickThinkModel;
        private MarketAnalyst marketAnalyst;
        private SentimentAnalyst sentimentAnalyst;
        private NewsAnalyst newsAnalyst;
        private FundamentalsAnalyst fundamentalsAnalyst;
        private BullResearcher bullResearcher;
        private BearResearcher bearResearcher;
        private Trader trader;
        private AggressiveRiskManager aggressiveRiskManager;
        private ConservativeRiskManager conservativeRiskManager;
        private NeutralRiskManager neutralRiskManager;
        private FinancialSituationMemory bullMemory;
        private FinancialSituationMemory bearMemory;
        private FinancialSituationMemory traderMemory;
        private FinancialSituationMemory investJudgeMemory;
        private FinancialSituationMemory portfolioManagerMemory;

        public Builder config(AppConfig config) { this.config = config; return this; }
        public Builder deepThinkModel(ChatModel model) { this.deepThinkModel = model; return this; }
        public Builder quickThinkModel(ChatModel model) { this.quickThinkModel = model; return this; }
        public Builder marketAnalyst(MarketAnalyst analyst) { this.marketAnalyst = analyst; return this; }
        public Builder sentimentAnalyst(SentimentAnalyst analyst) { this.sentimentAnalyst = analyst; return this; }
        public Builder newsAnalyst(NewsAnalyst analyst) { this.newsAnalyst = analyst; return this; }
        public Builder fundamentalsAnalyst(FundamentalsAnalyst analyst) { this.fundamentalsAnalyst = analyst; return this; }
        public Builder bullResearcher(BullResearcher researcher) { this.bullResearcher = researcher; return this; }
        public Builder bearResearcher(BearResearcher researcher) { this.bearResearcher = researcher; return this; }
        public Builder trader(Trader trader) { this.trader = trader; return this; }
        public Builder aggressiveRiskManager(AggressiveRiskManager manager) { this.aggressiveRiskManager = manager; return this; }
        public Builder conservativeRiskManager(ConservativeRiskManager manager) { this.conservativeRiskManager = manager; return this; }
        public Builder neutralRiskManager(NeutralRiskManager manager) { this.neutralRiskManager = manager; return this; }
        public Builder bullMemory(FinancialSituationMemory memory) { this.bullMemory = memory; return this; }
        public Builder bearMemory(FinancialSituationMemory memory) { this.bearMemory = memory; return this; }
        public Builder traderMemory(FinancialSituationMemory memory) { this.traderMemory = memory; return this; }
        public Builder investJudgeMemory(FinancialSituationMemory memory) { this.investJudgeMemory = memory; return this; }
        public Builder portfolioManagerMemory(FinancialSituationMemory memory) { this.portfolioManagerMemory = memory; return this; }

        public TradingAgentsGraph build() {
            return new TradingAgentsGraph(this);
        }
    }
}
