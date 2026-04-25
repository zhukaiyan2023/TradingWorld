package com.tradingworld.cli;

import com.tradingworld.domain.service.StockTradingFacade;
import com.tradingworld.graph.state.AgentState;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class StockAnalysisService {

    private final StockTradingFacade stockTradingFacade;

    public StockAnalysisService(StockTradingFacade stockTradingFacade) {
        this.stockTradingFacade = stockTradingFacade;
    }

    public AgentState analyze(String symbol, String tradeDate) {
        LocalDate date = LocalDate.parse(tradeDate);
        var result = stockTradingFacade.execute(symbol, date);

        return AgentState.builder()
                .companyOfInterest(symbol)
                .tradeDate(tradeDate)
                .marketReport(result.report().getMarketAnalysis())
                .sentimentReport(result.report().getSentimentAnalysis())
                .newsReport(result.report().getNewsAnalysis())
                .fundamentalsReport(result.report().getFundamentalsAnalysis())
                .finalTradeDecision(result.decision().getDecision())
                .traderInvestmentPlan(result.decision().getDecision())
                .build();
    }
}
