package com.tradingworld.agents.analysts;

import com.tradingworld.agents.BaseAgent;
import com.tradingworld.graph.state.AgentState;
import com.tradingworld.tools.NewsTools;
import com.tradingworld.tools.InsiderTools;
import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 新闻分析师智能体，监控全球新闻和宏观经济指标。
 * 解读事件对市场状况的影响。
 */
public class NewsAnalyst implements BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(NewsAnalyst.class);

    private static final String NAME = "NewsAnalyst";
    private static final String SYSTEM_PROMPT = """
        你是一位专注于全球新闻和宏观经济指标的新闻分析师。
        你的职责是监控和解读全球新闻事件、宏观经济数据发布及其对金融市场的潜在影响。
        分析央行决策、经济指标、地缘政治事件和行业特定新闻。
        提供关于这些因素如何影响交易决策的见解。
        """;

    private final NewsTools newsTools;
    private final InsiderTools insiderTools;

    public NewsAnalyst(NewsTools newsTools, InsiderTools insiderTools) {
        this.newsTools = newsTools;
        this.insiderTools = insiderTools;
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
        log.info("Executing News Analyst for: {}", state.getCompanyOfInterest());
        try {
            String ticker = state.getCompanyOfInterest();

            // 获取公司新闻
            String news = newsTools.getNews(ticker);

            // 获取内幕交易信息
            String insider = insiderTools.getInsiderTransactions(ticker);

            // 获取相关的全球新闻（可扩展为行业特定新闻）
            String globalNews = newsTools.getGlobalNews(ticker);

            // 构建新闻报告
            String report = buildNewsReport(ticker, news, insider, globalNews);

            // 更新状态
            state.setNewsReport(report);
            state.setSender(NAME);

            return report;
        } catch (Exception e) {
            log.error("Error executing News Analyst: {}", e.getMessage(), e);
            return "Error: Failed to perform news analysis - " + e.getMessage();
        }
    }

    private String buildNewsReport(String ticker, String news, String insider, String globalNews) {
        return String.format("""
            News Analysis Report for %s
            ===========================

            Recent Company News:
            %s

            Insider Transactions:
            %s

            Relevant Global/Industry News:
            %s

            News Analysis:
            [To be filled by LLM with interpretation of news impact]
            """, ticker, news, insider, globalNews);
    }

    @Tool("Get recent news")
    public String getNews(String ticker) {
        return newsTools.getNews(ticker);
    }

    @Tool("Get insider transactions")
    public String getInsiderTransactions(String ticker) {
        return insiderTools.getInsiderTransactions(ticker);
    }

    @Tool("Get global news")
    public String getGlobalNews(String topic) {
        return newsTools.getGlobalNews(topic);
    }
}
