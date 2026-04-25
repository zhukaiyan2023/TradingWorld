package com.tradingworld.domain.service;

import com.tradingworld.domain.do.trading.TradeDecisionDO;
import com.tradingworld.domain.do.analysis.AnalystReportDO;
import com.tradingworld.agents.researchers.BullResearcher;
import com.tradingworld.agents.researchers.BearResearcher;
import com.tradingworld.agents.trader.Trader;
import com.tradingworld.graph.state.AgentState;
import com.tradingworld.graph.state.InvestDebateState;
import com.tradingworld.infra.config.TradingProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TradingDecisionService {
    private static final Logger log = LoggerFactory.getLogger(TradingDecisionService.class);

    private final BullResearcher bullResearcher;
    private final BearResearcher bearResearcher;
    private final Trader trader;
    private final TradingProperties tradingProperties;

    public TradingDecisionService(
            BullResearcher bullResearcher,
            BearResearcher bearResearcher,
            Trader trader,
            TradingProperties tradingProperties) {
        this.bullResearcher = bullResearcher;
        this.bearResearcher = bearResearcher;
        this.trader = trader;
        this.tradingProperties = tradingProperties;
    }

    public TradeDecisionDO makeDecision(String symbol, String date, AnalystReportDO report) {
        log.info("Starting trading decision for {} on {}", symbol, date);
        AgentState state = createState(symbol, date, report);
        runInvestmentDebate(state);
        trader.execute(state);
        return buildTradeDecision(state);
    }

    private AgentState createState(String symbol, String date, AnalystReportDO report) {
        AgentState state = AgentState.builder()
                .companyOfInterest(symbol)
                .tradeDate(date)
                .marketReport(report.getMarketAnalysis())
                .sentimentReport(report.getSentimentAnalysis())
                .newsReport(report.getNewsAnalysis())
                .fundamentalsReport(report.getFundamentalsAnalysis())
                .investmentDebateState(new InvestDebateState())
                .build();
        return state;
    }

    private void runInvestmentDebate(AgentState state) {
        InvestDebateState debateState = state.getInvestmentDebateState();
        int maxRounds = tradingProperties.getTrading().getMaxDebateRounds();
        for (int round = 0; round < maxRounds; round++) {
            debateState.incrementCount();
            bullResearcher.execute(state);
            bearResearcher.execute(state);
            makeInvestmentJudgeDecision(state);
        }
    }

    private void makeInvestmentJudgeDecision(AgentState state) {
        // LLM-based decision logic handled by agents
        state.getInvestmentDebateState().setJudgeDecision(
            "Investment decision based on bull/bear debate analysis");
    }

    private TradeDecisionDO buildTradeDecision(AgentState state) {
        return TradeDecisionDO.builder()
                .symbol(state.getCompanyOfInterest())
                .tradeDate(state.getTradeDate())
                .decision(state.getFinalTradeDecision())
                .bullCase(state.getInvestmentPlan())
                .bearCase(state.getTraderInvestmentPlan())
                .build();
    }
}