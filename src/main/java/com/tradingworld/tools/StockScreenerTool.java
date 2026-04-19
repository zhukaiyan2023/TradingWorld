package com.tradingworld.tools;

import com.tradingworld.dataflows.DataVendor;
import com.tradingworld.dataflows.VendorRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 股票筛选工具 - 提供热门股票、涨跌幅榜、技术筛选、基本面筛选功能。
 * 供 LLM Agent 调用进行股票筛选分析。
 */
@Component
public class StockScreenerTool {

    private static final Logger log = LoggerFactory.getLogger(StockScreenerTool.class);

    private final VendorRouter vendorRouter;

    public StockScreenerTool(VendorRouter vendorRouter) {
        this.vendorRouter = vendorRouter;
    }

    /**
     * 获取热门/趋势股票。
     * 按成交量和涨幅综合排序，返回最活跃的股票。
     *
     * @param limit 返回数量限制（默认10）
     * @return JSON格式的热门股票列表
     */
    public String getTrendingTickers(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        try {
            Optional<List<DataVendor.TrendingTicker>> result = vendorRouter.getTrendingTickers(limit);

            if (result.isEmpty() || result.get().isEmpty()) {
                return """
                    {"error": "No trending tickers available", "data": []}
                    """;
            }

            StringBuilder json = new StringBuilder();
            json.append("{\"trending\": [");

            List<DataVendor.TrendingTicker> tickers = result.get();
            for (int i = 0; i < tickers.size(); i++) {
                DataVendor.TrendingTicker t = tickers.get(i);
                if (i > 0) json.append(",");
                json.append("{")
                    .append("\"symbol\":\"").append(t.symbol()).append("\",")
                    .append("\"name\":\"").append(escapeJson(t.name())).append("\",")
                    .append("\"price\":").append(t.price()).append(",")
                    .append("\"changePercent\":").append(t.changePercent()).append(",")
                    .append("\"volume\":").append(t.volume()).append(",")
                    .append("\"rank\":").append(t.rank())
                    .append("}");
            }

            json.append("]}");
            return json.toString();

        } catch (Exception e) {
            log.error("Failed to get trending tickers: {}", e.getMessage());
            return """
                {"error": "Failed to fetch trending tickers", "details": "%s"}
                """.formatted(e.getMessage());
        }
    }

    /**
     * 获取涨幅最大的股票（涨幅榜）。
     *
     * @param limit 返回数量限制（默认10）
     * @return JSON格式的涨幅榜股票列表
     */
    public String getTopGainers(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        return getMarketMovers("gainers", limit);
    }

    /**
     * 获取跌幅最大的股票（跌幅榜）。
     *
     * @param limit 返回数量限制（默认10）
     * @return JSON格式的跌幅榜股票列表
     */
    public String getTopLosers(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        return getMarketMovers("losers", limit);
    }

    /**
     * 获取成交量最大的股票（最活跃）。
     *
     * @param limit 返回数量限制（默认10）
     * @return JSON格式的最活跃股票列表
     */
    public String getMostActive(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        return getMarketMovers("active", limit);
    }

    /**
     * 获取市场涨跌幅榜股票。
     *
     * @param type 类型: "gainers"(涨幅榜), "losers"(跌幅榜), "active"(成交量榜)
     * @param limit 返回数量限制
     * @return JSON格式的股票列表
     */
    public String getMarketMovers(String type, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        if (type == null) {
            type = "active";
        }

        try {
            Optional<List<DataVendor.StockQuote>> result = vendorRouter.getMarketMovers(type, limit);

            if (result.isEmpty() || result.get().isEmpty()) {
                return """
                    {"error": "No market movers available for type: %s", "data": []}
                    """.formatted(type);
            }

            StringBuilder json = new StringBuilder();
            json.append("{\"type\":\"").append(type).append("\", \"movers\": [");

            List<DataVendor.StockQuote> movers = result.get();
            for (int i = 0; i < movers.size(); i++) {
                DataVendor.StockQuote q = movers.get(i);
                if (i > 0) json.append(",");
                json.append("{")
                    .append("\"symbol\":\"").append(q.symbol()).append("\",")
                    .append("\"price\":").append(q.price()).append(",")
                    .append("\"change\":").append(q.change()).append(",")
                    .append("\"changePercent\":").append(q.changePercent()).append(",")
                    .append("\"volume\":").append(q.volume())
                    .append("}");
            }

            json.append("]}");
            return json.toString();

        } catch (Exception e) {
            log.error("Failed to get market movers: {}", e.getMessage());
            return """
                {"error": "Failed to fetch market movers", "details": "%s"}
                """.formatted(e.getMessage());
        }
    }

