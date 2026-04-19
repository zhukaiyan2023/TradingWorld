package com.tradingworld.graph.state;

/**
 * 牛市和熊市研究员之间投资辩论的状态。
 * 跟踪对话历史和裁判决策。
 */
public class InvestDebateState {

    private String bullHistory;       // 看涨对话历史
    private String bearHistory;       // 看跌对话历史
    private String history;           // 一般对话历史
    private String currentResponse;   // 最新响应
    private String judgeDecision;     // 最终裁判决策
    private int count;                // 当前对话长度
    private String sender;            // 辩论中最后发言者

    public InvestDebateState() {
        this.bullHistory = "";
        this.bearHistory = "";
        this.history = "";
        this.currentResponse = "";
        this.judgeDecision = "";
        this.count = 0;
        this.sender = "";
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final InvestDebateState state = new InvestDebateState();

        public Builder bullHistory(String bullHistory) {
            state.bullHistory = bullHistory;
            return this;
        }

        public Builder bearHistory(String bearHistory) {
            state.bearHistory = bearHistory;
            return this;
        }

        public Builder history(String history) {
            state.history = history;
            return this;
        }

        public Builder currentResponse(String currentResponse) {
            state.currentResponse = currentResponse;
            return this;
        }

        public Builder judgeDecision(String judgeDecision) {
            state.judgeDecision = judgeDecision;
            return this;
        }

        public Builder count(int count) {
            state.count = count;
            return this;
        }

        public Builder sender(String sender) {
            state.sender = sender;
            return this;
        }

        public InvestDebateState build() {
            return state;
        }
    }

    // Getters and Setters
    public String getBullHistory() { return bullHistory; }
    public void setBullHistory(String bullHistory) { this.bullHistory = bullHistory; }

    public String getBearHistory() { return bearHistory; }
    public void setBearHistory(String bearHistory) { this.bearHistory = bearHistory; }

    public String getHistory() { return history; }
    public void setHistory(String history) { this.history = history; }

    public String getCurrentResponse() { return currentResponse; }
    public void setCurrentResponse(String currentResponse) { this.currentResponse = currentResponse; }

    public String getJudgeDecision() { return judgeDecision; }
    public void setJudgeDecision(String judgeDecision) { this.judgeDecision = judgeDecision; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    // 便捷方法
    public void incrementCount() {
        this.count++;
    }

    public void appendBullHistory(String text) {
        this.bullHistory = (this.bullHistory.isEmpty() ? "" : this.bullHistory + "\n") + text;
    }

    public void appendBearHistory(String text) {
        this.bearHistory = (this.bearHistory.isEmpty() ? "" : this.bearHistory + "\n") + text;
    }

    public void appendHistory(String text) {
        this.history = (this.history.isEmpty() ? "" : this.history + "\n") + text;
    }
}
