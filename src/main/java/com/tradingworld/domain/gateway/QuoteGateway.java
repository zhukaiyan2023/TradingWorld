package com.tradingworld.domain.gateway;

import com.tradingworld.domain.dom.quote.StockSpotDO;
import com.tradingworld.domain.dom.quote.StockDailyDO;
import java.time.LocalDate;
import java.util.List;

/**
 * 行情网关接口。
 * 定义股票行情数据的查询能力，包括实时行情和历史 K 线数据。
 *
 * <p>实现类：
 * <ul>
 *   <li>{@link com.tradingworld.infra.gateway.mysql.QuoteMySQLGateway} - MySQL 数据库实现</li>
 *   <li>{@link com.tradingworld.infra.gateway.external.AlphaVantageGateway} - AlphaVantage API 实现</li>
 * </ul>
 *
 * @see StockSpotDO 股票实时行情
 * @see StockDailyDO 股票日 K 线数据
 */
public interface QuoteGateway {

    /**
     * 获取股票实时行情。
     *
     * @param symbol 股票代码
     * @return 股票实时行情，若不存在返回 null
     */
    StockSpotDO getSpot(String symbol);

    /**
     * 批量获取股票实时行情。
     *
     * @param symbols 股票代码列表
     * @return 股票实时行情列表
     */
    List<StockSpotDO> getSpotList(List<String> symbols);

    /**
     * 获取股票历史日 K 线数据。
     *
     * @param symbol 股票代码
     * @param start  开始日期
     * @param end    结束日期
     * @return 日 K 线数据列表
     */
    List<StockDailyDO> getDaily(String symbol, LocalDate start, LocalDate end);

    /**
     * 获取指定日期的股票日 K 线数据。
     *
     * @param symbol 股票代码
     * @param date   交易日期
     * @return 日 K 线数据，若不存在返回 null
     */
    StockDailyDO getDailySingle(String symbol, LocalDate date);
}