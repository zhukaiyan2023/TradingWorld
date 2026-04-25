package com.tradingworld.infra.gateway.mysql;

import com.tradingworld.domain.do.portfolio.PortfolioDO;
import com.tradingworld.domain.do.portfolio.PerformanceDO;
import com.tradingworld.domain.gateway.PortfolioGateway;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Component
public class PortfolioMySQLGateway implements PortfolioGateway {

    @Override
    public PortfolioDO getPortfolio(String portfolioId) {
        // TODO: Implement with PortfolioMapper
        return null;
    }

    @Override
    public List<PortfolioDO> listPortfolios() {
        // TODO: Implement with PortfolioMapper
        return Collections.emptyList();
    }

    @Override
    public PerformanceDO getPerformance(String portfolioId, LocalDate start, LocalDate end) {
        // TODO: Implement with PerformanceMapper
        return null;
    }
}