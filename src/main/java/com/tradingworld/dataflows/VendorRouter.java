package com.tradingworld.dataflows;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * 将数据请求路由到可用供应商，并具有自动故障转移功能。
 * 按顺序尝试供应商，直到有一个返回数据。
 */
public class VendorRouter {

    private static final Logger log = LoggerFactory.getLogger(VendorRouter.class);

    private final List<DataVendor> vendors;

    public VendorRouter(List<DataVendor> vendors) {
        this.vendors = vendors;
    }

    public String getName() {
        return "VendorRouter";
    }

    public Optional<DataVendor.StockQuote> getStockQuote(String symbol) {
        for (DataVendor vendor : vendors) {
            if (!vendor.isAvailable()) continue;
            try {
                Optional<DataVendor.StockQuote> result = vendor.getStockQuote(symbol);
                if (result.isPresent()) {
                    log.debug("Got stock quote from vendor: {}", vendor.getName());
                    return result;
                }
            } catch (Exception e) {
                log.warn("Vendor {} failed for {}: {}", vendor.getName(), symbol, e.getMessage());
            }
        }
        return Optional.empty();
    }

    public Optional<List<DataVendor.StockQuote>> getStockQuotes(List<String> symbols) {
        for (DataVendor vendor : vendors) {
            if (!vendor.isAvailable()) continue;
            try {
                Optional<List<DataVendor.StockQuote>> result = vendor.getStockQuotes(symbols);
                if (result.isPresent() && !result.get().isEmpty()) {
                    log.debug("Got stock quotes from vendor: {}", vendor.getName());
                    return result;
                }
            } catch (Exception e) {
                log.warn("Vendor {} failed for batch quotes: {}", vendor.getName(), e.getMessage());
            }
        }
        return Optional.empty();
    }

    public Optional<List<DataVendor.Candle>> getHistorical(String symbol, String period) {
        for (DataVendor vendor : vendors) {
            if (!vendor.isAvailable()) continue;
            try {
                Optional<List<DataVendor.Candle>> result = vendor.getHistorical(symbol, period);
                if (result.isPresent()) {
                    log.debug("Got historical data from vendor: {}", vendor.getName());
                    return result;
                }
            } catch (Exception e) {
                log.warn("Vendor {} failed for {}: {}", vendor.getName(), symbol, e.getMessage());
            }
        }
        return Optional.empty();
    }

    public Optional<DataVendor.BalanceSheet> getBalanceSheet(String symbol) {
        for (DataVendor vendor : vendors) {
            if (!vendor.isAvailable()) continue;
            try {
                Optional<DataVendor.BalanceSheet> result = vendor.getBalanceSheet(symbol);
                if (result.isPresent()) {
                    log.debug("Got balance sheet from vendor: {}", vendor.getName());
                    return result;
                }
            } catch (Exception e) {
                log.warn("Vendor {} failed for {}: {}", vendor.getName(), symbol, e.getMessage());
            }
        }
        return Optional.empty();
    }

    public Optional<DataVendor.IncomeStatement> getIncomeStatement(String symbol) {
        for (DataVendor vendor : vendors) {
            if (!vendor.isAvailable()) continue;
            try {
                Optional<DataVendor.IncomeStatement> result = vendor.getIncomeStatement(symbol);
                if (result.isPresent()) {
                    log.debug("Got income statement from vendor: {}", vendor.getName());
                    return result;
                }
            } catch (Exception e) {
                log.warn("Vendor {} failed for {}: {}", vendor.getName(), symbol, e.getMessage());
            }
        }
        return Optional.empty();
    }

    public Optional<DataVendor.Cashflow> getCashflow(String symbol) {
        for (DataVendor vendor : vendors) {
            if (!vendor.isAvailable()) continue;
            try {
                Optional<DataVendor.Cashflow> result = vendor.getCashflow(symbol);
                if (result.isPresent()) {
                    log.debug("Got cashflow from vendor: {}", vendor.getName());
                    return result;
                }
            } catch (Exception e) {
                log.warn("Vendor {} failed for {}: {}", vendor.getName(), symbol, e.getMessage());
            }
        }
        return Optional.empty();
    }

