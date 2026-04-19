/package com.tradingworld.backtesting.metrics;

import com.tradingworld.backtesting.BacktestEngine;
import com.tradingworld.backtesting.BacktestEngine.HistoricalData;
import com.tradingworld.backtesting.BacktestEngine.TradeRecord;

import java.util.List;

/**
 * 回测性能指标计算器。
 * 计算并返回策略表现的各项指标，包括收益率、夏普比率、最大回撤等。
 */
public class BacktestMetrics {

    private final List<TradeRecord> trades;
    private final double initialCapital;
    private final double finalValue;
    private final List<HistoricalData> historicalData;

    private static final double RISK_FREE_RATE = 0.0; // 假设无风险利率为0

    public BacktestMetrics(List<TradeRecord> trades, double initialCapital, double finalValue, List<HistoricalData> historicalData) {
        this.trades = trades;
        this.initialCapital = initialCapital;
        this.finalValue = finalValue;
        this.historicalData = historicalData;
    }

    /**
     * 计算总收益率
     *
     * @return 总收益率百分比
     */
    public double calculateTotalReturn() {
        if (initialCapital <= 0) {
            return 0;
        }
        return ((finalValue - initialCapital) / initialCapital) * 100;
    }

    /**
     * 计算年化收益率
     *
     * @return 年化收益率百分比
     */
    public double calculateAnnualizedReturn() {
        if (historicalData == null || historicalData.isEmpty()) {
            return 0;
        }

        // 计算回测周期（天数）
        HistoricalData firstData = historicalData.get(0);
        HistoricalData lastData = historicalData.get(historicalData.size() - 1);
        long days = java.time.temporal.ChronoUnit.DAYS.between(firstData.date, lastData.date);

        if (days <= 0) {
            return 0;
        }

        // 计算年化收益率
        double totalReturn = finalValue / initialCapital;
        double years = days / 365.0;
        double annualizedReturn = (Math.pow(totalReturn, 1.0 / years) - 1) * 100;

        return annualizedReturn;
    }

    /**
     * 计算夏普比率
     *
     * @return 夏普比率
     */
    public double calculateSharpeRatio() {
        if (historicalData == null || historicalData.size() < 2) {
            return 0;
        }

        // 计算每日收益率
        List<Double> dailyReturns = calculateDailyReturns();
        if (dailyReturns.isEmpty()) {
            return 0;
        }

        // 计算平均收益率
        double avgReturn = dailyReturns.stream().mapToDouble(d -> d).average().orElse(0);

        // 计算收益率标准差
        double variance = dailyReturns.stream()
            .mapToDouble(d -> Math.pow(d - avgReturn, 2))
            .average()
            .orElse(0);
        double stdDev = Math.sqrt(variance);

        // 夏普比率 = (平均收益率 - 无风险利率) / 标准差
        if (stdDev == 0) {
            return 0;
        }

        double dailySharpe = (avgReturn - RISK_FREE_RATE / 365) / stdDev;
        // 年化夏普比率
        return dailySharpe * Math.sqrt(252);
    }

    /**
     * 计算最大回撤
     *
     * @return 最大回撤百分比
     */
    public double calculateMaxDrawdown() {
        if (historicalData == null || historicalData.isEmpty()) {
            return 0;
        }

        double maxDrawdown = 0;
        double peak = initialCapital;
        double currentValue = initialCapital;

        for (TradeRecord trade : trades) {
            if ("BUY".equals(trade.action)) {
                currentValue -= trade.quantity * trade.price;
            } else if ("SELL".equals(trade.action)) {
                currentValue += trade.quantity * trade.price;
            }

            if (currentValue > peak) {
                peak = currentValue;
            }

            double drawdown = ((peak - currentValue) / peak) * 100;
            if (drawdown > maxDrawdown) {
                maxDrawdown = drawdown;
            }
        }

        return maxDrawdown;
    }

    /**
     * 计算胜率
     *
     * @return 盈利交易占总交易数的比例（百分比）
     */
    public double calculateWinRate() {
        if (trades == null || trades.isEmpty()) {
            return 0;
        }

        // 按股票分组计算盈亏
        java.util.Map<String, java.util.List<TradeRecord>> tradesBySymbol = new java.util.HashMap<>();
        for (TradeRecord trade : trades) {
            tradesBySymbol.computeIfAbsent(trade.symbol, k -> new java.util.ArrayList<>()).add(trade);
        }

        int winningTrades = 0;
        int totalCompletedTrades = 0;

        for (java.util.Map.Entry<String, java.util.List<TradeRecord>> entry : tradesBySymbol.entrySet()) {
            java.util.List<TradeRecord> symbolTrades = entry.getValue();
            int buyQty = 0;
            double buyCost = 0;
            int sellQty = 0;
            double sellRevenue = 0;

            for (TradeRecord trade : symbolTrades) {
                if ("BUY".equals(trade.action)) {
                    buyQty += trade.quantity;
                    buyCost += trade.quantity * trade.price;
                } else if ("SELL".equals(trade.action)) {
                    sellQty += trade.quantity;
                    sellRevenue += trade.quantity * trade.price;
                }
            }

            // 只有完成一轮买卖才算完整交易
            if (buyQty > 0 && sellQty > 0) {
                totalCompletedTrades++;
                double profit = sellRevenue - (buyCost * sellQty / buyQty);
                if (profit > 0) {
                    winningTrades++;
                }
            }
        }

        if (totalCompletedTrades == 0) {
            return 0;
        }

        return ((double) winningTrades / totalCompletedTrades) * 100;
    }

