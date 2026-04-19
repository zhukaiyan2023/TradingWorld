package com.tradingworld.dto;

/**
 * 表示分析师报告的数据传输对象。
 */
public class AnalystReport {

    public enum AnalystType {
        MARKET,
        SENTIMENT,
        NEWS,
        FUNDAMENTALS
    }

    private AnalystType type;
    private String company;
    private String tradeDate;
    private String content;
    private long timestamp;

    public AnalystReport() {}

    public AnalystReport(AnalystType type, String company, String tradeDate, String content) {
        this.type = type;
        this.company = company;
        this.tradeDate = tradeDate;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final AnalystReport report = new AnalystReport();

        public Builder type(AnalystType type) {
            report.type = type;
            return this;
        }

        public Builder company(String company) {
            report.company = company;
            return this;
        }

        public Builder tradeDate(String tradeDate) {
            report.tradeDate = tradeDate;
            return this;
        }

        public Builder content(String content) {
            report.content = content;
            return this;
        }

        public Builder timestamp(long timestamp) {
            report.timestamp = timestamp;
            return this;
        }

        public AnalystReport build() {
            return report;
        }
    }

    // Getters and Setters
    public AnalystType getType() { return type; }
    public void setType(AnalystType type) { this.type = type; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getTradeDate() { return tradeDate; }
    public void setTradeDate(String tradeDate) { this.tradeDate = tradeDate; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
