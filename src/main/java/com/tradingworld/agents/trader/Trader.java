package com.tradingworld.agents.trader;

import com.tradingworld.agents.BaseAgent;
import com.tradingworld.graph.state.AgentState;
import com.tradingworld.graph.state.InvestDebateState;
import com.tradingworld.memory.FinancialSituationMemory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 交易员代理，负责整合分析师和研究员的报告以做出明智的交易决策。
 * 根据全面的市场洞察确定交易的时机和规模。
 */
public class Trader implements BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(Trader.class);

    private static final String NAME = "Trader";
    private static final String SYSTEM_PROMPT = """
        你是一个交易员，综合所有分析师和研究员的研究结果来做出明智的交易决策。
        你的职责是评估看涨和看跌的情况，评估投资辩论，并确定适当的交易行动。
        你需要考虑：时机、仓位大小、风险回报率和市场状况。
        提供明确的买入、卖出或持有建议，并附带具体理由。
        """;

    private final FinancialSituationMemory memory;

    public Trader(FinancialSituationMemory memory) {
        this.memory = memory;
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
        log.info("Executing Trader for: {}", state.getCompanyOfInterest());
        try {
            InvestDebateState debateState = state.getInvestmentDebateState();

            // 综合所有分析师报告
            String synthesis = synthesizeReports(state);

            // 根据辩论结果做出交易决策
            String decision = makeDecision(state, synthesis);

            // 更新状态
            state.setTraderInvestmentPlan(decision);
            state.setSender(NAME);

            return decision;
        } catch (Exception e) {
            log.error("Error executing Trader: {}", e.getMessage(), e);
            return "Error: Failed to make trading decision - " + e.getMessage();
        }
    }

    private String synthesizeReports(AgentState state) {
        StringBuilder synthesis = new StringBuilder();
        InvestDebateState debateState = state.getInvestmentDebateState();

        synthesis.append("=== 分析师报告 ===\n\n");
        synthesis.append("市场分析：\n").append(state.getMarketReport()).append("\n\n");
        synthesis.append("情绪分析：\n").append(state.getSentimentReport()).append("\n\n");
        synthesis.append("新闻分析：\n").append(state.getNewsReport()).append("\n\n");
        synthesis.append("基本面分析：\n").append(state.getFundamentalsReport()).append("\n\n");

        synthesis.append("=== 投资辩论 ===\n\n");
        synthesis.append("看涨理由：\n").append(debateState.getBullHistory()).append("\n\n");
        synthesis.append("看跌理由：\n").append(debateState.getBearHistory()).append("\n\n");
        synthesis.append("裁判决定：\n").append(debateState.getJudgeDecision()).append("\n\n");

        return synthesis.toString();
    }

    private String makeDecision(AgentState state, String synthesis) {
        return String.format("""
            %s 的交易决策
            ========================

            公司：%s
            交易日期：%s

            投资辩论摘要：
            %s

            交易建议：
            [由LLM填入具体的买入/卖出/持有建议]

            仓位大小：[根据信心程度确定]
            入场价格：[当前市场价格]
            止损位：[风险管理水平]
            目标价格：[回报目标]

            信心水平：[根据辩论共识的高/中/低]
            """,
            state.getCompanyOfInterest(),
            state.getCompanyOfInterest(),
            state.getTradeDate(),
            synthesis);
    }
}
