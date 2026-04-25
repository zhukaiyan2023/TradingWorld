package com.tradingworld.app.assembler;

import com.tradingworld.domain.do.analysis.AnalystReportDO;
import com.tradingworld.domain.do.trading.TradeDecisionDO;
import com.tradingworld.domain.do.analysis.RiskAssessmentDO;
import com.tradingworld.domain.service.StockTradingFacade.TradingResult;
import com.tradingworld.dto.RecommendationDTO;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class RecommendationAssembler {

    public RecommendationDTO toRecommendationDTO(TradingResult result) {
        return new RecommendationDTO(
            result.report().getSymbol(),
            result.report().getSymbol(),
            parseAction(result.decision().getDecision()),
            buildSummary(result),
            LocalDate.parse(result.report().getTradeDate()),
            result.assessment().getRiskLevel()
        );
    }

    private String parseAction(String decision) {
        if (decision == null) return "HOLD";
        String upper = decision.toUpperCase();
        if (upper.contains("BUY")) return "BUY";
        if (upper.contains("SELL")) return "SELL";
        return "HOLD";
    }

    private String buildSummary(TradingResult result) {
        return String.format("Decision: %s | Risk: %s",
            result.decision().getDecision() != null ? result.decision().getDecision() : "PENDING",
            result.assessment().getRiskLevel() != null ? result.assessment().getRiskLevel() : "UNKNOWN");
    }
}