    public Optional<List<DataVendor.InsiderTransaction>> getInsiderTransactions(String symbol) {
        for (DataVendor vendor : vendors) {
            if (!vendor.isAvailable()) continue;
            try {
                Optional<List<DataVendor.InsiderTransaction>> result = vendor.getInsiderTransactions(symbol);
                if (result.isPresent()) {
                    log.debug("Got insider transactions from vendor: {}", vendor.getName());
                    return result;
                }
            } catch (Exception e) {
                log.warn("Vendor {} failed for {}: {}", vendor.getName(), symbol, e.getMessage());
            }
        }
        return Optional.empty();
    }

    public Optional<List<DataVendor.NewsArticle>> getNews(String symbol) {
        for (DataVendor vendor : vendors) {
            if (!vendor.isAvailable()) continue;
            try {
                Optional<List<DataVendor.NewsArticle>> result = vendor.getNews(symbol);
                if (result.isPresent()) {
                    log.debug("Got news from vendor: {}", vendor.getName());
                    return result;
                }
            } catch (Exception e) {
                log.warn("Vendor {} failed for {}: {}", vendor.getName(), symbol, e.getMessage());
            }
        }
        return Optional.empty();
    }

    /**
     * 获取热门/趋势股票。
     * 从所有可用供应商获取并合并结果。
     */
    public Optional<List<DataVendor.TrendingTicker>> getTrendingTickers(int limit) {
        List<DataVendor.TrendingTicker> allTrending = new ArrayList<>();

        for (DataVendor vendor : vendors) {
            if (!vendor.isAvailable()) continue;
            try {
                Optional<List<DataVendor.TrendingTicker>> result = vendor.getTrendingTickers(limit);
                if (result.isPresent()) {
                    allTrending.addAll(result.get());
                    log.debug("Got {} trending tickers from vendor: {}",
                        result.get().size(), vendor.getName());
                }
            } catch (Exception e) {
                log.warn("Vendor {} failed for trending tickers: {}",
                    vendor.getName(), e.getMessage());
            }
        }

        if (allTrending.isEmpty()) {
            return Optional.empty();
        }

        // 按成交量降序排序，取前 limit 个
        allTrending.sort(Comparator.comparingLong(DataVendor.TrendingTicker::volume).reversed());
        return Optional.of(allTrending.stream().limit(limit).toList());
    }

    /**
     * 获取市场涨跌幅榜股票。
     * 从所有可用供应商获取并合并结果。
     */
    public Optional<List<DataVendor.StockQuote>> getMarketMovers(String type, int limit) {
        List<DataVendor.StockQuote> allMovers = new ArrayList<>();

        for (DataVendor vendor : vendors) {
            if (!vendor.isAvailable()) continue;
            try {
                Optional<List<DataVendor.StockQuote>> result = vendor.getMarketMovers(type, limit);
                if (result.isPresent()) {
                    allMovers.addAll(result.get());
                    log.debug("Got {} market movers from vendor: {}",
                        result.get().size(), vendor.getName());
                }
            } catch (Exception e) {
                log.warn("Vendor {} failed for market movers: {}",
                    vendor.getName(), e.getMessage());
            }
        }

        if (allMovers.isEmpty()) {
            return Optional.empty();
        }

        // 根据类型排序
        if ("gainers".equalsIgnoreCase(type)) {
            allMovers.sort(Comparator.comparingDouble(DataVendor.StockQuote::changePercent).reversed());
        } else if ("losers".equalsIgnoreCase(type)) {
            allMovers.sort(Comparator.comparingDouble(DataVendor.StockQuote::changePercent));
        } else { // active
            allMovers.sort(Comparator.comparingLong(DataVendor.StockQuote::volume).reversed());
        }

        return Optional.of(allMovers.stream().limit(limit).toList());
    }

    /**
     * 按条件筛选股票。
     */
    public Optional<List<DataVendor.StockQuote>> screenStocks(DataVendor.StockFilter filter) {
        for (DataVendor vendor : vendors) {
            if (!vendor.isAvailable()) continue;
            try {
                Optional<List<DataVendor.StockQuote>> result = vendor.screenStocks(filter);
                if (result.isPresent() && !result.get().isEmpty()) {
                    log.debug("Got {} screened stocks from vendor: {}",
                        result.get().size(), vendor.getName());
                    return result;
                }
            } catch (Exception e) {
                log.warn("Vendor {} failed for stock screening: {}",
                    vendor.getName(), e.getMessage());
            }
        }
        return Optional.empty();
    }
}
