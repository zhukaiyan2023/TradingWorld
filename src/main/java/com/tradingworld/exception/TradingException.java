package com.tradingworld.exception;

/**
 * 交易相关异常。
 * 当交易执行、策略计算等交易相关操作发生错误时抛出此异常。
 */
public class TradingException extends RuntimeException {

    private final String ticker;
    private final String action;

    public TradingException(String message) {
        super(message);
        this.ticker = null;
        this.action = null;
    }

    public TradingException(String message, Throwable cause) {
        super(message, cause);
        this.ticker = null;
        this.action = null;
    }

    public TradingException(String message, String ticker, String action) {
        super(message);
        this.ticker = ticker;
        this.action = action;
    }

    public TradingException(String message, String ticker, String action, Throwable cause) {
        super(message, cause);
        this.ticker = ticker;
        this.action = action;
    }

    public String getTicker() {
        return ticker;
    }

    public String getAction() {
        return action;
    }

    @Override
    public String toString() {
        if (ticker != null && action != null) {
            return String.format("TradingException: %s [ticker=%s, action=%s]", getMessage(), ticker, action);
        }
        return super.toString();
    }
}
