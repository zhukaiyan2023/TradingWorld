package com.tradingworld.domain.service;

import com.tradingworld.domain.dom.analysis.RiskAssessmentDO;
import com.tradingworld.domain.dom.trading.TradeDecisionDO;
import com.tradingworld.domain.dom.analysis.AnalystReportDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

/**
 * 股票交易门面服务。
 * 整合行情分析、交易决策、风险评估三大服务，
 * 提供完整的股票交易工作流程。
 *
 * <p>完整交易流程：
 * <ol>
 *   <li>行情分析 - 调用 QuoteAnalysisService 并行执行四位分析师</li>
 *   <li>交易决策 - 调用 TradingDecisionService 进行多空辩论并做出决策</li>
 *   <li>风险评估 - 调用 RiskEvaluationService 评估交易风险</li>
 * </ol>
 *
 * @see QuoteAnalysisService 行情分析服务
 * @see TradingDecisionService 交易决策服务
 * @see RiskEvaluationService 风险评估服务
 * @see TradingResult 交易结果记录
 */
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