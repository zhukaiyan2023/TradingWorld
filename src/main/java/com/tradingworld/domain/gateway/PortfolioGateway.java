package com.tradingworld.domain.gateway;

import com.tradingworld.domain.dom.portfolio.PortfolioDO;
import com.tradingworld.domain.dom.portfolio.PerformanceDO;
import java.time.LocalDate;
import java.util.List;

/**
 * 组合网关接口。
 * 定义投资组合和业绩数据的查询能力。
 *
 * @see PortfolioDO 投资组合
 * @see PerformanceDO 业绩表现
 */
public interface PortfolioGateway {

    /**
     * 获取投资组合详情。
     *
     * @param portfolioId 投资组合 ID
     * @return 投资组合信息，若不存在返回 null
     */
    PortfolioDO getPortfolio(String portfolioId);

    /**
     * 获取所有投资组合列表。
     *
     * @return 投资组合信息列表
     */
    List<PortfolioDO> listPortfolios();

    /**
     * 获取投资组合在指定时间段的业绩表现。
     *
     * @param portfolioId 投资组合 ID
     * @param start       开始日期
     * @param end         结束日期
     * @return 业绩表现数据
     */
    PerformanceDO getPerformance(String portfolioId, LocalDate start, LocalDate end);
}