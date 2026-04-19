package com.tradingworld.dto;

/**
 * 表示辩论中消息的数据传输对象。
 */
public class DebateMessage {

    public enum SpeakerRole {
        BULL_RESEARCHER,
        BEAR_RESEARCHER,
        JUDGE,
        AGGRESSIVE_RISK,
        CONSERVATIVE_RISK,
        NEUTRAL_RISK,
        RISK_JUDGE
    }

    private SpeakerRole speaker;
    private String content;
    private int round;
    private long timestamp;

    public DebateMessage() {}

    public DebateMessage(SpeakerRole speaker, String content, int round) {
        this.speaker = speaker;
        this.content = content;
        this.round = round;
        this.timestamp = System.currentTimeMillis();
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final DebateMessage message = new DebateMessage();

        public Builder speaker(SpeakerRole speaker) {
            message.speaker = speaker;
            return this;
        }

        public Builder content(String content) {
            message.content = content;
            return this;
        }

        public Builder round(int round) {
            message.round = round;
            return this;
        }

        public Builder timestamp(long timestamp) {
            message.timestamp = timestamp;
            return this;
        }

        public DebateMessage build() {
            return message;
        }
    }

    // Getters and Setters
    public SpeakerRole getSpeaker() { return speaker; }
    public void setSpeaker(SpeakerRole speaker) { this.speaker = speaker; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getRound() { return round; }
    public void setRound(int round) { this.round = round; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
