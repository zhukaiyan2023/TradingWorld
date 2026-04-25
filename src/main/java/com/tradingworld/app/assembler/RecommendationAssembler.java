package com.tradingworld.app.assembler;

import com.tradingworld.domain.dom.analysis.AnalystReportDO;
import com.tradingworld.domain.dom.analysis.RiskAssessmentDO;
import com.tradingworld.domain.dom.quote.StockSpotDO;
import com.tradingworld.domain.gateway.QuoteGateway;
import com.tradingworld.domain.service.StockTradingFacade.TradingResult;
import com.tradingworld.dto.RecommendationDTO;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

/**
 * 推荐 DTO 转换器。
 * 负责将交易结果（分析报告、交易决策、风险评估）转换为 API 传输用的推荐 DTO。
 *
 * @see TradingResult 交易结果
 * @see RecommendationDTO 推荐数据传输对象
 * @see QuoteGateway 行情网关接口
 */
@Component
public class RecommendationAssembler {

    private final QuoteGateway quoteGateway;

    public RecommendationAssembler(QuoteGateway quoteGateway) {
        this.quoteGateway = quoteGateway;
    }

    public RecommendationDTO toRecommendationDTO(TradingResult result) {
        String symbol = result.report().getSymbol();
        String name = getCompanyName(symbol);

        return new RecommendationDTO(
            symbol,
            name,
            parseAction(result.decision().getDecision()),
            buildSummary(result),
            LocalDate.parse(result.report().getTradeDate()),
            result.assessment().getRiskLevel()
        );
    }

    private String getCompanyName(String symbol) {
        StockSpotDO spot = quoteGateway.getSpot(symbol);
        return spot != null && spot.getName() != null ? spot.getName() : symbol;
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
