package com.tradingworld.backtesting;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * 回测功能配置属性。
 * 映射到application.yml中的'trading.backtest'前缀。
 */
@ConfigurationProperties(prefix = "trading.backtest")
@Validated
public class BacktestConfig {

    @NotBlank
    private String defaultStartDate = "2020-01-01";

    @NotBlank
    private String defaultEndDate = "2025-12-31";

    @Positive
    private double defaultInitialCapital = 100000.0;

    @Positive
    private int defaultMaxPositions = 5;

    private boolean enableLookAheadBiasProtection = true;

    private String dataProvider = "yfinance";

    public String getDefaultStartDate() {
        return defaultStartDate;
    }

    public void setDefaultStartDate(String defaultStartDate) {
        this.defaultStartDate = defaultStartDate;
    }

    public String getDefaultEndDate() {
        return defaultEndDate;
    }

    public void setDefaultEndDate(String defaultEndDate) {
        this.defaultEndDate = defaultEndDate;
    }

    public double getDefaultInitialCapital() {
        return defaultInitialCapital;
    }

    public void setDefaultInitialCapital(double defaultInitialCapital) {
        this.defaultInitialCapital = defaultInitialCapital;
    }

    public int getDefaultMaxPositions() {
        return defaultMaxPositions;
    }

    public void setDefaultMaxPositions(int defaultMaxPositions) {
        this.defaultMaxPositions = defaultMaxPositions;
    }

    public boolean isEnableLookAheadBiasProtection() {
        return enableLookAheadBiasProtection;
    }

    public void setEnableLookAheadBiasProtection(boolean enableLookAheadBiasProtection) {
        this.enableLookAheadBiasProtection = enableLookAheadBiasProtection;
    }

    public String getDataProvider() {
        return dataProvider;
    }

    public void setDataProvider(String dataProvider) {
        this.dataProvider = dataProvider;
    }
}
