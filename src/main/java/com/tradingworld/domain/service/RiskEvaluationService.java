package com.tradingworld.domain.service;

import com.tradingworld.domain.do.analysis.RiskAssessmentDO;
import com.tradingworld.domain.do.trading.TradeDecisionDO;
import com.tradingworld.agents.risk_managers.AggressiveRiskManager;
import com.tradingworld.agents.risk_managers.ConservativeRiskManager;
import com.tradingworld.agents.risk_managers.NeutralRiskManager;
import com.tradingworld.graph.state.AgentState;
import com.tradingworld.graph.state.RiskDebateState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

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