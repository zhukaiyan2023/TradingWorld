package com.tradingworld.graph.nodes;

import com.tradingworld.agents.BaseAgent;
import com.tradingworld.graph.state.AgentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 图形中风险管理器节点的基础类。
 */
public class RiskNode {

    private static final Logger log = LoggerFactory.getLogger(RiskNode.class);

    private final String name;
    private final BaseAgent agent;

    public RiskNode(String name, BaseAgent agent) {
        this.name = name;
        this.agent = agent;
    }

    public String getName() {
        return name;
    }

    /**
     * 执行风险管理器智能体并返回结果。
     */
    public String execute(AgentState state) {
        log.debug("Executing risk node: {}", name);
        try {
            String result = agent.execute(state);
            log.debug("Risk node {} completed", name);
            return result;
        } catch (Exception e) {
            log.error("Error executing risk node {}: {}", name, e.getMessage(), e);
            return "Error in risk node " + name + ": " + e.getMessage();
        }
    }
}
