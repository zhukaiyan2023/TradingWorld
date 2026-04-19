package com.tradingworld.exception;

/**
 * 数据访问相关异常。
 * 当从数据源（VendorRouter、DataVendor）获取数据时发生错误抛出此异常。
 */
public class DataAccessException extends RuntimeException {

    private final String ticker;
    private final String dataSource;

    public DataAccessException(String message) {
        super(message);
        this.ticker = null;
        this.dataSource = null;
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
        this.ticker = null;
        this.dataSource = null;
    }

    public DataAccessException(String message, String ticker, String dataSource) {
        super(message);
        this.ticker = ticker;
        this.dataSource = dataSource;
    }

    public DataAccessException(String message, String ticker, String dataSource, Throwable cause) {
        super(message, cause);
        this.ticker = ticker;
        this.dataSource = dataSource;
    }

    public String getTicker() {
        return ticker;
    }

    public String getDataSource() {
        return dataSource;
    }

    @Override
    public String toString() {
        if (ticker != null && dataSource != null) {
            return String.format("DataAccessException: %s [ticker=%s, source=%s]", getMessage(), ticker, dataSource);
        }
        return super.toString();
    }
}
