package com.tradingworld.domain.service;

import com.tradingworld.domain.dom.analysis.RiskAssessmentDO;
import com.tradingworld.domain.dom.trading.TradeDecisionDO;
import com.tradingworld.agents.risk_managers.AggressiveRiskManager;
import com.tradingworld.agents.risk_managers.ConservativeRiskManager;
import com.tradingworld.agents.risk_managers.NeutralRiskManager;
import com.tradingworld.graph.state.AgentState;
import com.tradingworld.graph.state.RiskDebateState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

/**
 * 风险评估服务。
 * 协调激进型、保守型、中性型三位风险管理器进行风险辩论，
 * 最终生成综合风险评估报告。
 *
 * <p>工作流程：
 * <ul>
 *   <li>根据交易决策创建初始状态</li>
 *   <li>三位风险管理器并行评估风险</li>
 *   <li>汇总辩论结果生成风险评估</li>
 * </ul>
 *
 * @see RiskAssessmentDO 风险评估
 * @see TradeDecisionDO 交易决策
 * @see AggressiveRiskManager 激进型风险管理器
 * @see ConservativeRiskManager 保守型风险管理器
 * @see NeutralRiskManager 中性型风险管理器
 */
@Service
public class RiskEvaluationService {
    private static final Logger log = LoggerFactory.getLogger(RiskEvaluationService.class);

    private final AggressiveRiskManager aggressiveRiskManager;
    private final ConservativeRiskManager conservativeRiskManager;
    private final NeutralRiskManager neutralRiskManager;

    public RiskEvaluationService(
            AggressiveRiskManager aggressiveRiskManager,
            ConservativeRiskManager conservativeRiskManager,
            NeutralRiskManager neutralRiskManager) {
        this.aggressiveRiskManager = aggressiveRiskManager;
        this.conservativeRiskManager = conservativeRiskManager;
        this.neutralRiskManager = neutralRiskManager;
    }

    public RiskAssessmentDO evaluate(TradeDecisionDO decision) {
        log.info("Starting risk evaluation for {}", decision.getSymbol());
        AgentState state = createState(decision);
        runRiskDebate(state);
        return buildRiskAssessment(state);
    }

    private AgentState createState(TradeDecisionDO decision) {
        return AgentState.builder()
                .companyOfInterest(decision.getSymbol())
                .tradeDate(decision.getTradeDate())
                .finalTradeDecision(decision.getDecision())
                .riskDebateState(new RiskDebateState())
                .build();
    }

    private void runRiskDebate(AgentState state) {
        try {
            CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> aggressiveRiskManager.execute(state)),
                CompletableFuture.runAsync(() -> conservativeRiskManager.execute(state)),
                CompletableFuture.runAsync(() -> neutralRiskManager.execute(state))
            ).join();
        } catch (Exception e) {
            log.error("Error running risk managers", e);
        }
    }

    private RiskAssessmentDO buildRiskAssessment(AgentState state) {
        RiskDebateState debateState = state.getRiskDebateState();
        return RiskAssessmentDO.builder()
                .symbol(state.getCompanyOfInterest())
                .tradeDate(state.getTradeDate())
                .riskLevel(debateState.getJudgeDecision())
                .riskAssessment(debateState.getHistory())
                .riskFactors(debateState.getAggressiveHistory() + "\n" + debateState.getConservativeHistory())
                .finalDecision(state.getFinalTradeDecision())
                .build();
    }
}