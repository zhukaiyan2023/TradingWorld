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

    @Override
    public Optional<List<StockQuote>> getStockQuotes(List<String> symbols) {
        if (symbols == null || symbols.isEmpty()) {
            return Optional.empty();
        }
        try {
            String symbolsParam = String.join(",", symbols.stream()
                .map(String::toUpperCase)
                .toList());
            String url = String.format(
                "https://query1.finance.yahoo.com/v7/finance/quote?symbols=%s",
                symbolsParam
            );

            String response = fetchUrl(url);
            if (response == null) return Optional.empty();

            JsonNode root = objectMapper.readTree(response);
            JsonNode result = root.path("quoteResponse").path("result");

            if (result.isEmpty()) return Optional.empty();

            List<StockQuote> quotes = new ArrayList<>();
            for (JsonNode item : result) {
                String symbol = item.path("symbol").asText();
                double price = item.path("regularMarketPrice").asDouble(0);
                double previousClose = item.path("regularMarketPreviousClose").asDouble(0);
                double change = price - previousClose;
                double changePercent = previousClose > 0 ? (change / previousClose) * 100 : 0;
                long volume = item.path("regularMarketVolume").asLong(0);

                quotes.add(new StockQuote(
                    symbol,
                    price,
                    change,
                    changePercent,
                    volume,
                    LocalDateTime.now(ZoneOffset.UTC)
                ));
            }
            return Optional.of(quotes);
        } catch (Exception e) {
            log.warn("Failed to get batch stock quotes: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<TrendingTicker>> getTrendingTickers(int limit) {
        try {
            // 使用 Yahoo Finance 的热门榜 API
            String url = "https://query1.finance.yahoo.com/v6/finance/recommendationsbyasset?symbol=^GSPC";

            String response = fetchUrl(url);
            if (response == null) return Optional.empty();

            JsonNode root = objectMapper.readTree(response);
            JsonNode quotes = root.path("finance").path("result");

            if (quotes.isEmpty()) return Optional.empty();

            // 从大盘指数成分股中获取热门股票
            List<TrendingTicker> trending = new ArrayList<>();
            JsonNode instrumentInfo = quotes.get(0).path("instrumentInfo");
            JsonNode relatedEquities = instrumentInfo.path("relatedEquities");

            int rank = 0;
            for (JsonNode item : relatedEquities) {
                if (rank >= limit) break;
                String symbol = item.path("symbol").asText();
                double price = item.path("regularMarketPrice").asDouble(0);
                double changePercent = item.path("regularMarketChangePercent").asDouble(0);
                long volume = item.path("regularMarketVolume").asLong(0);
                String name = item.path("shortName").asText(symbol);

                trending.add(new TrendingTicker(
                    symbol,
                    name,
                    price,
                    changePercent,
                    volume,
                    ++rank
                ));
            }

            return trending.isEmpty() ? Optional.empty() : Optional.of(trending);
        } catch (Exception e) {
            log.warn("Failed to get trending tickers: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<StockQuote>> getMarketMovers(String type, int limit) {
        try {
            // Yahoo Finance 市场涨跌幅榜
            // ^GSPC = S&P 500, ^DJI = Dow Jones, ^IXIC = Nasdaq
            String url = String.format(
                "https://query1.finance.yahoo.com/v8/finance/chart/%s?interval=1d&range=5d",
                "^GSPC"
            );

            String response = fetchUrl(url);
            if (response == null) return Optional.empty();

            // 获取市场概览页面的涨跌幅榜数据
            String moversUrl = "https://query1.finance.yahoo.com/v6/finance/quote/marketMovers";

            // 实际上 Yahoo Finance 没有直接的 API 获取涨跌幅榜
            // 我们使用 v7/finance/market/overview 风格的 API
            String overviewUrl = "https://query1.finance.yahoo.com/v1/finance/screener/predefined/saved?formatted=true&lang=en-US&region=US&scrIds=day_gainers&start=0&count=" + limit;

            String overviewResponse = fetchUrl(overviewUrl);
            if (overviewResponse == null) return Optional.empty();

            JsonNode root = objectMapper.readTree(overviewResponse);
            JsonNode quotes = root.path("finance").path("result").path("quotes");

            if (quotes.isEmpty()) return Optional.empty();

            List<StockQuote> movers = new ArrayList<>();
            for (JsonNode item : quotes) {
                String symbol = item.path("symbol").asText();
                double price = item.path("regularMarketPrice").asDouble(0);
                double change = item.path("regularMarketChange").asDouble(0);
                double changePercent = item.path("regularMarketChangePercent").asDouble(0);
                long volume = item.path("regularMarketVolume").asLong(0);

                movers.add(new StockQuote(
                    symbol,
                    price,
                    change,
                    changePercent,
                    volume,
                    LocalDateTime.now(ZoneOffset.UTC)
                ));
            }

            // 根据类型排序
            if ("gainers".equalsIgnoreCase(type)) {
                movers.sort((a, b) -> Double.compare(b.changePercent(), a.changePercent()));
            } else if ("losers".equalsIgnoreCase(type)) {
                movers.sort((a, b) -> Double.compare(a.changePercent(), b.changePercent()));
            } else { // active
                movers.sort((a, b) -> Long.compare(b.volume(), a.volume()));
            }

            // 限制返回数量
            if (movers.size() > limit) {
                movers = movers.subList(0, limit);
            }

            return movers.isEmpty() ? Optional.empty() : Optional.of(movers);
        } catch (Exception e) {
            log.warn("Failed to get market movers: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<StockQuote>> screenStocks(StockFilter filter) {
        try {
            // Yahoo Finance 没有直接的筛选 API
            // 使用批量报价获取数据后本地筛选
            // 为了实现筛选功能，我们需要获取更大的股票池
            String screenerUrl = "https://query1.finance.yahoo.com/v1/finance/screener/predefined/saved?formatted=true&lang=en-US&region=US&scrIds=custom&start=0&count=100";

            String response = fetchUrl(screenerUrl);
            if (response == null) return Optional.empty();

            JsonNode root = objectMapper.readTree(response);
            JsonNode quotes = root.path("finance").path("result").path("quotes");

            if (quotes.isEmpty()) return Optional.empty();

            List<StockQuote> screened = new ArrayList<>();
            for (JsonNode item : quotes) {
                double price = item.path("regularMarketPrice").asDouble(0);
                long volume = item.path("regularMarketVolume").asLong(0);
                double marketCap = item.path("marketCap").asDouble(0);
                double pe = item.path("trailingPE").asDouble(0);
                String sector = item.path("sector").asText("");
                String exchange = item.path("exchange").asText("");

                // 应用筛选条件
                boolean matches = true;

                if (filter.minPrice() != null && price < filter.minPrice()) {
                    matches = false;
                }
                if (filter.maxPrice() != null && price > filter.maxPrice()) {
                    matches = false;
                }
                if (filter.minVolume() != null && volume < filter.minVolume()) {
                    matches = false;
                }
                if (filter.minMarketCap() != null && marketCap < filter.minMarketCap()) {
                    matches = false;
                }
                if (filter.minPe() != null && pe > 0 && pe < filter.minPe()) {
                    matches = false;
                }
                if (filter.maxPe() != null && pe > filter.maxPe()) {
                    matches = false;
                }
                if (filter.sector() != null && !filter.sector().isEmpty() && !sector.equalsIgnoreCase(filter.sector())) {
                    matches = false;
                }
                if (filter.exchange() != null && !filter.exchange().isEmpty() && !exchange.equalsIgnoreCase(filter.exchange())) {
                    matches = false;
                }

                if (matches) {
                    double previousClose = item.path("regularMarketPreviousClose").asDouble(0);
                    double change = price - previousClose;
                    double changePercent = previousClose > 0 ? (change / previousClose) * 100 : 0;

                    screened.add(new StockQuote(
                        item.path("symbol").asText(),
                        price,
                        change,
                        changePercent,
                        volume,
                        LocalDateTime.now(ZoneOffset.UTC)
                    ));
                }
            }

            return screened.isEmpty() ? Optional.empty() : Optional.of(screened);
        } catch (Exception e) {
            log.warn("Failed to screen stocks: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
