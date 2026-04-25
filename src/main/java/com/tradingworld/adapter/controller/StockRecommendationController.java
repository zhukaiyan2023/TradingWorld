package com.tradingworld.adapter.controller;

import com.tradingworld.cmd.trade.AnalyzeStockCmd;
import com.tradingworld.domain.service.StockTradingFacade;
import com.tradingworld.app.assembler.RecommendationAssembler;
import com.tradingworld.dto.RecommendationDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/stock")
public class StockRecommendationController {
    private static final Logger log = LoggerFactory.getLogger(StockRecommendationController.class);

    private final StockTradingFacade stockTradingFacade;
    private final RecommendationAssembler recommendationAssembler;

    public StockRecommendationController(
            StockTradingFacade stockTradingFacade,
            RecommendationAssembler recommendationAssembler) {
        this.stockTradingFacade = stockTradingFacade;
        this.recommendationAssembler = recommendationAssembler;
    }

    @PostMapping("/analyze")
    public ResponseEntity<RecommendationDTO> analyze(@Valid @RequestBody AnalyzeStockCmd cmd) {
        log.info("Received analyze request for {} on {}", cmd.getSymbol(), cmd.getTradeDate());
        try {
            var result = stockTradingFacade.execute(cmd.getSymbol(), cmd.getTradeDate());
            var response = recommendationAssembler.toRecommendationDTO(result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error analyzing stock", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}