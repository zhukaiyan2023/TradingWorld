package com.tradingworld.dataflows;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
