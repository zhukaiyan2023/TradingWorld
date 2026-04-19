package com.tradingworld.graph.state;

import java.util.ArrayList;
import java.util.List;

/**
 * TradingAgents图形的主要状态对象。
 * 跟踪所有分析师报告、辩论状态和最终决策。
 */
public class AgentState {

    // 公司和交易上下文
    private String companyOfInterest;
    private String tradeDate;
    private String sender;

    // 分析师报告
    private String marketReport;
    private String sentimentReport;
    private String newsReport;
    private String fundamentalsReport;

    // 投资辩论状态
    private InvestDebateState investmentDebateState;

    // 投资计划
    private String investmentPlan;
    private String traderInvestmentPlan;

    // 风险辩论状态
    private RiskDebateState riskDebateState;

    // 最终决策
    private String finalTradeDecision;

    // 消息（LangChain4j使用此进行聊天记忆）
    private List<ChatMessage> messages;

    public AgentState() {
        this.messages = new ArrayList<>();
        this.investmentDebateState = new InvestDebateState();
        this.riskDebateState = new RiskDebateState();
    }

    // 用于更易构建的Builder模式
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final AgentState state = new AgentState();

        public Builder companyOfInterest(String companyOfInterest) {
            state.companyOfInterest = companyOfInterest;
            return this;
        }

        public Builder tradeDate(String tradeDate) {
            state.tradeDate = tradeDate;
            return this;
        }

        public Builder sender(String sender) {
            state.sender = sender;
            return this;
        }

        public Builder marketReport(String marketReport) {
            state.marketReport = marketReport;
            return this;
        }

        public Builder sentimentReport(String sentimentReport) {
            state.sentimentReport = sentimentReport;
            return this;
        }

        public Builder newsReport(String newsReport) {
            state.newsReport = newsReport;
            return this;
        }

        public Builder fundamentalsReport(String fundamentalsReport) {
            state.fundamentalsReport = fundamentalsReport;
            return this;
        }

        public Builder investmentDebateState(InvestDebateState investmentDebateState) {
            state.investmentDebateState = investmentDebateState;
            return this;
        }

        public Builder investmentPlan(String investmentPlan) {
            state.investmentPlan = investmentPlan;
            return this;
        }

        public Builder traderInvestmentPlan(String traderInvestmentPlan) {
            state.traderInvestmentPlan = traderInvestmentPlan;
            return this;
        }

        public Builder riskDebateState(RiskDebateState riskDebateState) {
            state.riskDebateState = riskDebateState;
            return this;
        }

        public Builder finalTradeDecision(String finalTradeDecision) {
            state.finalTradeDecision = finalTradeDecision;
            return this;
        }

        public Builder messages(List<ChatMessage> messages) {
            state.messages = messages;
            return this;
        }

        public AgentState build() {
            return state;
        }
    }

    // Getters and Setters
    public String getCompanyOfInterest() { return companyOfInterest; }
    public void setCompanyOfInterest(String companyOfInterest) { this.companyOfInterest = companyOfInterest; }

    public String getTradeDate() { return tradeDate; }
    public void setTradeDate(String tradeDate) { this.tradeDate = tradeDate; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getMarketReport() { return marketReport; }
    public void setMarketReport(String marketReport) { this.marketReport = marketReport; }

    public String getSentimentReport() { return sentimentReport; }
    public void setSentimentReport(String sentimentReport) { this.sentimentReport = sentimentReport; }

    public String getNewsReport() { return newsReport; }
    public void setNewsReport(String newsReport) { this.newsReport = newsReport; }

    public String getFundamentalsReport() { return fundamentalsReport; }
    public void setFundamentalsReport(String fundamentalsReport) { this.fundamentalsReport = fundamentalsReport; }

    public InvestDebateState getInvestmentDebateState() { return investmentDebateState; }
    public void setInvestmentDebateState(InvestDebateState investmentDebateState) { this.investmentDebateState = investmentDebateState; }

    public String getInvestmentPlan() { return investmentPlan; }
    public void setInvestmentPlan(String investmentPlan) { this.investmentPlan = investmentPlan; }

    public String getTraderInvestmentPlan() { return traderInvestmentPlan; }
    public void setTraderInvestmentPlan(String traderInvestmentPlan) { this.traderInvestmentPlan = traderInvestmentPlan; }

    public RiskDebateState getRiskDebateState() { return riskDebateState; }
    public void setRiskDebateState(RiskDebateState riskDebateState) { this.riskDebateState = riskDebateState; }

    public String getFinalTradeDecision() { return finalTradeDecision; }
    public void setFinalTradeDecision(String finalTradeDecision) { this.finalTradeDecision = finalTradeDecision; }

    public List<ChatMessage> getMessages() { return messages; }
    public void setMessages(List<ChatMessage> messages) { this.messages = messages; }

    // 添加消息的便捷方法
    public void addMessage(ChatMessage message) {
        this.messages.add(message);
    }

    /**
     * 简单的聊天消息表示。
     * 在LangChain4j中，这将是dev.langchain4j.data.message.ChatMessage，
     * 但我们使用简单的POJO进行状态序列化。
     */
    public static class ChatMessage {
        private String role;      // "user", "assistant", "system"
        private String content;

        public ChatMessage() {}

        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}
