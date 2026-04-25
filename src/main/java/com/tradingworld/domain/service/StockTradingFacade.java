package com.tradingworld.domain.service;

import com.tradingworld.domain.do.analysis.RiskAssessmentDO;
import com.tradingworld.domain.do.trading.TradeDecisionDO;
import com.tradingworld.domain.do.analysis.AnalystReportDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class StockTradingFacade {
    private static final Logger log = LoggerFactory.getLogger(StockTradingFacade.class);

    private final QuoteAnalysisService quoteAnalysisService;
    private final TradingDecisionService tradingDecisionService;
    private final RiskEvaluationService riskEvaluationService;

    public StockTradingFacade(
            QuoteAnalysisService quoteAnalysisService,
            TradingDecisionService tradingDecisionService,
            RiskEvaluationService riskEvaluationService) {
        this.quoteAnalysisService = quoteAnalysisService;
        this.tradingDecisionService = tradingDecisionService;
        this.riskEvaluationService = riskEvaluationService;
    }

    public TradingResult execute(String symbol, LocalDate date) {
        log.info("Executing complete trading workflow for {} on {}", symbol, date);

        // Step 1: Quote Analysis
        AnalystReportDO report = quoteAnalysisService.analyze(symbol, date);

        // Step 2: Trading Decision
        TradeDecisionDO decision = tradingDecisionService.makeDecision(
            symbol, date.toString(), report);

        // Step 3: Risk Evaluation
        RiskAssessmentDO assessment = riskEvaluationService.evaluate(decision);

        return new TradingResult(report, decision, assessment);
    }

    public record TradingResult(
            AnalystReportDO report,
            TradeDecisionDO decision,
            RiskAssessmentDO assessment) {}
}