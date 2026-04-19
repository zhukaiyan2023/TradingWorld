package com.tradingworld.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tradingworld.dataflows.DataVendor;
import com.tradingworld.dataflows.VendorRouter;
import com.tradingworld.dataflows.YFinanceVendor;
import com.tradingworld.dataflows.AlphaVantageVendor;
import com.tradingworld.dataflows.AShareVendor;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.P;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 用于获取市场/股票数据的工具。
 * 通过VendorRouter使用Yahoo Finance API。
 */
@Component
public class MarketTools {

    private static final Logger log = LoggerFactory.getLogger(MarketTools.class);

    private final VendorRouter vendorRouter;
    private final ObjectMapper objectMapper;

    public MarketTools() {
        YFinanceVendor yFinanceVendor = new YFinanceVendor();
        AlphaVantageVendor alphaVantageVendor = new AlphaVantageVendor();
        AShareVendor aShareVendor = new AShareVendor();
        this.vendorRouter = new VendorRouter(List.of(yFinanceVendor, alphaVantageVendor, aShareVendor));
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * 获取实时股票数据，包括价格、成交量和市值。
     */
    @Tool("Get real-time stock data including price, volume, and market cap for a given ticker")
    public String getStockData(@P("Stock ticker symbol (e.g., NVDA, AAPL)") String ticker) {
        log.debug("Fetching stock data for ticker: {}", ticker);
        try {
            return vendorRouter.getStockQuote(ticker.toUpperCase())
                    .map(quote -> {
                        try {
                            return objectMapper.writeValueAsString(Map.of(
                                    "ticker", quote.symbol(),
                                    "price", quote.price(),
                                    "change", quote.change(),
                                    "changePercent", quote.changePercent(),
                                    "volume", quote.volume(),
                                    "timestamp", quote.timestamp().toString()
                            ));
                        } catch (Exception e) {
                            return "{\"error\": \"Serialization error\"}";
                        }
                    })
                    .orElse("{\"error\": \"No data available for ticker: " + ticker + "\"}");
        } catch (Exception e) {
            log.error("Error fetching stock data for {}: {}", ticker, e.getMessage());
            return "{\"error\": \"Failed to fetch stock data: " + e.getMessage() + "\"}";
        }
    }

    /**
     * 获取指定时期的历史股票数据。
     */
    @Tool("Get historical stock data (OHLCV) for a specified period: 1d, 1w, 1m, 3m, 6m, 1y, 2y, 5y")
    public String getHistoricalData(
            @P("Stock ticker symbol (e.g., NVDA)") String ticker,
            @P("Period: 1d, 1w, 1m, 3m, 6m, 1y, 2y, 5y") String period) {
        log.debug("Fetching historical data for {} with period: {}", ticker, period);
        try {
            return vendorRouter.getHistorical(ticker.toUpperCase(), period)
                    .map(candles -> {
                        try {
                            List<Map<String, Object>> data = new java.util.ArrayList<>();
                            for (var c : candles) {
                                Map<String, Object> item = new java.util.HashMap<>();
                                item.put("datetime", c.datetime().toString());
                                item.put("open", c.open());
                                item.put("high", c.high());
                                item.put("low", c.low());
                                item.put("close", c.close());
                                item.put("volume", c.volume());
                                data.add(item);
                            }
                            return objectMapper.writeValueAsString(Map.of(
                                    "ticker", ticker.toUpperCase(),
                                    "period", period,
                                    "data", data
                            ));
                        } catch (Exception e) {
                            return "{\"error\": \"Serialization error\"}";
                        }
                    })
                    .orElse("{\"error\": \"No historical data available for ticker: " + ticker + "\"}");
        } catch (Exception e) {
            log.error("Error fetching historical data for {}: {}", ticker, e.getMessage());
            return "{\"error\": \"Failed to fetch historical data: " + e.getMessage() + "\"}";
        }
    }

    /**
     * 获取股票报价摘要，包括开盘价、收盘价等。
     */
    @Tool("Get stock quote summary including open, high, low, close, volume")
    public String getQuoteSummary(@P("Stock ticker symbol") String ticker) {
        log.debug("Fetching quote summary for ticker: {}", ticker);
        try {
            return vendorRouter.getHistorical(ticker.toUpperCase(), "1d")
                    .map(candles -> {
                        if (candles.isEmpty()) {
                            return "{\"error\": \"No quote data available\"}";
                        }
                        var latest = candles.get(candles.size() - 1);
                        try {
                            return objectMapper.writeValueAsString(Map.of(
                                    "ticker", ticker.toUpperCase(),
                                    "open", latest.open(),
                                    "high", latest.high(),
                                    "low", latest.low(),
                                    "close", latest.close(),
                                    "volume", latest.volume(),
                                    "timestamp", latest.datetime().toString()
                            ));
                        } catch (Exception e) {
                            return "{\"error\": \"Serialization error\"}";
                        }
                    })
                    .orElse("{\"error\": \"No quote data available for ticker: " + ticker + "\"}");
        } catch (Exception e) {
            log.error("Error fetching quote summary for {}: {}", ticker, e.getMessage());
            return "{\"error\": \"Failed to fetch quote summary: " + e.getMessage() + "\"}";
        }
    }

    /**
     * 获取股票的期权数据。
     */
    @Tool("Get options chain data including calls and puts")
    public String getOptions(@P("Stock ticker symbol") String ticker) {
        log.debug("Fetching options for ticker: {}", ticker);
        // 期权数据需要单独的API - 在基本Yahoo Finance中不可用
        return "{\"error\": \"Options data not yet implemented. Please use a specialized options API.\"}";
    }
}
