package com.tradingworld.graph.nodes;

import com.tradingworld.agents.BaseAgent;
import com.tradingworld.graph.state.AgentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 图形中分析师节点的基础类。
 * 提供分析师执行公共功能。
 */
public class AnalystNode {

    private static final Logger log = LoggerFactory.getLogger(AnalystNode.class);

    private final String name;
    private final BaseAgent agent;

    public AnalystNode(String name, BaseAgent agent) {
        this.name = name;
        this.agent = agent;
    }

    public String getName() {
        return name;
    }

    /**
     * 执行分析师智能体并返回结果。
     */
    public String execute(AgentState state) {
        log.debug("Executing node: {}", name);
        try {
            String result = agent.execute(state);
            log.debug("Node {} completed successfully", name);
            return result;
        } catch (Exception e) {
            log.error("Error executing node {}: {}", name, e.getMessage(), e);
            return "Error in " + name + ": " + e.getMessage();
        }
    }
}
