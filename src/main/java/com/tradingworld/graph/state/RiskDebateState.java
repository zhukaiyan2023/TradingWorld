package com.tradingworld.graph.state;

/**
 * 激进型、保守型和中性风险管理器之间风险辩论的状态。
 * 跟踪对话历史和裁判决策。
 */
public class RiskDebateState {

    private String aggressiveHistory;     // 激进型智能体的对话历史
    private String conservativeHistory;  // 保守型智能体的对话历史
    private String neutralHistory;        // 中性智能体的对话历史
    private String history;               // 一般对话历史
    private String latestSpeaker;         // 最后发言的分析师
    private String currentAggressiveResponse;
    private String currentConservativeResponse;
    private String currentNeutralResponse;
    private String judgeDecision;
    private int count;                    // 当前对话长度

    public RiskDebateState() {
        this.aggressiveHistory = "";
        this.conservativeHistory = "";
        this.neutralHistory = "";
        this.history = "";
        this.latestSpeaker = "";
        this.currentAggressiveResponse = "";
        this.currentConservativeResponse = "";
        this.currentNeutralResponse = "";
        this.judgeDecision = "";
        this.count = 0;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final RiskDebateState state = new RiskDebateState();

        public Builder aggressiveHistory(String aggressiveHistory) {
            state.aggressiveHistory = aggressiveHistory;
            return this;
        }

        public Builder conservativeHistory(String conservativeHistory) {
            state.conservativeHistory = conservativeHistory;
            return this;
        }

        public Builder neutralHistory(String neutralHistory) {
            state.neutralHistory = neutralHistory;
            return this;
        }

        public Builder history(String history) {
            state.history = history;
            return this;
        }

        public Builder latestSpeaker(String latestSpeaker) {
            state.latestSpeaker = latestSpeaker;
            return this;
        }

        public Builder currentAggressiveResponse(String currentAggressiveResponse) {
            state.currentAggressiveResponse = currentAggressiveResponse;
            return this;
        }

        public Builder currentConservativeResponse(String currentConservativeResponse) {
            state.currentConservativeResponse = currentConservativeResponse;
            return this;
        }

        public Builder currentNeutralResponse(String currentNeutralResponse) {
            state.currentNeutralResponse = currentNeutralResponse;
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

        public RiskDebateState build() {
            return state;
        }
    }

    // Getters and Setters
    public String getAggressiveHistory() { return aggressiveHistory; }
    public void setAggressiveHistory(String aggressiveHistory) { this.aggressiveHistory = aggressiveHistory; }

    public String getConservativeHistory() { return conservativeHistory; }
    public void setConservativeHistory(String conservativeHistory) { this.conservativeHistory = conservativeHistory; }

    public String getNeutralHistory() { return neutralHistory; }
    public void setNeutralHistory(String neutralHistory) { this.neutralHistory = neutralHistory; }

    public String getHistory() { return history; }
    public void setHistory(String history) { this.history = history; }

    public String getLatestSpeaker() { return latestSpeaker; }
    public void setLatestSpeaker(String latestSpeaker) { this.latestSpeaker = latestSpeaker; }

    public String getCurrentAggressiveResponse() { return currentAggressiveResponse; }
    public void setCurrentAggressiveResponse(String currentAggressiveResponse) { this.currentAggressiveResponse = currentAggressiveResponse; }

    public String getCurrentConservativeResponse() { return currentConservativeResponse; }
    public void setCurrentConservativeResponse(String currentConservativeResponse) { this.currentConservativeResponse = currentConservativeResponse; }

    public String getCurrentNeutralResponse() { return currentNeutralResponse; }
    public void setCurrentNeutralResponse(String currentNeutralResponse) { this.currentNeutralResponse = currentNeutralResponse; }

    public String getJudgeDecision() { return judgeDecision; }
    public void setJudgeDecision(String judgeDecision) { this.judgeDecision = judgeDecision; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    // 便捷方法
    public void incrementCount() {
        this.count++;
    }

    public void appendAggressiveHistory(String text) {
        this.aggressiveHistory = (this.aggressiveHistory.isEmpty() ? "" : this.aggressiveHistory + "\n") + text;
    }

    public void appendConservativeHistory(String text) {
        this.conservativeHistory = (this.conservativeHistory.isEmpty() ? "" : this.conservativeHistory + "\n") + text;
    }

    public void appendNeutralHistory(String text) {
        this.neutralHistory = (this.neutralHistory.isEmpty() ? "" : this.neutralHistory + "\n") + text;
    }

    public void appendHistory(String text) {
        this.history = (this.history.isEmpty() ? "" : this.history + "\n") + text;
    }
}
