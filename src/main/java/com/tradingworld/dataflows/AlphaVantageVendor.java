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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Alpha Vantage数据供应商实现。
 * 作为Yahoo Finance的备用提供股票数据、技术指标和新闻。
 */
@Component
public class AlphaVantageVendor implements DataVendor {

    private static final Logger log = LoggerFactory.getLogger(AlphaVantageVendor.class);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${trading.data.alpha-vantage.enabled:false}")
    private boolean enabled;

    @Value("${trading.data.alpha-vantage.api-key:}")
    private String apiKey;

    @Value("${trading.data.alpha-vantage.base-url:https://www.alphavantage.co}")
    private String baseUrl;

    public AlphaVantageVendor() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String getName() {
        return "Alpha Vantage";
    }

    @Override
    public boolean isAvailable() {
        return enabled && apiKey != null && !apiKey.isEmpty();
    }

    @Override
    public Optional<StockQuote> getStockQuote(String symbol) {
        try {
            String url = String.format(
                "%s/query?function=GLOBAL_QUOTE&symbol=%s&apikey=%s",
                baseUrl, symbol.toUpperCase(), apiKey
            );

            String response = fetchUrl(url);
            if (response == null) return Optional.empty();

            JsonNode root = objectMapper.readTree(response);
            JsonNode quote = root.path("Global Quote");

            if (quote.isEmpty()) return Optional.empty();

            return Optional.of(new StockQuote(
                symbol.toUpperCase(),
                quote.path("05. price").asDouble(),
                quote.path("09. change").asDouble(),
                Double.parseDouble(quote.path("10. change percent").asText().replace("%", "")),
                quote.path("06. volume").asLong(),
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
            String url = String.format(
                "%s/query?function=TIME_SERIES_DAILY&symbol=%s&apikey=%s&outputsize=%s",
                baseUrl, symbol.toUpperCase(), apiKey,
                periodToSize(period)
            );

            String response = fetchUrl(url);
            if (response == null) return Optional.empty();

            JsonNode root = objectMapper.readTree(response);
            JsonNode timeSeries = root.path("Time Series (Daily)");

            if (timeSeries.isEmpty()) return Optional.empty();

            List<Candle> candles = new ArrayList<>();
            timeSeries.fieldNames().forEachRemaining(dateStr -> {
                JsonNode dayData = timeSeries.get(dateStr);
                candles.add(new Candle(
                    LocalDateTime.parse(dateStr + "T00:00:00"),
                    dayData.path("1. open").asDouble(),
                    dayData.path("2. high").asDouble(),
                    dayData.path("3. low").asDouble(),
                    dayData.path("4. close").asDouble(),
                    dayData.path("5. volume").asLong()
                ));
            });

            return Optional.of(candles);
        } catch (Exception e) {
            log.warn("Failed to get historical data for {}: {}", symbol, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<BalanceSheet> getBalanceSheet(String symbol) {
        // Alpha Vantage免费版不提供资产负债表
        log.debug("Balance sheet not available via Alpha Vantage");
        return Optional.empty();
    }

    @Override
    public Optional<IncomeStatement> getIncomeStatement(String symbol) {
        // Alpha Vantage免费版不提供利润表
        log.debug("Income statement not available via Alpha Vantage");
        return Optional.empty();
    }

    @Override
    public Optional<Cashflow> getCashflow(String symbol) {
        // Alpha Vantage免费版不提供现金流量表
        log.debug("Cashflow not available via Alpha Vantage");
        return Optional.empty();
    }

    @Override
    public Optional<List<InsiderTransaction>> getInsiderTransactions(String symbol) {
        // Alpha Vantage不提供内幕交易数据
        log.debug("Insider transactions not available via Alpha Vantage");
        return Optional.empty();
    }

    @Override
    public Optional<List<NewsArticle>> getNews(String symbol) {
        try {
            String url = String.format(
                "%s/query?function=NEWS_SENTIMENT&tickers=%s&apikey=%s&limit=10",
                baseUrl, symbol.toUpperCase(), apiKey
            );

            String response = fetchUrl(url);
            if (response == null) return Optional.empty();

            JsonNode root = objectMapper.readTree(response);
            JsonNode feed = root.path("feed");

            if (feed.isEmpty()) return Optional.empty();

            List<NewsArticle> articles = new ArrayList<>();
            for (JsonNode item : feed) {
                articles.add(new NewsArticle(
                    item.path("title").asText(),
                    item.path("summary").asText(),
                    item.path("url").asText(),
                    item.path("source").asText(),
                    LocalDateTime.now(ZoneOffset.UTC)
                ));
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
                String body = response.body();
                if (body.contains("Invalid API call") || body.contains("Premium")) {
                    log.warn("Alpha Vantage API error or premium required");
                    return null;
                }
                return body;
            } else {
                log.warn("HTTP {} for URL: {}", response.statusCode(), url);
                return null;
            }
        } catch (Exception e) {
            log.warn("Failed to fetch URL {}: {}", url, e.getMessage());
            return null;
        }
    }

    private String periodToSize(String period) {
        return switch (period.toLowerCase()) {
            case "1d", "1w" -> "compact";
            default -> "full";
        };
    }
}
