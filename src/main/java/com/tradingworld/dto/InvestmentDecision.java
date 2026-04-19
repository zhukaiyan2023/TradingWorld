package com.tradingworld.dto;

/**
 * 表示交易员投资决策的数据传输对象。
 */
public class InvestmentDecision {

    public enum Decision {
        BUY,
        SELL,
        HOLD,
        SKIP
    }

    private Decision decision;
    private String reasoning;
    private String company;
    private String tradeDate;
    private double confidenceScore;  // 0.0 - 1.0
    private long timestamp;

    public InvestmentDecision() {}

    public InvestmentDecision(Decision decision, String reasoning, String company, String tradeDate) {
        this.decision = decision;
        this.reasoning = reasoning;
        this.company = company;
        this.tradeDate = tradeDate;
        this.confidenceScore = 0.0;
        this.timestamp = System.currentTimeMillis();
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final InvestmentDecision d = new InvestmentDecision();

        public Builder decision(Decision decision) {
            d.decision = decision;
            return this;
        }

        public Builder reasoning(String reasoning) {
            d.reasoning = reasoning;
            return this;
        }

        public Builder company(String company) {
            d.company = company;
            return this;
        }

        public Builder tradeDate(String tradeDate) {
            d.tradeDate = tradeDate;
            return this;
        }

        public Builder confidenceScore(double confidenceScore) {
            d.confidenceScore = confidenceScore;
            return this;
        }

        public Builder timestamp(long timestamp) {
            d.timestamp = timestamp;
            return this;
        }

        public InvestmentDecision build() {
            return d;
        }
    }

    // Getters and Setters
    public Decision getDecision() { return decision; }
    public void setDecision(Decision decision) { this.decision = decision; }

    public String getReasoning() { return reasoning; }
    public void setReasoning(String reasoning) { this.reasoning = reasoning; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getTradeDate() { return tradeDate; }
    public void setTradeDate(String tradeDate) { this.tradeDate = tradeDate; }

    public double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
