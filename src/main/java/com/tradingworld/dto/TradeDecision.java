package com.tradingworld.dto;

/**
 * 表示经过风险评估后的最终交易决策的数据传输对象。
 */
public class TradeDecision {

    public enum Action {
        BUY,
        SELL,
        HOLD,
        SKIP,
        ERROR
    }

    private Action action;
    private String signal;           // 完整信号文本
    private String reasoning;        // 总结推理
    private String company;
    private String tradeDate;
    private double confidenceScore;  // 0.0 - 1.0
    private double riskScore;        // 0.0 - 1.0（越高=风险越大）
    private long timestamp;

    public TradeDecision() {}

    public TradeDecision(Action action, String signal, String company, String tradeDate) {
        this.action = action;
        this.signal = signal;
        this.company = company;
        this.tradeDate = tradeDate;
        this.timestamp = System.currentTimeMillis();
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final TradeDecision d = new TradeDecision();

        public Builder action(Action action) {
            d.action = action;
            return this;
        }

        public Builder signal(String signal) {
            d.signal = signal;
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

        public Builder riskScore(double riskScore) {
            d.riskScore = riskScore;
            return this;
        }

        public Builder timestamp(long timestamp) {
            d.timestamp = timestamp;
            return this;
        }

        public TradeDecision build() {
            return d;
        }
    }

    // 常用决策的工厂方法
    public static TradeDecision skip(String reason) {
        return builder()
                .action(Action.SKIP)
                .reasoning(reason)
                .build();
    }

    public static TradeDecision hold() {
        return builder()
                .action(Action.HOLD)
                .reasoning("No clear signal generated")
                .build();
    }

    public static TradeDecision error(String errorMessage) {
        return builder()
                .action(Action.ERROR)
                .reasoning(errorMessage)
                .build();
    }

    // Getters and Setters
    public Action getAction() { return action; }
    public void setAction(Action action) { this.action = action; }

    public String getSignal() { return signal; }
    public void setSignal(String signal) { this.signal = signal; }

    public String getReasoning() { return reasoning; }
    public void setReasoning(String reasoning) { this.reasoning = reasoning; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getTradeDate() { return tradeDate; }
    public void setTradeDate(String tradeDate) { this.tradeDate = tradeDate; }

    public double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }

    public double getRiskScore() { return riskScore; }
    public void setRiskScore(double riskScore) { this.riskScore = riskScore; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
