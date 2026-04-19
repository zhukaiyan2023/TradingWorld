package com.tradingworld.agents;

import com.tradingworld.graph.state.AgentState;

/**
 * TradingAgents框架中所有代理的基类接口。
 * 定义了分析师、研究员、交易员和风险管理器代理的通用契约。
 */
public interface BaseAgent {

    /**
     * 获取代理的名称/角色标识符。
     */
    String getName();

    /**
     * 执行代理的分析或决策。
     *
     * @param state 当前图状态
     * @return 更新后的状态或结果字符串
     */
    String execute(AgentState state);

    /**
     * 获取定义代理角色和行为的系统提示词。
     */
    String getSystemPrompt();
}
