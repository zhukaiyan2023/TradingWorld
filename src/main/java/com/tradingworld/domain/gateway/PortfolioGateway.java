package com.tradingworld.domain.gateway;

import com.tradingworld.domain.do.portfolio.PortfolioDO;
import com.tradingworld.domain.do.portfolio.PerformanceDO;
import java.time.LocalDate;
import java.util.List;

public interface PortfolioGateway {
    PortfolioDO getPortfolio(String portfolioId);
    List<PortfolioDO> listPortfolios();
    PerformanceDO getPerformance(String portfolioId, LocalDate start, LocalDate end);
}