package com.tradingworld.backtesting.metrics;

import com.tradingworld.backtesting.BacktestEngine;
import com.tradingworld.backtesting.BacktestEngine.HistoricalData;
import com.tradingworld.backtesting.BacktestEngine.TradeRecord;

import java.util.List;

/**
 * 回测性能指标计算器。
 * 计算并返回策略表现的各项指标，包括收益率、夏普比率、最大回撤、索提诺比率、VaR等。
 */
public class BacktestMetrics {

    private final List<TradeRecord> trades;
    private final double initialCapital;
    private final double finalValue;
    private final List<HistoricalData> historicalData;
    private final List<Double> benchmarkReturns;  // 基准收益率（可选，用于计算 Beta/Alpha）

    private static final double RISK_FREE_RATE = 0.0; // 假设无风险利率为0

    public BacktestMetrics(List<TradeRecord> trades, double initialCapital, double finalValue, List<HistoricalData> historicalData) {
        this(trades, initialCapital, finalValue, historicalData, null);
    }

    public BacktestMetrics(List<TradeRecord> trades, double initialCapital, double finalValue,
                          List<HistoricalData> historicalData, List<Double> benchmarkReturns) {
        this.trades = trades;
        this.initialCapital = initialCapital;
        this.finalValue = finalValue;
        this.historicalData = historicalData;
        this.benchmarkReturns = benchmarkReturns;
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
    public double calculateAnnualReturn() {
        if (historicalData == null || historicalData.isEmpty()) {
            return 0;
        }

        HistoricalData firstData = historicalData.get(0);
        HistoricalData lastData = historicalData.get(historicalData.size() - 1);
        long days = java.time.temporal.ChronoUnit.DAYS.between(firstData.date, lastData.date);

        if (days <= 0) {
            return 0;
        }

        double totalReturn = finalValue / initialCapital;
        double years = days / 365.0;
        return (Math.pow(totalReturn, 1.0 / years) - 1) * 100;
    }

    public double calculateAnnualizedReturn() {
        return calculateAnnualReturn();
    }

    /**
     * 计算年化波动率
     *
     * @return 年化波动率百分比
     */
    public double calculateAnnualVolatility() {
        List<Double> dailyReturns = calculateDailyReturns();
        if (dailyReturns.isEmpty()) {
            return 0;
        }

        double variance = dailyReturns.stream()
            .mapToDouble(d -> Math.pow(d - dailyReturns.stream().mapToDouble(x -> x).average().orElse(0), 2))
            .average()
            .orElse(0);

        double dailyVolatility = Math.sqrt(variance);
        return dailyVolatility * Math.sqrt(252) * 100;
    }

    /**
     * 计算夏普比率
     *
     * @return 夏普比率
     */
    public double calculateSharpeRatio() {
        List<Double> dailyReturns = calculateDailyReturns();
        if (dailyReturns.isEmpty()) {
            return 0;
        }

        double avgReturn = dailyReturns.stream().mapToDouble(d -> d).average().orElse(0);
        double stdDev = calculateStandardDeviation(dailyReturns, avgReturn);

        if (stdDev == 0) {
            return 0;
        }

        double dailySharpe = (avgReturn - RISK_FREE_RATE / 365) / stdDev;
        return dailySharpe * Math.sqrt(252);
    }

    /**
     * 计算索提诺比率
     * 只考虑下行波动率，更准确反映下行风险
     *
     * @return 索提诺比率
     */
    public double calculateSortinoRatio() {
        List<Double> dailyReturns = calculateDailyReturns();
        if (dailyReturns.isEmpty()) {
            return 0;
        }

        double avgReturn = dailyReturns.stream().mapToDouble(d -> d).average().orElse(0);
        double downsideDeviation = calculateDownsideDeviation(dailyReturns);

        if (downsideDeviation == 0) {
            return 0;
        }

        double dailySortino = (avgReturn - RISK_FREE_RATE / 365) / downsideDeviation;
        return dailySortino * Math.sqrt(252);
    }

    /**
     * 计算卡玛比率
     * 年化收益 / 最大回撤
     *
     * @return 卡玛比率
     */
    public double calculateCalmarRatio() {
        double maxDrawdown = calculateMaxDrawdown();
        if (maxDrawdown == 0) {
            return 0;
        }

        double annualReturn = calculateAnnualReturn();
        return annualReturn / maxDrawdown;
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
     * 计算 Value at Risk (VaR)
     * 在给定置信水平下，最大潜在损失
     *
     * @param confidenceLevel 置信水平（默认0.95）
     * @return VaR 百分比
     */
    public double calculateVaR(double confidenceLevel) {
        return calculateVaR(confidenceLevel, calculateDailyReturns());
    }

    public double calculateVaR(double confidenceLevel, List<Double> returns) {
        if (returns == null || returns.isEmpty()) {
            return 0;
        }
        double varPercentile = (1 - confidenceLevel) * 100;
        return percentile(returns, varPercentile) * 100;
    }

    public double calculateVaR() {
        return calculateVaR(0.95);
    }

    /**
     * 计算 Conditional VaR (CVaR) / Expected Shortfall
     * VaR 假设外的平均损失
     *
     * @param confidenceLevel 置信水平（默认0.95）
     * @return CVaR 百分比
     */
    public double calculateCVaR(double confidenceLevel) {
        List<Double> dailyReturns = calculateDailyReturns();
        if (dailyReturns.isEmpty()) {
            return 0;
        }

        double varPercentile = (1 - confidenceLevel) * 100;
        double var = percentile(dailyReturns, varPercentile);

        // 计算 VaR 假设外的平均损失
        return dailyReturns.stream()
            .filter(r -> r <= var)
            .mapToDouble(r -> Math.abs(r))
            .average()
            .orElse(0) * 100;
    }

    public double calculateCVaR() {
        return calculateCVaR(0.95);
    }

    /**
     * 计算 Beta 系数
     * 策略相对于基准的系统性风险
     * 需要提供基准收益率数据
     *
     * @return Beta 系数
     */
    public double calculateBeta() {
        if (benchmarkReturns == null || benchmarkReturns.isEmpty()) {
            return 1.0;  // 默认 Beta = 1（与市场同风险）
        }

        List<Double> strategyReturns = calculateDailyReturns();
        if (strategyReturns.isEmpty() || strategyReturns.size() != benchmarkReturns.size()) {
            return 1.0;
        }

        double strategyAvg = strategyReturns.stream().mapToDouble(x -> x).average().orElse(0);
        double benchmarkAvg = benchmarkReturns.stream().mapToDouble(x -> x).average().orElse(0);

        double covariance = 0;
        double benchmarkVariance = 0;

        for (int i = 0; i < strategyReturns.size(); i++) {
            double strategyDiff = strategyReturns.get(i) - strategyAvg;
            double benchmarkDiff = benchmarkReturns.get(i) - benchmarkAvg;
            covariance += strategyDiff * benchmarkDiff;
            benchmarkVariance += benchmarkDiff * benchmarkDiff;
        }

        if (benchmarkVariance == 0) {
            return 1.0;
        }

        return covariance / benchmarkVariance;
    }

    /**
     * 计算 Alpha
     * 策略相对于 CAPM 预期的超额收益
     * Alpha = 策略收益率 - (无风险利率 + Beta * (基准收益率 - 无风险利率))
     *
     * @return Alpha 百分比
     */
    public double calculateAlpha() {
        if (benchmarkReturns == null || benchmarkReturns.isEmpty()) {
            // 无法计算 Alpha，返回 0
            return 0;
        }

        double annualReturn = calculateAnnualReturn() / 100;  // 转为小数
        double annualBenchmarkReturn = benchmarkReturns.stream().mapToDouble(x -> x).average().orElse(0) * 252;
        double beta = calculateBeta();

        // Alpha 年化
        double alpha = annualReturn - (RISK_FREE_RATE + beta * (annualBenchmarkReturn - RISK_FREE_RATE));
        return alpha * 100;
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
     * 计算日收益率
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
     * 计算标准差
     */
    private double calculateStandardDeviation(List<Double> values, double mean) {
        if (values.isEmpty()) {
            return 0;
        }

        double variance = values.stream()
            .mapToDouble(d -> Math.pow(d - mean, 2))
            .average()
            .orElse(0);

        return Math.sqrt(variance);
    }

    /**
     * 计算下行偏差（只考虑负收益）
     */
    private double calculateDownsideDeviation(List<Double> returns) {
        if (returns.isEmpty()) {
            return 0;
        }

        double avgReturn = returns.stream().mapToDouble(x -> x).average().orElse(0);
        double targetReturn = 0;  // 假设目标收益为 0

        double squaredDownside = returns.stream()
            .filter(r -> r < targetReturn)
            .mapToDouble(r -> Math.pow(r - targetReturn, 2))
            .average()
            .orElse(0);

        return Math.sqrt(squaredDownside);
    }

    /**
     * 计算百分位数
     */
    private double percentile(List<Double> values, double percentile) {
        if (values.isEmpty()) {
            return 0;
        }

        java.util.List<Double> sorted = new java.util.ArrayList<>(values);
        sorted.sort(Double::compareTo);

        int index = (int) Math.ceil(percentile / 100.0 * sorted.size()) - 1;
        index = Math.max(0, Math.min(index, sorted.size() - 1));

        return sorted.get(index);
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
            "年化波动率: %.2f%%\n" +
            "夏普比率: %.2f\n" +
            "索提诺比率: %.2f\n" +
            "卡玛比率: %.2f\n" +
            "最大回撤: %.2f%%\n" +
            "VaR (95%%): %.2f%%\n" +
            "CVaR (95%%): %.2f%%\n" +
            "Beta: %.2f\n" +
            "Alpha: %.2f%%\n" +
            "胜率: %.2f%%\n" +
            "盈亏比: %.2f\n" +
            "总交易次数: %d\n" +
            "盈利交易次数: %d",
            calculateTotalReturn(),
            calculateAnnualReturn(),
            calculateAnnualVolatility(),
            calculateSharpeRatio(),
            calculateSortinoRatio(),
            calculateCalmarRatio(),
            calculateMaxDrawdown(),
            calculateVaR(),
            calculateCVaR(),
            calculateBeta(),
            calculateAlpha(),
            calculateWinRate(),
            calculateProfitLossRatio(),
            getTotalTrades(),
            getWinningTrades()
        );
    }
}
