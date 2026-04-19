package com.tradingworld.graph.nodes;

import com.tradingworld.agents.BaseAgent;
import com.tradingworld.graph.state.AgentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 交易员智能体的节点。
 */
public class TraderNode {

    private static final Logger log = LoggerFactory.getLogger(TraderNode.class);

    private final String name;
    private final BaseAgent agent;

    public TraderNode(String name, BaseAgent agent) {
        this.name = name;
        this.agent = agent;
    }

    public String getName() {
        return name;
    }

    /**
     * 执行交易员智能体并返回结果。
     */
    public String execute(AgentState state) {
        log.debug("Executing trader node: {}", name);
        try {
            String result = agent.execute(state);
            log.debug("Trader node {} completed", name);
            return result;
        } catch (Exception e) {
            log.error("Error executing trader node {}: {}", name, e.getMessage(), e);
            return "Error in trader node: " + e.getMessage();
        }
    }
}
