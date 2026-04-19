package com.tradingworld.agents.risk_managers;

import com.tradingworld.agents.BaseAgent;
import com.tradingworld.graph.state.AgentState;
import com.tradingworld.graph.state.RiskDebateState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 中性风险管理器智能体，在激进和保守策略之间取得平衡。
 * 专注于以均衡仓位规模实现中等风险调整回报。
 */
public class NeutralRiskManager implements BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(NeutralRiskManager.class);

    private static final String NAME = "NeutralRiskManager";
    private static final String SYSTEM_PROMPT = """
        你是一位中性风险管理器，在激进和保守观点之间取得平衡。
        你的职责是客观地评估投资机会的风险回报比，建议中等仓位和平衡对冲。
        你寻求风险调整后的回报。
        你平衡双方，提供审慎的风险管理方法。
        考虑因素：均衡配置、适度对冲、多元化的风险敞口。
        """;

    public NeutralRiskManager() {
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
        log.info("Executing Neutral Risk Manager for: {}", state.getCompanyOfInterest());
        try {
            RiskDebateState debateState = state.getRiskDebateState();

            // 构建中性风险评估
            String riskAssessment = buildNeutralAssessment(state);

            // 更新辩论状态
            debateState.appendNeutralHistory(NAME + ": " + riskAssessment);
            debateState.setCurrentNeutralResponse(riskAssessment);
            debateState.setLatestSpeaker(NAME);

            state.setRiskDebateState(debateState);

            return riskAssessment;
        } catch (Exception e) {
            log.error("Error executing Neutral Risk Manager: {}", e.getMessage(), e);
            return "Error: Failed to build neutral risk assessment - " + e.getMessage();
        }
    }

    private String buildNeutralAssessment(AgentState state) {
        return String.format("""
            NEUTRAL RISK ASSESSMENT for %s
            ===============================

            Investment Plan Under Review:
            %s

            Trader's Rationale:
            %s

            Neutral Risk Analysis:
            [To be filled by LLM with balanced perspective]

            Risk Tolerance: MODERATE
            Recommended Position Size: 10-15%% of portfolio
            Hedging: MODERATE
            Stop Loss: 8-10%% below entry

            Recommendation: NEUTRAL - Balanced approach with measured risk
            """,
            state.getCompanyOfInterest(),
            state.getInvestmentPlan(),
            state.getTraderInvestmentPlan());
    }
}
