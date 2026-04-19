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

    // 数据类

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
}
