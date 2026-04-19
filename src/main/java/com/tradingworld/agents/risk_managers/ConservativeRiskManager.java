package com.tradingworld.agents.risk_managers;

import com.tradingworld.agents.BaseAgent;
import com.tradingworld.graph.state.AgentState;
import com.tradingworld.graph.state.RiskDebateState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 保守型风险管理器智能体，主张低风险资本保值策略。
 * 专注于以较小仓位和广泛对冲来最小化损失。
 */
public class ConservativeRiskManager implements BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(ConservativeRiskManager.class);

    private static final String NAME = "ConservativeRiskManager";
    private static final String SYSTEM_PROMPT = """
        你是一位保守型风险管理器，主张资本保值和风险最小化。
        你的职责是评估投资机会的潜在下行空间，强调风险调整后的回报而非原始回报。
        你建议更小的仓位和广泛的对冲。
        你在恐惧与机会之间取得平衡，确保资本首先得到保护。
        考虑因素：资本保值、更低波动性、对冲、以价值为中心的配置。
        """;

    public ConservativeRiskManager() {
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getSystemPrompt() {
        return SYSTEM_PROMPT;
    }

    @Override
    public String execute(AgentState state) {
        log.info("Executing Conservative Risk Manager for: {}", state.getCompanyOfInterest());
        try {
            RiskDebateState debateState = state.getRiskDebateState();

            // 构建保守型风险评估
            String riskAssessment = buildConservativeAssessment(state);

            // 更新辩论状态
            debateState.appendConservativeHistory(NAME + ": " + riskAssessment);
            debateState.setCurrentConservativeResponse(riskAssessment);
            debateState.setLatestSpeaker(NAME);

            state.setRiskDebateState(debateState);

            return riskAssessment;
        } catch (Exception e) {
            log.error("Error executing Conservative Risk Manager: {}", e.getMessage(), e);
            return "Error: Failed to build conservative risk assessment - " + e.getMessage();
        }
    }

    private String buildConservativeAssessment(AgentState state) {
        return String.format("""
            CONSERVATIVE RISK ASSESSMENT for %s
            ====================================

            Investment Plan Under Review:
            %s

            Trader's Rationale:
            %s

            Conservative Risk Analysis:
            [To be filled by LLM with conservative perspective]

            Risk Tolerance: LOW
            Recommended Position Size: 5-10%% of portfolio
            Hedging: EXTENSIVE
            Stop Loss: 5-8%% below entry

            Recommendation: CONSERVATIVE - Minimal allocation, maximum protection
            """,
            state.getCompanyOfInterest(),
            state.getInvestmentPlan(),
            state.getTraderInvestmentPlan());
    }
}
