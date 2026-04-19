package com.tradingworld.dataflows;

/**
 * 当所有数据供应商都未能返回数据时抛出此异常。
 */
public class DataFetchException extends RuntimeException {

    private final String symbol;
    private final String dataType;

    public DataFetchException(String message) {
        super(message);
        this.symbol = null;
        this.dataType = null;
    }

    public DataFetchException(String message, String symbol, String dataType) {
        super(message);
        this.symbol = symbol;
        this.dataType = dataType;
    }

    public DataFetchException(String message, Throwable cause) {
        super(message, cause);
        this.symbol = null;
        this.dataType = null;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getDataType() {
        return dataType;
    }
}
