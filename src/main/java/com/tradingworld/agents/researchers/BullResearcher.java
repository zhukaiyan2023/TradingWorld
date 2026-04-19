package com.tradingworld.agents.researchers;

import com.tradingworld.agents.BaseAgent;
import com.tradingworld.graph.state.AgentState;
import com.tradingworld.graph.state.InvestDebateState;
import com.tradingworld.memory.FinancialSituationMemory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 牛市研究员智能体，主张投资机会。
 * 从分析师报告中提供看涨观点，突出积极信号。
 */
public class BullResearcher implements BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(BullResearcher.class);

    private static final String NAME = "BullResearcher";
    private static final String SYSTEM_PROMPT = """
        你是一位牛市研究员，提供建设性的投资分析。
        你的职责是批判性地评估分析师报告，并提出投资看涨观点。
        你突出积极信号、增长机会和有利的市场状况。
        你与熊市研究员进行辩论，以确保分析平衡。
        重点识别：增长催化剂、被低估的资产、积极趋势和战略优势。
        """;

    private final FinancialSituationMemory memory;
    private final int maxDebateRounds;

    public BullResearcher(FinancialSituationMemory memory, int maxDebateRounds) {
        this.memory = memory;
        this.maxDebateRounds = maxDebateRounds;
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
        log.info("Executing Bullish Researcher for: {}", state.getCompanyOfInterest());
        try {
            InvestDebateState debateState = state.getInvestmentDebateState();

            // 获取相关的记忆上下文
            String contextFromMemory = getMemoryContext(state);

            // 基于分析师报告构建看涨论点
            String bullCase = buildBullishCase(state, contextFromMemory);

            // 更新辩论状态
            debateState.appendBullHistory(NAME + ": " + bullCase);
            debateState.setCurrentResponse(bullCase);
            debateState.setSender(NAME);

            state.setInvestmentDebateState(debateState);

            return bullCase;
        } catch (Exception e) {
            log.error("Error executing Bullish Researcher: {}", e.getMessage(), e);
            return "Error: Failed to build bullish case - " + e.getMessage();
        }
    }

    private String getMemoryContext(AgentState state) {
        StringBuilder context = new StringBuilder();
        context.append("Current Market Report:\n").append(state.getMarketReport()).append("\n\n");
        context.append("Current Sentiment Report:\n").append(state.getSentimentReport()).append("\n\n");
        context.append("Current Fundamentals Report:\n").append(state.getFundamentalsReport()).append("\n\n");

        // 从记忆中获取相关的历史情境
        if (memory != null && memory.size() > 0) {
            String situation = buildSituationDescription(state);
            var memories = memory.getMemories(situation, 2);
            if (!memories.isEmpty()) {
                context.append("\nRelevant Historical Patterns:\n");
                for (var mem : memories) {
                    context.append("- ").append(mem.getRecommendation()).append("\n");
                }
            }
        }

        return context.toString();
    }

    private String buildSituationDescription(AgentState state) {
        return String.format("""
            %s on %s:
            Market analysis showing positive momentum.
            Sentiment trending bullish.
            Fundamentals indicate strong performance.
            """, state.getCompanyOfInterest(), state.getTradeDate());
    }

    private String buildBullishCase(AgentState state, String context) {
        return String.format("""
            BULLISH ANALYSIS for %s
            ======================

            MARKET CONTEXT:
            %s

            BULLISH THESIS:
            [To be filled by LLM with specific bullish arguments]

            Key Bullish Points:
            1. [Growth catalyst to identify]
            2. [Undervalued aspect to highlight]
            3. [Positive trend to emphasize]
            4. [Strategic advantage to note]

            Investment Recommendation: BULLISH
            """, state.getCompanyOfInterest(), context);
    }

    // 用于反思集成的记忆设置方法
    public void setMemory(FinancialSituationMemory memory) {
        // 用于反思集成
    }
}
