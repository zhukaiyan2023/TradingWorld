package com.tradingworld.graph.edges;

import com.tradingworld.graph.state.AgentState;
import com.tradingworld.graph.state.InvestDebateState;

/**
 * 从研究员辩论到交易员的边转换。
 * 根据辩论完成情况确定路由。
 */
public class ResearcherToTraderEdge {

    /**
     * 确定是否应该转换到交易员。
     * 如果投资辩论已结束则返回true。
     */
    public static boolean shouldTransitionToTrader(AgentState state) {
        InvestDebateState debateState = state.getInvestmentDebateState();
        return debateState.getJudgeDecision() != null
            && !debateState.getJudgeDecision().isEmpty()
            && debateState.getCount() >= 1; // 辩论轮次已完成
    }

    /**
     * 获取转换的目标节点名称。
     */
    public static String getTargetNode() {
        return "trader";
    }
}
