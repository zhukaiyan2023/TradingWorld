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

/**
 * 股票推荐控制器。
 * 提供股票分析和推荐的核心 API。
 *
 * <p>主要功能：
 * <ul>
 *   <li>股票分析 - 触发完整的 AI 多智能体分析流程</li>
 *   <li>返回综合交易建议</li>
 * </ul>
 *
 * @see StockTradingFacade 股票交易门面服务
 * @see RecommendationAssembler 推荐DTO转换器
 * @see RecommendationDTO 推荐数据传输对象
 */
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