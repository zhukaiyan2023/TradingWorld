package com.tradingworld.exception;

/**
 * 工具执行相关异常。
 * 当 AI 工具执行过程中发生错误时抛出此异常。
 */
public class ToolExecutionException extends RuntimeException {

    private final String toolName;
    private final String ticker;

    public ToolExecutionException(String message) {
        super(message);
        this.toolName = null;
        this.ticker = null;
    }

    public ToolExecutionException(String message, Throwable cause) {
        super(message, cause);
        this.toolName = null;
        this.ticker = null;
    }

    public ToolExecutionException(String message, String toolName, String ticker) {
        super(message);
        this.toolName = toolName;
        this.ticker = ticker;
    }

    public ToolExecutionException(String message, String toolName, String ticker, Throwable cause) {
        super(message, cause);
        this.toolName = toolName;
        this.ticker = ticker;
    }

    public String getToolName() {
        return toolName;
    }

    public String getTicker() {
        return ticker;
    }

    @Override
    public String toString() {
        if (toolName != null && ticker != null) {
            return String.format("ToolExecutionException: %s [tool=%s, ticker=%s]", getMessage(), toolName, ticker);
        } else if (toolName != null) {
            return String.format("ToolExecutionException: %s [tool=%s]", getMessage(), toolName);
        }
        return super.toString();
    }
}
