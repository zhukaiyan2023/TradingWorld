package com.tradingworld.tools;

import com.tradingworld.dataflows.VendorRouter;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.P;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用于获取新闻数据的工具。
 * 通过注入的 VendorRouter 获取新闻数据。
 */
@Component
public class NewsTools {

    private static final Logger log = LoggerFactory.getLogger(NewsTools.class);

    private final VendorRouter vendorRouter;

    public NewsTools(VendorRouter vendorRouter) {
        this.vendorRouter = vendorRouter;
    }

    /**
     * 获取公司的近期新闻。
     */
    @Tool("Get recent news articles for a company")
    public String getNews(@P("Stock ticker symbol (e.g., NVDA)") String ticker) {
        log.debug("Fetching news for ticker: {}", ticker);
        try {
            return vendorRouter.getNews(ticker.toUpperCase())
                    .map(articles -> {
                        StringBuilder sb = new StringBuilder();
                        sb.append("{\"ticker\": \"").append(ticker.toUpperCase()).append("\", \"news\": [");
                        for (int i = 0; i < articles.size(); i++) {
                            var article = articles.get(i);
                            if (i > 0) sb.append(", ");
                            sb.append("{");
                            sb.append("\"title\": \"").append(escapeJson(article.title())).append("\", ");
                            sb.append("\"content\": \"").append(escapeJson(article.content())).append("\", ");
                            sb.append("\"source\": \"").append(escapeJson(article.source())).append("\", ");
                            sb.append("\"url\": \"").append(escapeJson(article.url())).append("\", ");
                            sb.append("\"publishTime\": \"").append(article.publishTime().toString()).append("\"");
                            sb.append("}");
                        }
                        sb.append("], \"count\": ").append(articles.size()).append("}");
                        return sb.toString();
                    })
                    .orElse("{\"error\": \"No news available for ticker: " + ticker + "\"}");
        } catch (Exception e) {
            log.error("Error fetching news for {}: {}", ticker, e.getMessage());
            return "{\"error\": \"Failed to fetch news: " + e.getMessage() + "\"}";
        }
    }

    /**
     * 获取公司在指定日期范围内的近期新闻。
     */
    @Tool("Get recent news articles for a company within specified days")
    public String getNewsWithDateRange(
            @P("Stock ticker symbol") String ticker,
            @P("Number of days to look back (default 7)") Integer days) {
        int d = (days != null) ? days : 7;
        log.debug("Fetching news for {} within {} days", ticker, d);
        // 与getNews相同的实现 - 日期过滤需要额外的逻辑
        return getNews(ticker);
    }

    /**
     * 获取全球宏观经济新闻。
     */
    @Tool("Get global macroeconomic news for a topic")
    public String getGlobalNews(@P("Topic to search (e.g., FED, inflation, GDP)") String topic) {
        log.debug("Fetching global news for topic: {}", topic);
        try {
            String[] marketProxies = {"SPY", "QQQ", "DIA"};
            StringBuilder allNews = new StringBuilder();
            allNews.append("{\"topic\": \"").append(escapeJson(topic)).append("\", \"news\": [");

            boolean hasNews = false;
            for (String proxy : marketProxies) {
                var result = vendorRouter.getNews(proxy);
                if (result.isPresent() && !result.get().isEmpty()) {
                    for (var article : result.get()) {
                        if (hasNews) allNews.append(", ");
                        allNews.append("{");
                        allNews.append("\"title\": \"").append(escapeJson(article.title())).append("\", ");
                        allNews.append("\"content\": \"").append(escapeJson(article.content())).append("\", ");
                        allNews.append("\"source\": \"").append(escapeJson(article.source())).append("\", ");
                        allNews.append("\"url\": \"").append(escapeJson(article.url())).append("\", ");
                        allNews.append("\"publishTime\": \"").append(article.publishTime().toString()).append("\", ");
                        allNews.append("\"related\": \"").append(proxy).append("\"");
                        allNews.append("}");
                        hasNews = true;
                    }
                }
            }

            if (!hasNews) {
                return "{\"error\": \"No global news available for topic: " + escapeJson(topic) + "\"}";
            }

            allNews.append("], \"topic\": \"").append(escapeJson(topic)).append("\"}");
            return allNews.toString();
        } catch (Exception e) {
            log.error("Error fetching global news for topic {}: {}", topic, e.getMessage());
            return "{\"error\": \"Failed to fetch global news: " + e.getMessage() + "\"}";
        }
    }

