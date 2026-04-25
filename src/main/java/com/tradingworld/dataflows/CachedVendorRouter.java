package com.tradingworld.dataflows;

import com.tradingworld.cache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * 带缓存的 VendorRouter 包装器。
 * 缓存数据供应商的响应，减少 API 调用次数。
 */
public class CachedVendorRouter {

    private static final Logger log = LoggerFactory.getLogger(CachedVendorRouter.class);

    private final VendorRouter delegate;
    private final CacheManager cache;

    // 不同类型数据的 TTL
    private static final Duration STOCK_QUOTE_TTL = Duration.ofMinutes(1);      // 股票报价缓存 1 分钟
    private static final Duration HISTORICAL_TTL = Duration.ofMinutes(5);       // 历史数据缓存 5 分钟
    private static final Duration FUNDAMENTAL_TTL = Duration.ofHours(1);       // 基本面数据缓存 1 小时
    private static final Duration NEWS_TTL = Duration.ofMinutes(10);           // 新闻缓存 10 分钟

    public CachedVendorRouter(VendorRouter delegate) {
        this(delegate, new CacheManager());
    }

    public CachedVendorRouter(VendorRouter delegate, CacheManager cache) {
        this.delegate = delegate;
        this.cache = cache;
    }

    public String getName() {
        return "CachedVendorRouter";
    }

    /**
     * 获取股票报价（带缓存）
     */
    public Optional<DataVendor.StockQuote> getStockQuote(String symbol) {
        String key = "quote:" + symbol;
        return cache.getOrLoad(key, () -> delegate.getStockQuote(symbol), STOCK_QUOTE_TTL);
    }

    /**
     * 获取历史数据（带缓存）
     */
    public Optional<List<DataVendor.Candle>> getHistorical(String symbol, String period) {
        String key = "historical:" + symbol + ":" + period;
        return cache.getOrLoad(key, () -> delegate.getHistorical(symbol, period), HISTORICAL_TTL);
    }

    /**
     * 获取资产负债表（带缓存）
     */
    public Optional<DataVendor.BalanceSheet> getBalanceSheet(String symbol) {
        String key = "balance_sheet:" + symbol;
        return cache.getOrLoad(key, () -> delegate.getBalanceSheet(symbol), FUNDAMENTAL_TTL);
    }

    /**
     * 获取利润表（带缓存）
     */
    public Optional<DataVendor.IncomeStatement> getIncomeStatement(String symbol) {
        String key = "income_statement:" + symbol;
        return cache.getOrLoad(key, () -> delegate.getIncomeStatement(symbol), FUNDAMENTAL_TTL);
    }

    /**
     * 获取现金流量表（带缓存）
     */
    public Optional<DataVendor.Cashflow> getCashflow(String symbol) {
        String key = "cashflow:" + symbol;
        return cache.getOrLoad(key, () -> delegate.getCashflow(symbol), FUNDAMENTAL_TTL);
    }

    /**
     * 获取内部人交易（带缓存）
     */
    public Optional<List<DataVendor.InsiderTransaction>> getInsiderTransactions(String symbol) {
        String key = "insider:" + symbol;
        return cache.getOrLoad(key, () -> delegate.getInsiderTransactions(symbol), FUNDAMENTAL_TTL);
    }

    /**
     * 获取新闻（带缓存）
     */
    public Optional<List<DataVendor.NewsArticle>> getNews(String symbol) {
        String key = "news:" + symbol;
        return cache.getOrLoad(key, () -> delegate.getNews(symbol), NEWS_TTL);
    }

    /**
     * 清除所有缓存
     */
    public void clearCache() {
        cache.clear();
    }

    /**
     * 获取缓存统计
     */
    public int getCacheSize() {
        return cache.size();
    }
}