    /**
     * 按技术指标筛选股票。
     *
     * @param minPrice 最低价格（可选）
     * @param maxPrice 最高价格（可选）
     * @param minVolume 最低成交量（可选）
     * @return JSON格式的筛选结果
     */
    public String screenByTechnical(Double minPrice, Double maxPrice, Long minVolume) {
        try {
            DataVendor.StockFilter filter = new DataVendor.StockFilter(
                minPrice,
                maxPrice,
                minVolume,
                null, // minMarketCap
                null, // minPe
                null, // maxPe
                null, // sector
                null  // exchange
            );

            Optional<List<DataVendor.StockQuote>> result = vendorRouter.screenStocks(filter);

            if (result.isEmpty() || result.get().isEmpty()) {
                return """
                    {"error": "No stocks match the technical criteria", "data": []}
                    """;
            }

            StringBuilder json = new StringBuilder();
            json.append("{\"screened_by\": \"technical\", \"stocks\": [");

            List<DataVendor.StockQuote> stocks = result.get();
            for (int i = 0; i < stocks.size(); i++) {
                DataVendor.StockQuote q = stocks.get(i);
                if (i > 0) json.append(",");
                json.append("{")
                    .append("\"symbol\":\"").append(q.symbol()).append("\",")
                    .append("\"price\":").append(q.price()).append(",")
                    .append("\"changePercent\":").append(q.changePercent()).append(",")
                    .append("\"volume\":").append(q.volume())
                    .append("}");
            }

            json.append("]}");
            return json.toString();

        } catch (Exception e) {
            log.error("Failed to screen stocks by technical criteria: {}", e.getMessage());
            return """
                {"error": "Failed to screen stocks", "details": "%s"}
                """.formatted(e.getMessage());
        }
    }

    /**
     * 按基本面指标筛选股票。
     *
     * @param minMarketCap 最低市值（可选）
     * @param minPe 最低市盈率（可选）
     * @param maxPe 最高市盈率（可选）
     * @param sector 板块（可选）
     * @return JSON格式的筛选结果
     */
    public String screenByFundamental(Double minMarketCap, Double minPe, Double maxPe, String sector) {
        try {
            DataVendor.StockFilter filter = new DataVendor.StockFilter(
                null,  // minPrice
                null,  // maxPrice
                null,  // minVolume
                minMarketCap,
                minPe,
                maxPe,
                sector,
                null   // exchange
            );

            Optional<List<DataVendor.StockQuote>> result = vendorRouter.screenStocks(filter);

            if (result.isEmpty() || result.get().isEmpty()) {
                return """
                    {"error": "No stocks match the fundamental criteria", "data": []}
                    """;
            }

            StringBuilder json = new StringBuilder();
            json.append("{\"screened_by\": \"fundamental\", \"stocks\": [");

            List<DataVendor.StockQuote> stocks = result.get();
            for (int i = 0; i < stocks.size(); i++) {
                DataVendor.StockQuote q = stocks.get(i);
                if (i > 0) json.append(",");
                json.append("{")
                    .append("\"symbol\":\"").append(q.symbol()).append("\",")
                    .append("\"price\":").append(q.price()).append(",")
                    .append("\"changePercent\":").append(q.changePercent()).append(",")
                    .append("\"volume\":").append(q.volume())
                    .append("}");
            }

            json.append("]}");
            return json.toString();

        } catch (Exception e) {
            log.error("Failed to screen stocks by fundamental criteria: {}", e.getMessage());
            return """
                {"error": "Failed to screen stocks", "details": "%s"}
                """.formatted(e.getMessage());
        }
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
