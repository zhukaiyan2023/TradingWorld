package com.tradingworld.agents.analysts;

import com.tradingworld.agents.BaseAgent;
import com.tradingworld.graph.state.AgentState;
import com.tradingworld.tools.NewsTools;
import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 情感分析师智能体，分析社交媒体和公众情绪。
 * 使用情感评分算法来衡量短期市场情绪。
 */
public class SentimentAnalyst implements BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(SentimentAnalyst.class);

    private static final String NAME = "SentimentAnalyst";
    private static final String SYSTEM_PROMPT = """
        你是一位专注于社交媒体和公众情绪分析的情绪分析师。
        你的职责是通过分析新闻、社交媒体和公众舆论的情绪来衡量短期市场情绪。
        使用情绪评分来识别看涨或看跌倾向。
        提供关于市场情绪及任何重大变化 的简洁见解。
        """;

    private final NewsTools newsTools;

    public SentimentAnalyst(NewsTools newsTools) {
        this.newsTools = newsTools;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getSystemPrompt() {
        return SYSTEM_PROMPT;
    }

    @Override
    public String execute(AgentState state) {
        log.info("Executing Sentiment Analyst for: {}", state.getCompanyOfInterest());
        try {
            String ticker = state.getCompanyOfInterest();

            // 获取新闻情感
            String sentiment = newsTools.getSentiment(ticker);

            // 获取近期新闻
            String news = newsTools.getNews(ticker);

            // 构建情感报告
            String report = buildSentimentReport(ticker, sentiment, news);

            // 更新状态
            state.setSentimentReport(report);
            state.setSender(NAME);

            return report;
        } catch (Exception e) {
            log.error("Error executing Sentiment Analyst: {}", e.getMessage(), e);
            return "Error: Failed to perform sentiment analysis - " + e.getMessage();
        }
    }

    private String buildSentimentReport(String ticker, String sentiment, String news) {
        return String.format("""
            Sentiment Analysis Report for %s
            =================================

            Overall Sentiment:
            %s

            Recent News Headlines:
            %s

            Sentiment Analysis:
            [To be filled by LLM with interpretation of sentiment scores and news]
            """, ticker, sentiment, news);
    }

    @Tool("Get sentiment analysis")
    public String getSentiment(String ticker) {
        return newsTools.getSentiment(ticker);
    }

    @Tool("Get recent news")
    public String getNews(String ticker) {
        return newsTools.getNews(ticker);
    }
}
