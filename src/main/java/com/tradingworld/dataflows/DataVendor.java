package com.tradingworld.dataflows;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 数据供应商接口（例如：Yahoo Finance、Alpha Vantage）。
 * 每个供应商实现特定数据类型的数据获取。
 */
public interface DataVendor {

    /**
     * 获取供应商名称。
     */
    String getName();

    /**
     * 检查此供应商是否可用/已配置。
     */
    boolean isAvailable();

    /**
     * 获取股票的实时价格。
     */
    Optional<StockQuote> getStockQuote(String symbol);

    /**
     * 批量获取多个股票的实时价格。
     *
     * @param symbols 股票代码列表
     * @return 股票报价列表
     */
    Optional<List<StockQuote>> getStockQuotes(List<String> symbols);

    /**
     * 获取历史OHLCV数据。
     */
    Optional<List<Candle>> getHistorical(String symbol, String period);

    /**
     * 获取资产负债表数据。
     */
    Optional<BalanceSheet> getBalanceSheet(String symbol);

    /**
     * 获取利润表数据。
     */
    Optional<IncomeStatement> getIncomeStatement(String symbol);

    /**
     * 获取现金流量表数据。
     */
    Optional<Cashflow> getCashflow(String symbol);

    /**
     * 获取内幕交易信息。
     */
    Optional<List<InsiderTransaction>> getInsiderTransactions(String symbol);

    /**
     * 获取股票的新闻。
     */
    Optional<List<NewsArticle>> getNews(String symbol);

    /**
     * 获取热门/趋势股票。
     *
     * @param limit 返回数量限制
     * @return 热门股票列表
     */
    Optional<List<TrendingTicker>> getTrendingTickers(int limit);

    /**
     * 获取市场涨跌幅榜股票。
     *
     * @param type 类型: "gainers"(涨幅榜), "losers"(跌幅榜), "active"(成交量榜)
     * @param limit 返回数量限制
     * @return 股票列表
     */
    Optional<List<StockQuote>> getMarketMovers(String type, int limit);

    /**
     * 按条件筛选股票。
     *
     * @param filter 筛选条件
     * @return 符合条件股票列表
     */
    Optional<List<StockQuote>> screenStocks(StockFilter filter);

    // ==================== 数据类 ====================

    record StockQuote(
        String symbol,
        double price,
        double change,
        double changePercent,
        long volume,
        LocalDateTime timestamp
    ) {}

    record Candle(
        LocalDateTime datetime,
        double open,
        double high,
        double low,
        double close,
        long volume
    ) {}

    record BalanceSheet(
        String symbol,
        LocalDateTime reportDate,
        double totalAssets,
        double totalLiabilities,
        double totalEquity
    ) {}

    record IncomeStatement(
        String symbol,
        LocalDateTime reportDate,
        double revenue,
        double netIncome,
        double eps
    ) {}

    record Cashflow(
        String symbol,
        LocalDateTime reportDate,
        double operatingCashflow,
        double investingCashflow,
        double financingCashflow
    ) {}

    record InsiderTransaction(
        String symbol,
        String insiderName,
        String transactionType,
        int shares,
        double price,
        LocalDateTime date
    ) {}

    record NewsArticle(
        String title,
        String content,
        String url,
        String source,
        LocalDateTime publishTime
    ) {}

    /**
     * 热门/趋势股票数据。
     */
    record TrendingTicker(
        String symbol,
        String name,
        double price,
        double changePercent,
        long volume,
        int rank
    ) {}

    /**
     * 股票筛选条件。
     */
    record StockFilter(
        Double minPrice,
        Double maxPrice,
        Long minVolume,
        Double minMarketCap,
        Double minPe,
        Double maxPe,
        String sector,
        String exchange
    ) {}
}
