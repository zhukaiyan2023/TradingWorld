package com.tradingworld.agents.analysts;

import com.tradingworld.agents.BaseAgent;
import com.tradingworld.graph.state.AgentState;
import com.tradingworld.tools.MarketTools;
import com.tradingworld.tools.TechnicalTools;
import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 市场分析师智能体，专长于技术分析。
 * 使用技术指标评估股票市场状况。
 */
public class MarketAnalyst implements BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(MarketAnalyst.class);

    private static final String NAME = "MarketAnalyst";
    private static final String SYSTEM_PROMPT = """
        你是一位专注于技术分析的市场分析师。
        你的职责是使用RSI、MACD、布林带等技术指标来评估股票市场状况。
        提供关于价格趋势、动量和市场波动的简洁数据驱动见解。
        在分析中务必引用具体指标数值。
        """;

    private final MarketTools marketTools;
    private final TechnicalTools technicalTools;

    public MarketAnalyst(MarketTools marketTools, TechnicalTools technicalTools) {
        this.marketTools = marketTools;
        this.technicalTools = technicalTools;
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
        log.info("Executing Market Analyst for: {}", state.getCompanyOfInterest());
        try {
            String ticker = state.getCompanyOfInterest();
            String tradeDate = state.getTradeDate();

            // 获取股票数据
            String stockData = marketTools.getStockData(ticker);

            // 获取技术指标
            String indicators = technicalTools.getIndicators(ticker, "RSI,MACD,BB,SMA,EMA");

            // 获取历史数据作为参考
            String historicalData = marketTools.getHistoricalData(ticker, "1m");

            // 构建分析报告
            String report = buildMarketReport(ticker, stockData, indicators, historicalData);

            // 更新状态
            state.setMarketReport(report);
            state.setSender(NAME);

            return report;
        } catch (Exception e) {
            log.error("Error executing Market Analyst: {}", e.getMessage(), e);
            return "Error: Failed to perform market analysis - " + e.getMessage();
        }
    }

    private String buildMarketReport(String ticker, String stockData, String indicators, String historicalData) {
        return String.format("""
            Market Analysis Report for %s
            ==============================

            Stock Data:
            %s

            Technical Indicators:
            %s

            Historical Context:
            %s

            Analysis Summary:
            [To be filled by LLM with interpretation of indicators]
            """, ticker, stockData, indicators, historicalData);
    }

    // 供LLM使用的工具方法
    @Tool("Get stock data")
    public String getStockData(String ticker) {
        return marketTools.getStockData(ticker);
    }

    @Tool("Get technical indicators")
    public String getIndicators(String ticker, String indicators) {
        return technicalTools.getIndicators(ticker, indicators);
    }
}
