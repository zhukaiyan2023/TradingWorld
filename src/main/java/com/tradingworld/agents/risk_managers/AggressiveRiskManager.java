package com.tradingworld.agents.risk_managers;

import com.tradingworld.agents.BaseAgent;
import com.tradingworld.graph.state.AgentState;
import com.tradingworld.graph.state.RiskDebateState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 激进型风险管理器智能体，主张高风险高回报策略。
 * 专注于以较大仓位和较少对冲来最大化回报。
 */
public class AggressiveRiskManager implements BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(AggressiveRiskManager.class);

    private static final String NAME = "AggressiveRiskManager";
    private static final String SYSTEM_PROMPT = """
        你是一位激进型风险管理器，主张高风险高回报策略。
        你的职责是在接受更高波动性的同时，评估投资机会的潜在上行空间。
        你建议更大的仓位和最小化的对冲。
        你在贪婪与理性之间取得平衡，当看涨理由强劲时追求最大回报。
        考虑因素：更高回报、更大仓位、杠杆机会、以增长为中心的配置。
        """;

    public AggressiveRiskManager() {
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
        log.info("Executing Aggressive Risk Manager for: {}", state.getCompanyOfInterest());
        try {
            RiskDebateState debateState = state.getRiskDebateState();

            // 构建激进型风险评估
            String riskAssessment = buildAggressiveAssessment(state);

            // 更新辩论状态
            debateState.appendAggressiveHistory(NAME + ": " + riskAssessment);
            debateState.setCurrentAggressiveResponse(riskAssessment);
            debateState.setLatestSpeaker(NAME);

            state.setRiskDebateState(debateState);

            return riskAssessment;
        } catch (Exception e) {
            log.error("Error executing Aggressive Risk Manager: {}", e.getMessage(), e);
            return "Error: Failed to build aggressive risk assessment - " + e.getMessage();
        }
    }

    private String buildAggressiveAssessment(AgentState state) {
        return String.format("""
            AGGRESSIVE RISK ASSESSMENT for %s
            ==================================

            Investment Plan Under Review:
            %s

            Trader's Rationale:
            %s

            Aggressive Risk Analysis:
            [To be filled by LLM with aggressive perspective]

            Risk Tolerance: HIGH
            Recommended Position Size: 15-25%% of portfolio
            Hedging: MINIMAL
            Stop Loss: 10-15%% below entry

            Recommendation: AGGRESSIVE - Full allocation to upside opportunity
            """,
            state.getCompanyOfInterest(),
            state.getInvestmentPlan(),
            state.getTraderInvestmentPlan());
    }
}
