package com.tradingworld.api;

import com.tradingworld.domain.dom.portfolio.PortfolioDO;
import com.tradingworld.domain.dom.portfolio.PerformanceDO;
import com.tradingworld.domain.gateway.PortfolioGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

/**
 * 投资组合控制器。
 * 提供投资组合信息和业绩数据的查询接口。
 *
 * @see PortfolioDO 投资组合
 * @see PerformanceDO 业绩表现
 * @see PortfolioGateway 组合网关接口
 */
@RestController
@RequestMapping("/api/v1/portfolio")
public class PortfolioController {

    private final PortfolioGateway portfolioGateway;

    public PortfolioController(PortfolioGateway portfolioGateway) {
        this.portfolioGateway = portfolioGateway;
    }

    @GetMapping("/{portfolioId}")
    public ResponseEntity<PortfolioDO> getPortfolio(@PathVariable String portfolioId) {
        var portfolio = portfolioGateway.getPortfolio(portfolioId);
        if (portfolio == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(portfolio);
    }

    @GetMapping
    public ResponseEntity<List<PortfolioDO>> listPortfolios() {
        var portfolios = portfolioGateway.listPortfolios();
        return ResponseEntity.ok(portfolios);
    }

    @GetMapping("/{portfolioId}/performance")
    public ResponseEntity<PerformanceDO> getPerformance(
            @PathVariable String portfolioId,
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {
        var performance = portfolioGateway.getPerformance(portfolioId, start, end);
        if (performance == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(performance);
    }
}
