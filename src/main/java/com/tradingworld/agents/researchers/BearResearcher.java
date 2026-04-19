package com.tradingworld.agents.researchers;

import com.tradingworld.agents.BaseAgent;
import com.tradingworld.graph.state.AgentState;
import com.tradingworld.graph.state.InvestDebateState;
import com.tradingworld.memory.FinancialSituationMemory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 熊市研究员智能体，强调投资风险。
 * 从分析师报告中提供看跌观点，突出潜在负面影响。
 */
public class BearResearcher implements BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(BearResearcher.class);

    private static final String NAME = "BearResearcher";
    private static final String SYSTEM_PROMPT = """
        你是一位熊市研究员，提供批判性的投资分析。
        你的职责是批判性地评估分析师报告，并提出投资看跌观点。
        你突出风险、负面信号、高估值和不利的市状况。
        你与牛市研究员进行辩论，以确保分析平衡。
        重点识别：风险因素、逆风因素、高估值方面和潜在陷阱。
        """;

    private final FinancialSituationMemory memory;
    private final int maxDebateRounds;

    public BearResearcher(FinancialSituationMemory memory, int maxDebateRounds) {
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
        log.info("Executing Bearish Researcher for: {}", state.getCompanyOfInterest());
        try {
            InvestDebateState debateState = state.getInvestmentDebateState();

            // 获取相关的记忆上下文
            String contextFromMemory = getMemoryContext(state);

            // 基于分析师报告构建看跌论点
            String bearCase = buildBearishCase(state, contextFromMemory);

            // 更新辩论状态
            debateState.appendBearHistory(NAME + ": " + bearCase);
            debateState.setCurrentResponse(bearCase);
            debateState.setSender(NAME);

            state.setInvestmentDebateState(debateState);

            return bearCase;
        } catch (Exception e) {
            log.error("Error executing Bearish Researcher: {}", e.getMessage(), e);
            return "Error: Failed to build bearish case - " + e.getMessage();
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
            Market analysis showing potential concerns.
            Sentiment may be overly optimistic.
            Fundamentals could face headwinds.
            """, state.getCompanyOfInterest(), state.getTradeDate());
    }

    private String buildBearishCase(AgentState state, String context) {
        return String.format("""
            BEARISH ANALYSIS for %s
            ======================

            MARKET CONTEXT:
            %s

            BEARISH THESIS:
            [To be filled by LLM with specific bearish arguments]

            Key Risk Factors:
            1. [Headwind to identify]
            2. [Overvalued aspect to highlight]
            3. [Negative trend to emphasize]
            4. [Potential pitfall to note]

            Investment Recommendation: BEARISH
            """, state.getCompanyOfInterest(), context);
    }
}
