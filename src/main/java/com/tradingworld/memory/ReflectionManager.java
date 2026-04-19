package com.tradingworld.memory;

import com.tradingworld.graph.state.AgentState;
import com.tradingworld.dto.TradeDecision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 管理交易决策的反思并相应更新记忆。
 * 评估交易结果并将经验存储在FinancialSituationMemory中。
 */
public class ReflectionManager {

    private static final Logger log = LoggerFactory.getLogger(ReflectionManager.class);

    private final FinancialSituationMemory bullMemory;
    private final FinancialSituationMemory bearMemory;
    private final FinancialSituationMemory traderMemory;
    private final FinancialSituationMemory investJudgeMemory;
    private final FinancialSituationMemory portfolioManagerMemory;

    public ReflectionManager(
            FinancialSituationMemory bullMemory,
            FinancialSituationMemory bearMemory,
            FinancialSituationMemory traderMemory,
            FinancialSituationMemory investJudgeMemory,
            FinancialSituationMemory portfolioManagerMemory) {
        this.bullMemory = bullMemory;
        this.bearMemory = bearMemory;
        this.traderMemory = traderMemory;
        this.investJudgeMemory = investJudgeMemory;
        this.portfolioManagerMemory = portfolioManagerMemory;
    }

    /**
     * 反思交易决策并根据结果更新记忆。
     *
     * @param state 交易工作流程的最终状态
     * @param returnsLosses 交易的收益/损失（如果尚未知则可为null）
     */
    public void reflectAndRemember(AgentState state, Double returnsLosses) {
        log.info("Reflecting on trade decision for {}", state.getCompanyOfInterest());

        try {
            // 从当前状态构建情境描述
            String situation = buildSituationDescription(state);
            String advice = buildAdviceFromOutcome(state, returnsLosses);

            // 使用情境和结果更新所有记忆
            List<FinancialSituationMemory.Pair<String, String>> situationAndAdvice =
                    List.of(FinancialSituationMemory.Pair.of(situation, advice));

            bullMemory.addSituations(situationAndAdvice);
            bearMemory.addSituations(situationAndAdvice);
            traderMemory.addSituations(situationAndAdvice);
            investJudgeMemory.addSituations(situationAndAdvice);
            portfolioManagerMemory.addSituations(situationAndAdvice);

            log.info("Memories updated for {} - Situation: {}", state.getCompanyOfInterest(),
                    situation.substring(0, Math.min(100, situation.length())));

        } catch (Exception e) {
            log.error("Error during reflection: {}", e.getMessage(), e);
        }
    }

    /**
     * 从当前状态构建情境描述。
     */
    private String buildSituationDescription(AgentState state) {
        StringBuilder sb = new StringBuilder();
        sb.append("Company: ").append(state.getCompanyOfInterest()).append("\n");
        sb.append("Date: ").append(state.getTradeDate()).append("\n");
        sb.append("Market Analysis: ").append(state.getMarketReport() != null ? state.getMarketReport().substring(0, Math.min(200, state.getMarketReport().length())) : "N/A").append("\n");
        sb.append("Sentiment: ").append(state.getSentimentReport() != null ? state.getSentimentReport().substring(0, Math.min(200, state.getSentimentReport().length())) : "N/A").append("\n");
        sb.append("Fundamentals: ").append(state.getFundamentalsReport() != null ? state.getFundamentalsReport().substring(0, Math.min(200, state.getFundamentalsReport().length())) : "N/A").append("\n");
        return sb.toString();
    }

    /**
     * 根据交易结果构建建议字符串。
     */
    private String buildAdviceFromOutcome(AgentState state, Double returnsLosses) {
        StringBuilder sb = new StringBuilder();
        sb.append("Decision: ").append(state.getFinalTradeDecision() != null ? state.getFinalTradeDecision().substring(0, Math.min(200, state.getFinalTradeDecision().length())) : "N/A").append("\n");

        if (returnsLosses != null) {
            sb.append("Actual Return/Loss: ").append(String.format("%.2f%%", returnsLosses)).append("\n");
            if (returnsLosses > 0) {
                sb.append("Lesson: Positive outcome - what factors contributed?");
            } else if (returnsLosses < 0) {
                sb.append("Lesson: Negative outcome - what could have been improved?");
            } else {
                sb.append("Lesson: Neutral outcome - maintain current approach");
            }
        } else {
            sb.append("Note: Trade outcome not yet known - record decision rationale for future reference");
        }

        return sb.toString();
    }

    /**
     * 获取给定情境的相关记忆。
     */
    public List<FinancialSituationMemory.MemoryResult> getRelevantMemories(
            String situation, int nMatches) {
        // 合并所有记忆进行统一搜索
        // 在实践中，可能需要搜索特定记忆
        return traderMemory.getMemories(situation, nMatches);
    }

    /**
     * 清除所有反思记忆。
     */
    public void clearAllMemories() {
        bullMemory.clear();
        bearMemory.clear();
        traderMemory.clear();
        investJudgeMemory.clear();
        portfolioManagerMemory.clear();
        log.info("All reflection memories cleared");
    }
}
