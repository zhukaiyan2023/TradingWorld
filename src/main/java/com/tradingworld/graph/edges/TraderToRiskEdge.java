package com.tradingworld.graph.edges;

import com.tradingworld.graph.state.AgentState;

/**
 * 从交易员到风险管理团队的边转换。
 */
public class TraderToRiskEdge {

    /**
     * 确定是否应该转换到风险管理。
     * 如果交易员已做出投资决策则返回true。
     */
    public static boolean shouldTransition(AgentState state) {
        return state.getTraderInvestmentPlan() != null
            && !state.getTraderInvestmentPlan().isEmpty();
    }

    /**
     * 获取转换的目标节点名称。
     */
    public static String getTargetNode() {
        return "aggressiveRisk"; // 从激进型风险管理器开始
    }
}
