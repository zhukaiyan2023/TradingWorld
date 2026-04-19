package com.tradingworld.graph.edges;

import com.tradingworld.graph.state.AgentState;

/**
 * 从分析师团队到研究员团队的边转换。
 * 根据所有分析师是否完成来确定路由。
 */
public class AnalystToResearcherEdge {

    /**
     * 确定是否应该转换到研究员。
     * 如果所有必需的分析师报告都存在则返回true。
     */
    public static boolean shouldTransition(AgentState state) {
        return state.getMarketReport() != null && !state.getMarketReport().isEmpty()
            && state.getSentimentReport() != null && !state.getSentimentReport().isEmpty()
            && state.getNewsReport() != null && !state.getNewsReport().isEmpty()
            && state.getFundamentalsReport() != null && !state.getFundamentalsReport().isEmpty();
    }

    /**
     * 获取转换的目标节点名称。
     */
    public static String getTargetNode() {
        return "bullResearcher";
    }
}
