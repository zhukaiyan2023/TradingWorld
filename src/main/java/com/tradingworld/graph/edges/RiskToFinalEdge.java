package com.tradingworld.graph.edges;

import com.tradingworld.graph.state.AgentState;
import com.tradingworld.graph.state.RiskDebateState;

/**
 * 从风险管理到最终决策的边转换。
 */
public class RiskToFinalEdge {

    /**
     * 确定是否应该转换到最终决策。
     * 如果风险辩论已结束则返回true。
     */
    public static boolean shouldTransition(AgentState state) {
        RiskDebateState debateState = state.getRiskDebateState();
        return debateState.getJudgeDecision() != null
            && !debateState.getJudgeDecision().isEmpty()
            && debateState.getCount() >= 1; // 风险辩论轮次已完成
    }

    /**
     * 获取转换的目标节点名称。
     */
    public static String getTargetNode() {
        return "finalDecision";
    }
}
