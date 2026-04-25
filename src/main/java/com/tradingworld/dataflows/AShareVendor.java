package com.tradingworld.dataflows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A股数据供应商实现，使用东方财富API。
 * 从中国股票市场（上海和深圳）获取股票数据。
 */
@Component
public class AShareVendor implements DataVendor {

    private static final Logger log = LoggerFactory.getLogger(AShareVendor.class);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${trading.data.ashare.enabled:true}")
    private boolean enabled;

    @Value("${trading.data.ashare.base-url:https://push2.eastmoney.com}")
    private String baseUrl;

    @Value("${trading.data.ashare.timeout:30}")
    private int timeout;

    public AShareVendor() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String getName() {
        return "East Money A-Share";
    }

    @Override
    public boolean isAvailable() {
        return enabled;
    }

    @Override
    public Optional<StockQuote> getStockQuote(String symbol) {
        try {
            String normalizedSymbol = normalizeTicker(symbol);
            String url = String.format(
                "%s/api/qt/stock/get?secid=%s&fields=f43,f44,f45,f46,f47,f48,f57,f58",
                baseUrl,
                secIdFromSymbol(normalizedSymbol)
            );

            String response = fetchUrl(url);
            if (response == null) return Optional.empty();

            JsonNode root = objectMapper.readTree(response);
            JsonNode data = root.path("data");
            if (data.isEmpty() || data.path("f43").isNull()) return Optional.empty();

            double price = data.path("f43").asDouble() / 100.0;
            double previousClose = data.path("f46").asDouble() / 100.0;
            double change = price - previousClose;
            double changePercent = previousClose > 0 ? (change / previousClose) * 100 : 0;
            long volume = data.path("f48").asLong();

            return Optional.of(new StockQuote(
                normalizedSymbol,
                price,
                change,
                changePercent,
                volume,
                LocalDateTime.now(ZoneOffset.UTC)
            ));
        } catch (Exception e) {
            log.warn("Failed to get stock quote for {}: {}", symbol, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<StockQuote>> getStockQuotes(List<String> symbols) {
        List<StockQuote> quotes = new ArrayList<>();
        for (String symbol : symbols) {
            getStockQuote(symbol).ifPresent(quotes::add);
        }
        return quotes.isEmpty() ? Optional.empty() : Optional.of(quotes);
    }

    @Override
    public Optional<List<Candle>> getHistorical(String symbol, String period) {
        try {
            String normalizedSymbol = normalizeTicker(symbol);
            String range = mapPeriodToRange(period);
            String url = String.format(
                "%s/api/qt/stock/kline/get?secid=%s&fields1=f1,f2,f3,f4,f5,f6&fields2=f51,f52,f53,f54,f55,f56,f57,f58&klt=101&fqt=1&beg=%s&end=20500101&lmt=%s",
                baseUrl,
                secIdFromSymbol(normalizedSymbol),
                range,
                range.equals("1d") ? 240 : 60000
            );

            String response = fetchUrl(url);
            if (response == null) return Optional.empty();

            JsonNode root = objectMapper.readTree(response);
            JsonNode klines = root.path("data").path("klines");

            if (klines.isEmpty()) return Optional.empty();

            List<Candle> candles = new ArrayList<>();
            for (JsonNode line : klines) {
                String[] parts = line.asText().split(",");
                if (parts.length >= 6) {
                    LocalDateTime datetime = LocalDateTime.parse(parts[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    candles.add(new Candle(
                        datetime,
                        Double.parseDouble(parts[1]),
                        Double.parseDouble(parts[2]),
                        Double.parseDouble(parts[3]),
                        Double.parseDouble(parts[4]),
                        Long.parseLong(parts[5])
                    ));
                }
            }

            return Optional.of(candles);
        } catch (Exception e) {
            log.warn("Failed to get historical data for {}: {}", symbol, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<BalanceSheet> getBalanceSheet(String symbol) {
        // 东方财富API基本面数据 - 简化实现
        // 完整的财务报表需要更复杂的API调用
        try {
            String normalizedSymbol = normalizeTicker(symbol);
            String url = String.format(
                "%s/api/qt/stock/get?secid=%s&fields=f58,f84,f85,f116,f117,f181,f236,f237",
                baseUrl,
                secIdFromSymbol(normalizedSymbol)
            );

            String response = fetchUrl(url);
            if (response == null) return Optional.empty();

            JsonNode root = objectMapper.readTree(response);
            JsonNode data = root.path("data");
            if (data.isEmpty()) return Optional.empty();

            // 简化 - 实际资产负债表需要单独的API调用
            return Optional.of(new BalanceSheet(
                normalizedSymbol,
                LocalDateTime.now(ZoneOffset.UTC),
                data.path("f84").asDouble(),  // totalAssets
                data.path("f85").asDouble(),  // totalLiabilities
                data.path("f116").asDouble()  // totalEquity
            ));
        } catch (Exception e) {
            log.warn("Failed to get balance sheet for {}: {}", symbol, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<IncomeStatement> getIncomeStatement(String symbol) {
        try {
            String normalizedSymbol = normalizeTicker(symbol);
            String url = String.format(
                "%s/api/qt/stock/get?secid=%s&fields=f58,f86,f87,f88,f189,f190",
                baseUrl,
                secIdFromSymbol(normalizedSymbol)
            );

            String response = fetchUrl(url);
            if (response == null) return Optional.empty();

            JsonNode root = objectMapper.readTree(response);
            JsonNode data = root.path("data");
            if (data.isEmpty()) return Optional.empty();

            return Optional.of(new IncomeStatement(
                normalizedSymbol,
                LocalDateTime.now(ZoneOffset.UTC),
                data.path("f86").asDouble(),  // totalRevenue
                data.path("f88").asDouble(),  // netIncome
                data.path("f87").asDouble()   // earningsPerShare
            ));
        } catch (Exception e) {
            log.warn("Failed to get income statement for {}: {}", symbol, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<Cashflow> getCashflow(String symbol) {
        // 现金流量表在东方财富简单报价API中不能直接获取
        log.debug("Cashflow not available via East Money simple API for {}", symbol);
        return Optional.empty();
    }

    @Override
    public Optional<List<InsiderTransaction>> getInsiderTransactions(String symbol) {
        // 内幕交易数据在东方财富API中不可用
        log.debug("Insider transactions not available via East Money for {}", symbol);
        return Optional.empty();
    }

    @Override
    public Optional<List<StockQuote>> screenStocks(StockFilter filter) {
        log.debug("Stock screening not available via East Money API");
        return Optional.empty();
    }

    @Override
    public Optional<List<NewsArticle>> getNews(String symbol) {
        try {
            String normalizedSymbol = normalizeTicker(symbol);
            String url = String.format(
                "https://newsapi.eastmoney.com/kuaixun/v1/getlist_v3.aspx?page=1&pageSize=10&order=webid&keyword=%s",
                normalizedSymbol.split("\\.")[0]
            );

            String response = fetchUrl(url);
            if (response == null) return Optional.empty();

            JsonNode root = objectMapper.readTree(response);
            JsonNode articles = root.path("LtaList");

            if (articles.isEmpty()) return Optional.empty();

            List<NewsArticle> newsArticles = new ArrayList<>();
            for (JsonNode item : articles) {
                newsArticles.add(new NewsArticle(
                    item.path("title").asText(),
                    item.path("digest").asText(),
                    item.path("url").asText(),
                    item.path("source").asText("East Money"),
                    LocalDateTime.now(ZoneOffset.UTC)
                ));
            }

            return Optional.of(newsArticles);
        } catch (Exception e) {
            log.warn("Failed to get news for {}: {}", symbol, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<TrendingTicker>> getTrendingTickers(int limit) {
        log.debug("Trending tickers not available via East Money API");
        return Optional.empty();
    }

    @Override
    public Optional<List<StockQuote>> getMarketMovers(String type, int limit) {
        log.debug("Market movers not available via East Money API");
        return Optional.empty();
    }

    /**
     * 规范化股票代码以包含.SS或.SZ后缀。
     */
    String normalizeTicker(String ticker) {
        if (ticker == null || ticker.isBlank()) {
            return ticker;
        }

        String trimmed = ticker.trim();

        // 已经是规范化的
        if (trimmed.endsWith(".SS") || trimmed.endsWith(".SZ")) {
            return trimmed;
        }

        // 提取数字部分
        String code = trimmed.replaceAll("[^0-9]", "");

        // 上海：代码以6或9开头（科创板）
        // 深圳：代码以0或3开头（创业板）
        if (code.startsWith("6") || code.startsWith("9")) {
            return code + ".SS";
        } else {
            return code + ".SZ";
        }
    }

    /**
     * 将规范化符号转换为东方财富secId格式。
     */
    private String secIdFromSymbol(String normalizedSymbol) {
        if (normalizedSymbol.endsWith(".SS")) {
            return "1." + normalizedSymbol.replace(".SS", "");
        } else {
            return "0." + normalizedSymbol.replace(".SZ", "");
        }
    }

    /**
     * 将周期字符串映射到东方财富日期范围。
     */
    private String mapPeriodToRange(String period) {
        return switch (period.toLowerCase()) {
            case "1d" -> "1d";
            case "1w" -> "5d";
            case "1m" -> "1mo";
            case "3m" -> "3mo";
            case "6m" -> "6mo";
            case "1y" -> "1y";
            case "2y" -> "2y";
            case "5y" -> "5y";
            case "max" -> "max";
            default -> "1mo";
        };
    }

    private String fetchUrl(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .header("Referer", "https://www.eastmoney.com")
                    .timeout(Duration.ofSeconds(timeout))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                log.warn("HTTP {} for URL: {}", response.statusCode(), url);
                return null;
            }
        } catch (Exception e) {
            log.warn("Failed to fetch URL {}: {}", url, e.getMessage());
            return null;
        }
    }
}