    /**
     * 计算盈亏比
     *
     * @return 平均盈利与平均亏损的比值
     */
    public double calculateProfitLossRatio() {
        if (trades == null || trades.isEmpty()) {
            return 0;
        }

        // 按股票分组计算盈亏
        java.util.Map<String, java.util.List<TradeRecord>> tradesBySymbol = new java.util.HashMap<>();
        for (TradeRecord trade : trades) {
            tradesBySymbol.computeIfAbsent(trade.symbol, k -> new java.util.ArrayList<>()).add(trade);
        }

        double totalProfit = 0;
        double totalLoss = 0;
        int profitCount = 0;
        int lossCount = 0;

        for (java.util.Map.Entry<String, java.util.List<TradeRecord>> entry : tradesBySymbol.entrySet()) {
            java.util.List<TradeRecord> symbolTrades = entry.getValue();
            int buyQty = 0;
            double buyCost = 0;
            int sellQty = 0;
            double sellRevenue = 0;

            for (TradeRecord trade : symbolTrades) {
                if ("BUY".equals(trade.action)) {
                    buyQty += trade.quantity;
                    buyCost += trade.quantity * trade.price;
                } else if ("SELL".equals(trade.action)) {
                    sellQty += trade.quantity;
                    sellRevenue += trade.quantity * trade.price;
                }
            }

            if (buyQty > 0 && sellQty > 0) {
                double profit = sellRevenue - (buyCost * sellQty / buyQty);
                if (profit > 0) {
                    totalProfit += profit;
                    profitCount++;
                } else if (profit < 0) {
                    totalLoss += Math.abs(profit);
                    lossCount++;
                }
            }
        }

        if (lossCount == 0 || totalLoss == 0) {
            return profitCount > 0 ? Double.POSITIVE_INFINITY : 0;
        }

        double avgProfit = totalProfit / profitCount;
        double avgLoss = totalLoss / lossCount;

        if (avgLoss == 0) {
            return avgProfit > 0 ? Double.POSITIVE_INFINITY : 0;
        }

        return avgProfit / avgLoss;
    }

    /**
     * 计算每日收益率
     */
    private List<Double> calculateDailyReturns() {
        java.util.List<Double> returns = new java.util.ArrayList<>();

        if (historicalData == null || historicalData.size() < 2) {
            return returns;
        }

        for (int i = 1; i < historicalData.size(); i++) {
            double prevClose = historicalData.get(i - 1).closePrice;
            double currClose = historicalData.get(i).closePrice;

            if (prevClose > 0) {
                double dailyReturn = (currClose - prevClose) / prevClose;
                returns.add(dailyReturn);
            }
        }

        return returns;
    }

    /**
     * 获取总交易次数
     */
    public int getTotalTrades() {
        return trades != null ? trades.size() : 0;
    }

    /**
     * 获取盈利交易次数
     */
    public int getWinningTrades() {
        if (trades == null || trades.isEmpty()) {
            return 0;
        }

        java.util.Map<String, java.util.List<TradeRecord>> tradesBySymbol = new java.util.HashMap<>();
        for (TradeRecord trade : trades) {
            tradesBySymbol.computeIfAbsent(trade.symbol, k -> new java.util.ArrayList<>()).add(trade);
        }

        int winningCount = 0;
        for (java.util.Map.Entry<String, java.util.List<TradeRecord>> entry : tradesBySymbol.entrySet()) {
            java.util.List<TradeRecord> symbolTrades = entry.getValue();
            int buyQty = 0;
            double buyCost = 0;
            int sellQty = 0;
            double sellRevenue = 0;

            for (TradeRecord trade : symbolTrades) {
                if ("BUY".equals(trade.action)) {
                    buyQty += trade.quantity;
                    buyCost += trade.quantity * trade.price;
                } else if ("SELL".equals(trade.action)) {
                    sellQty += trade.quantity;
                    sellRevenue += trade.quantity * trade.price;
                }
            }

            if (buyQty > 0 && sellQty > 0) {
                double profit = sellRevenue - (buyCost * sellQty / buyQty);
                if (profit > 0) {
                    winningCount++;
                }
            }
        }

        return winningCount;
    }

    /**
     * 生成回测报告摘要
     */
    public String generateSummary() {
        return String.format(
            "=== 回测性能摘要 ===\n" +
            "总收益率: %.2f%%\n" +
            "年化收益率: %.2f%%\n" +
            "夏普比率: %.2f\n" +
            "最大回撤: %.2f%%\n" +
            "胜率: %.2f%%\n" +
            "盈亏比: %.2f\n" +
            "总交易次数: %d\n" +
            "盈利交易次数: %d",
            calculateTotalReturn(),
            calculateAnnualizedReturn(),
            calculateSharpeRatio(),
            calculateMaxDrawdown(),
            calculateWinRate(),
            calculateProfitLossRatio(),
            getTotalTrades(),
            getWinningTrades()
        );
    }
}
