package com.tradingworld.domain.service;

import com.tradingworld.domain.do.analysis.AnalystReportDO;
import com.tradingworld.agents.analysts.MarketAnalyst;
import com.tradingworld.agents.analysts.SentimentAnalyst;
import com.tradingworld.agents.analysts.NewsAnalyst;
import com.tradingworld.agents.analysts.FundamentalsAnalyst;
import com.tradingworld.graph.state.AgentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

@Service
public class QuoteAnalysisService {
    private static final Logger log = LoggerFactory.getLogger(QuoteAnalysisService.class);

    private final MarketAnalyst marketAnalyst;
    private final SentimentAnalyst sentimentAnalyst;
    private final NewsAnalyst newsAnalyst;
    private final FundamentalsAnalyst fundamentalsAnalyst;

    public QuoteAnalysisService(
            MarketAnalyst marketAnalyst,
            SentimentAnalyst sentimentAnalyst,
            NewsAnalyst newsAnalyst,
            FundamentalsAnalyst fundamentalsAnalyst) {
        this.marketAnalyst = marketAnalyst;
        this.sentimentAnalyst = sentimentAnalyst;
        this.newsAnalyst = newsAnalyst;
        this.fundamentalsAnalyst = fundamentalsAnalyst;
    }

    public AnalystReportDO analyze(String symbol, LocalDate date) {
        log.info("Starting quote analysis for {} on {}", symbol, date);
        AgentState state = createInitialState(symbol, date);
        runAnalysts(state);
        return buildAnalystReport(state);
    }

    private AgentState createInitialState(String symbol, LocalDate date) {
        return AgentState.builder()
                .companyOfInterest(symbol)
                .tradeDate(date.toString())
                .sender("system")
                .build();
    }

    private void runAnalysts(AgentState state) {
        try {
            CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> marketAnalyst.execute(state)),
                CompletableFuture.runAsync(() -> sentimentAnalyst.execute(state)),
                CompletableFuture.runAsync(() -> newsAnalyst.execute(state)),
                CompletableFuture.runAsync(() -> fundamentalsAnalyst.execute(state))
            ).join();
        } catch (Exception e) {
            log.error("Error running analysts", e);
        }
    }

    private AnalystReportDO buildAnalystReport(AgentState state) {
        return AnalystReportDO.builder()
                .symbol(state.getCompanyOfInterest())
                .tradeDate(state.getTradeDate())
                .marketAnalysis(state.getMarketReport())
                .sentimentAnalysis(state.getSentimentReport())
                .newsAnalysis(state.getNewsReport())
                .fundamentalsAnalysis(state.getFundamentalsReport())
                .build();
    }
}