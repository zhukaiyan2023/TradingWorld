package com.tradingworld.backtesting;

/**
 * 交易成本配置，包含手续费、滑点等交易成本参数。
 */
public class TradingCostConfig {

    private final double commissionRate;
    private final double slippagePercent;

    public TradingCostConfig(double commissionRate, double slippagePercent) {
        this.commissionRate = commissionRate;
        this.slippagePercent = slippagePercent;
    }

    public static TradingCostConfig defaults() {
        return new TradingCostConfig(0.001, 0.0005);
    }

    public double getCommissionRate() {
        return commissionRate;
    }

    public double getSlippagePercent() {
        return slippagePercent;
    }

    public double calculateCommission(double amount) {
        return amount * commissionRate;
    }

    public double getBuyPrice(double marketPrice) {
        return marketPrice * (1 + slippagePercent);
    }

    public double getSellPrice(double marketPrice) {
        return marketPrice * (1 - slippagePercent);
    }
}
