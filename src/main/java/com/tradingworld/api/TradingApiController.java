package com.tradingworld.api;

import com.tradingworld.config.AppConfig;
import com.tradingworld.dto.TradeDecision;
import com.tradingworld.graph.StateLogger;
import com.tradingworld.graph.TradingAgentsGraph;
import com.tradingworld.graph.state.AgentState;
import com.tradingworld.memory.ReflectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * TradingAgents的REST API控制器。
 * 提供交易分析和健康检查的端点。
 */
@RestController
@RequestMapping("/api")
public class TradingApiController {

    private static final Logger log = LoggerFactory.getLogger(TradingApiController.class);

    private final TradingAgentsGraph tradingGraph;
    private final ReflectionManager reflectionManager;
    private final StateLogger stateLogger;

    public TradingApiController(
            TradingAgentsGraph tradingGraph,
            ReflectionManager reflectionManager,
            StateLogger stateLogger) {
        this.tradingGraph = tradingGraph;
        this.reflectionManager = reflectionManager;
        this.stateLogger = stateLogger;
    }

    /**
     * 执行公司给定日期的交易分析。
     *
     * POST /api/trade
     * Body: {"company": "NVDA", "tradeDate": "2026-01-15"}
     */
    @PostMapping("/trade")
    public ResponseEntity<TradeResponse> executeTrade(@RequestBody TradeRequest request) {
        log.info("Received trade request for {} on {}", request.company(), request.tradeDate());

        try {
            AgentState finalState = tradingGraph.propagate(request.company(), request.tradeDate());

            // 记录状态以供将来参考
            stateLogger.logState(finalState);

            TradeResponse response = new TradeResponse(
                    request.company(),
                    request.tradeDate(),
                    finalState.getFinalTradeDecision(),
                    "SUCCESS",
                    System.currentTimeMillis()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error executing trade for {}: {}", request.company(), e.getMessage(), e);
            TradeResponse errorResponse = new TradeResponse(
                    request.company(),
                    request.tradeDate(),
                    null,
                    "ERROR: " + e.getMessage(),
                    System.currentTimeMillis()
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 获取健康检查状态。
     *
     * GET /api/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "TradingAgents",
                "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * 获取特定股票代码的结果。
     *
     * GET /api/results/{ticker}
     */
    @GetMapping("/results/{ticker}")
    public ResponseEntity<AgentState> getResults(@PathVariable String ticker,
            @RequestParam(required = false) String date) {
        AgentState state = stateLogger.loadState(ticker, date);
        if (state != null) {
            return ResponseEntity.ok(state);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 记录交易结果用于反思。
     *
     * POST /api/feedback
     * Body: {"company": "NVDA", "tradeDate": "2026-01-15", "returnsLosses": 5.2}
     */
    @PostMapping("/feedback")
    public ResponseEntity<String> recordFeedback(@RequestBody FeedbackRequest request) {
        log.info("Recording feedback for {} on {}: {}%",
                request.company(), request.tradeDate(), request.returnsLosses());

        try {
            AgentState state = stateLogger.loadState(request.company(), request.tradeDate());
            if (state != null) {
                reflectionManager.reflectAndRemember(state, request.returnsLosses());
                return ResponseEntity.ok("Feedback recorded successfully");
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error recording feedback: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    // 请求/响应记录
    public record TradeRequest(String company, String tradeDate) {}
    public record TradeResponse(String company, String tradeDate, String decision, String status, long timestamp) {}
    public record FeedbackRequest(String company, String tradeDate, Double returnsLosses) {}
}
