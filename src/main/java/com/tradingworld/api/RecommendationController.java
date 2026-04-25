package com.tradingworld.api;

import com.tradingworld.domain.dom.analysis.AnalystReportDO;
import com.tradingworld.domain.gateway.AnalysisGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

/**
 * 推荐控制器。
 * 提供分析师报告和历史推荐数据的查询接口。
 *
 * @see AnalystReportDO 分析师报告
 * @see AnalysisGateway 分析网关接口
 */
@RestController
@RequestMapping("/api/v1/recommendation")
public class RecommendationController {

    private final AnalysisGateway analysisGateway;

    public RecommendationController(AnalysisGateway analysisGateway) {
        this.analysisGateway = analysisGateway;
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<AnalystReportDO> getReport(
            @PathVariable String symbol,
            @RequestParam LocalDate date) {
        var report = analysisGateway.getReport(symbol, date);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(report);
    }

    @GetMapping("/{symbol}/history")
    public ResponseEntity<List<AnalystReportDO>> getReportHistory(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "30") int days) {
        var reports = analysisGateway.getReports(symbol, days);
        return ResponseEntity.ok(reports);
    }
}
