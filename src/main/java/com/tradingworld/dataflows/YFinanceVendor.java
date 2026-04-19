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
 * Yahoo Finance数据供应商实现。
 * 从Yahoo Finance API获取股票数据。
 */
@Component
public class YFinanceVendor implements DataVendor {

    private static final Logger log = LoggerFactory.getLogger(YFinanceVendor.class);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${trading.data.yfinance.enabled:true}")
    private boolean enabled;

    public YFinanceVendor() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String getName() {
        return "Yahoo Finance";
    }

    @Override
    public boolean isAvailable() {
        return enabled;
    }

    @Override
    public Optional<StockQuote> getStockQuote(String symbol) {
        try {
            String url = String.format(
                "https://query1.finance.yahoo.com/v8/finance/chart/%s?interval=1d&range=1d",
                symbol.toUpperCase()
            );

            String response = fetchUrl(url);
            if (response == null) return Optional.empty();

            JsonNode root = objectMapper.readTree(response);
            JsonNode result = root.path("chart").path("result");
            if (result.isEmpty()) return Optional.empty();

            JsonNode meta = result.get(0).path("meta");
            double price = meta.path("regularMarketPrice").asDouble();
            double previousClose = meta.path("chartPreviousClose").asDouble();
            double change = price - previousClose;
            double changePercent = previousClose > 0 ? (change / previousClose) * 100 : 0;
            long volume = meta.path("regularMarketVolume").asLong();

            return Optional.of(new StockQuote(
                symbol.toUpperCase(),
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
    public Optional<List<Candle>> getHistorical(String symbol, String period) {
        try {
            String range = mapPeriodToRange(period);
            String url = String.format(
                "https://query1.finance.yahoo.com/v8/finance/chart/%s?interval=1d&range=%s",
                symbol.toUpperCase(),
                range
            );

            String response = fetchUrl(url);
            if (response == null) return Optional.empty();

            JsonNode root = objectMapper.readTree(response);
            JsonNode result = root.path("chart").path("result");
            if (result.isEmpty()) return Optional.empty();

            JsonNode timestamps = result.get(0).path("timestamp");
            JsonNode quotes = result.get(0).path("indicators").path("quote").get(0);

            List<Candle> candles = new ArrayList<>();
            for (int i = 0; i < timestamps.size(); i++) {
                long timestamp = timestamps.get(i).asLong();
                LocalDateTime datetime = LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC);

                candles.add(new Candle(
                    datetime,
                    quotes.path("open").get(i).asDouble(),
                    quotes.path("high").get(i).asDouble(),
                    quotes.path("low").get(i).asDouble(),
                    quotes.path("close").get(i).asDouble(),
                    quotes.path("volume").get(i).asLong()
                ));
            }

            return Optional.of(candles);
        } catch (Exception e) {
            log.warn("Failed to get historical data for {}: {}", symbol, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<BalanceSheet> getBalanceSheet(String symbol) {
        try {
            String url = String.format(
                "https://query1.finance.yahoo.com/v10/finance/quoteSummary/%s?modules=balanceSheetHistory",
                symbol.toUpperCase()
            );

            String response = fetchUrl(url);
            if (response == null) return Optional.empty();

            JsonNode root = objectMapper.readTree(response);
            JsonNode balanceSheet = root.path("quoteSummary")
                .path("result").get(0)
                .path("balanceSheetHistory")
                .path("balanceSheetStatements");

            if (balanceSheet.isEmpty()) return Optional.empty();

            JsonNode latest = balanceSheet.get(0);
            JsonNode endDate = latest.path("endDate");

            return Optional.of(new BalanceSheet(
                symbol.toUpperCase(),
                LocalDateTime.now(ZoneOffset.UTC),
                latest.path("totalAssets").asDouble(),
                latest.path("totalLiabilities").asDouble(),
                latest.path("totalStockholderEquity").asDouble()
            ));
        } catch (Exception e) {
            log.warn("Failed to get balance sheet for {}: {}", symbol, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<IncomeStatement> getIncomeStatement(String symbol) {
        try {
            String url = String.format(
                "https://query1.finance.yahoo.com/v10/finance/quoteSummary/%s?modules=incomeStatementHistory",
                symbol.toUpperCase()
            );

            String response = fetchUrl(url);
            if (response == null) return Optional.empty();

            JsonNode root = objectMapper.readTree(response);
            JsonNode statements = root.path("quoteSummary")
                .path("result").get(0)
                .path("incomeStatementHistory")
                .path("incomeStatementHistory");

            if (statements.isEmpty()) return Optional.empty();

            JsonNode latest = statements.get(0);

            return Optional.of(new IncomeStatement(
                symbol.toUpperCase(),
                LocalDateTime.now(ZoneOffset.UTC),
                latest.path("totalRevenue").asDouble(),
                latest.path("netIncome").asDouble(),
                latest.path("earningsPerShare").asDouble()
            ));
        } catch (Exception e) {
            log.warn("Failed to get income statement for {}: {}", symbol, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<Cashflow> getCashflow(String symbol) {
        try {
            String url = String.format(
                "https://query1.finance.yahoo.com/v10/finance/quoteSummary/%s?modules=cashflowStatementHistory",
                symbol.toUpperCase()
            );

            String response = fetchUrl(url);
            if (response == null) return Optional.empty();

            JsonNode root = objectMapper.readTree(response);
            JsonNode statements = root.path("quoteSummary")
                .path("result").get(0)
                .path("cashflowStatementHistory")
                .path("cashflowStatements");

            if (statements.isEmpty()) return Optional.empty();

            JsonNode latest = statements.get(0);

            return Optional.of(new Cashflow(
                symbol.toUpperCase(),
                LocalDateTime.now(ZoneOffset.UTC),
                latest.path("operatingCashflow").asDouble(),
                latest.path("capital Expenditures").asDouble(),
                latest.path("financingCashflow").asDouble()
            ));
        } catch (Exception e) {
            log.warn("Failed to get cashflow for {}: {}", symbol, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<InsiderTransaction>> getInsiderTransactions(String symbol) {
        // Yahoo Finance API不直接提供内幕交易数据
        // 这需要不同的数据源
        log.debug("Insider transactions not available via Yahoo Finance");
        return Optional.empty();
    }

    @Override
    public Optional<List<NewsArticle>> getNews(String symbol) {
        try {
            String url = String.format(
                "https://query1.finance.yahoo.com/v1/finance/search?q=%s&newsCount=10",
                symbol.toUpperCase()
            );

            String response = fetchUrl(url);
            if (response == null) return Optional.empty();

            JsonNode root = objectMapper.readTree(response);
            JsonNode newsItems = root.path("news");

            List<NewsArticle> articles = new ArrayList<>();
            for (JsonNode item : newsItems) {
                String title = item.path("title").asText();
                String content = item.path("summary").asText();
                String url_str = item.path("link").asText();
                String source = item.path("publisher").asText();

                long time = item.path("providerPublishTime").asLong(0);
                LocalDateTime publishTime = LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC);

                articles.add(new NewsArticle(title, content, url_str, source, publishTime));
            }

            return Optional.of(articles);
        } catch (Exception e) {
            log.warn("Failed to get news for {}: {}", symbol, e.getMessage());
            return Optional.empty();
        }
    }

    private String fetchUrl(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0")
                    .timeout(Duration.ofSeconds(30))
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
}