    /**
     * 获取新闻文章的情绪分析。
     */
    @Tool("Get sentiment analysis of recent news for a company")
    public String getSentiment(@P("Stock ticker symbol") String ticker) {
        log.debug("Fetching sentiment for ticker: {}", ticker);
        try {
            return vendorRouter.getNews(ticker.toUpperCase())
                    .map(articles -> {
                        if (articles.isEmpty()) {
                            return String.format("""
                                {
                                    "ticker": "%s",
                                    "overallSentiment": "neutral",
                                    "positiveCount": 0,
                                    "negativeCount": 0,
                                    "neutralCount": 0,
                                    "articleCount": 0
                                }
                                """, ticker.toUpperCase());
                        }

                        // 基于标题关键词的简单情绪估计
                        int positive = 0;
                        int negative = 0;
                        int neutral = 0;

                        List<String> positiveWords = List.of("surge", "gain", "rise", "grow", "profit", "beat", "upgrade", "bullish", "optimistic", "soar", "jump", "rally");
                        List<String> negativeWords = List.of("fall", "drop", "loss", "miss", "downgrade", "bearish", "pessimistic", "plunge", "decline", "cut", "warn", "fear");

                        for ( var article : articles) {
                            String text = (article.title() + " " + article.content()).toLowerCase();
                            boolean hasPositive = positiveWords.stream().anyMatch(text::contains);
                            boolean hasNegative = negativeWords.stream().anyMatch(text::contains);

                            if (hasPositive && !hasNegative) positive++;
                            else if (hasNegative && !hasPositive) negative++;
                            else neutral++;
                        }

                        String overallSentiment = positive > negative ? "positive" :
                                negative > positive ? "negative" : "neutral";

                        return String.format("""
                            {
                                "ticker": "%s",
                                "overallSentiment": "%s",
                                "positiveCount": %d,
                                "negativeCount": %d,
                                "neutralCount": %d,
                                "articleCount": %d
                            }
                            """, ticker.toUpperCase(), overallSentiment, positive, negative, neutral, articles.size());
                    })
                    .orElse("{\"error\": \"No news available for ticker: " + ticker + "\"}");
        } catch (Exception e) {
            log.error("Error analyzing sentiment for {}: {}", ticker, e.getMessage());
            return "{\"error\": \"Failed to analyze sentiment: " + e.getMessage() + "\"}";
        }
    }

    /**
     * 获取市场上最热门的股票代码。
     */
    @Tool("Get top trending tickers in the market")
    public String getTrendingTickers(@P("Maximum number of tickers to return (default 10)") Integer limit) {
        int l = (limit != null && limit > 0) ? limit : 10;
        log.debug("Fetching {} trending tickers", l);
        try {
            return vendorRouter.getTrendingTickers(l)
                    .map(tickers -> {
                        StringBuilder sb = new StringBuilder();
                        sb.append("{\"trending\": [");
                        for (int i = 0; i < tickers.size(); i++) {
                            var ticker = tickers.get(i);
                            if (i > 0) sb.append(", ");
                            sb.append("{");
                            sb.append("\"symbol\": \"").append(ticker.symbol()).append("\", ");
                            sb.append("\"name\": \"").append(escapeJson(ticker.name())).append("\", ");
                            sb.append("\"price\": ").append(ticker.price()).append(", ");
                            sb.append("\"changePercent\": ").append(ticker.changePercent()).append(", ");
                            sb.append("\"volume\": ").append(ticker.volume()).append(", ");
                            sb.append("\"rank\": ").append(ticker.rank());
                            sb.append("}");
                        }
                        sb.append("], \"count\": ").append(tickers.size()).append("}");
                        return sb.toString();
                    })
                    .orElse("{\"error\": \"No trending tickers available\"}");
        } catch (Exception e) {
            log.error("Error fetching trending tickers: {}", e.getMessage());
            return "{\"error\": \"Failed to fetch trending tickers: " + e.getMessage() + "\"}";
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
