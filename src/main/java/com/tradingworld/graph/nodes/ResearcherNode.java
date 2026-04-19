package com.tradingworld.graph.nodes;

import com.tradingworld.agents.BaseAgent;
import com.tradingworld.graph.state.AgentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 图形中研究员节点的基础类。
 * 提供研究员执行公共功能。
 */
public class ResearcherNode {

    private static final Logger log = LoggerFactory.getLogger(ResearcherNode.class);

    private final String name;
    private final BaseAgent agent;

    public ResearcherNode(String name, BaseAgent agent) {
        this.name = name;
        this.agent = agent;
    }

    public String getName() {
        return name;
    }

    /**
     * 执行研究员智能体并返回结果。
     */
    public String execute(AgentState state) {
        log.debug("Executing researcher node: {}", name);
        try {
            String result = agent.execute(state);
            log.debug("Researcher node {} completed", name);
            return result;
        } catch (Exception e) {
            log.error("Error executing researcher node {}: {}", name, e.getMessage(), e);
            return "Error in researcher " + name + ": " + e.getMessage();
        }
    }
}
