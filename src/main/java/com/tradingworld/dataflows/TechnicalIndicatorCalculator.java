package com.tradingworld.dataflows;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * 技术指标计算器。
 * 从Python stockstats_utils.py迁移而来。
 */
public class TechnicalIndicatorCalculator {

    private static final Logger log = LoggerFactory.getLogger(TechnicalIndicatorCalculator.class);

    /**
     * 计算简单移动平均线 (SMA)。
     */
    public static double calculateSMA(List<Double> prices, int period) {
        if (prices.size() < period) {
            return Double.NaN;
        }
        double sum = 0;
        for (int i = prices.size() - period; i < prices.size(); i++) {
            sum += prices.get(i);
        }
        return sum / period;
    }

    /**
     * 计算指数移动平均线 (EMA)。
     */
    public static double calculateEMA(List<Double> prices, int period) {
        if (prices.size() < period) {
            return Double.NaN;
        }
        double multiplier = 2.0 / (period + 1);
        double ema = calculateSMA(prices.subList(0, period), period);

        for (int i = period; i < prices.size(); i++) {
            ema = (prices.get(i) - ema) * multiplier + ema;
        }
        return ema;
    }

    /**
     * 计算MACD（移动平均收敛散度）。
     * 返回数组：[MACD线，信号线，直方图]
     */
    public static MACDResult calculateMACD(List<Double> prices, int fastPeriod, int slowPeriod, int signalPeriod) {
        if (prices.size() < slowPeriod) {
            return new MACDResult(Double.NaN, Double.NaN, Double.NaN);
        }

        List<Double> fastEMA = calculateEMAList(prices, fastPeriod);
        List<Double> slowEMA = calculateEMAList(prices, slowPeriod);

        // 对齐EMA
        int offset = slowPeriod - fastPeriod;
        double[] macdLine = new double[fastEMA.size() - offset];
        for (int i = 0; i < macdLine.length; i++) {
            macdLine[i] = fastEMA.get(i + offset) - slowEMA.get(i);
        }

        // 信号线是MACD线的EMA
        double signal = calculateEMAFromArray(macdLine, signalPeriod);
        double macd = macdLine[macdLine.length - 1];
        double histogram = macd - signal;

        return new MACDResult(macd, signal, histogram);
    }

    /**
     * 计算相对强弱指数 (RSI)。
     */
    public static double calculateRSI(List<Double> prices, int period) {
        if (prices.size() < period + 1) {
            return Double.NaN;
        }

        double gains = 0;
        double losses = 0;

        for (int i = prices.size() - period; i < prices.size(); i++) {
            double change = prices.get(i) - prices.get(i - 1);
            if (change > 0) {
                gains += change;
            } else {
                losses -= change;
            }
        }

        double avgGain = gains / period;
        double avgLoss = losses / period;

        if (avgLoss == 0) {
            return 100;
        }

        double rs = avgGain / avgLoss;
        return 100 - (100 / (1 + rs));
    }

    /**
     * 计算布林带。
     * 返回数组：[上轨，中轨（ SMA），下轨]
     */
    public static BollingerBandsResult calculateBollingerBands(List<Double> prices, int period, double stdDevMultiplier) {
        if (prices.size() < period) {
            return new BollingerBandsResult(Double.NaN, Double.NaN, Double.NaN);
        }

        List<Double> recentPrices = prices.subList(prices.size() - period, prices.size());
        double sma = calculateSMA(recentPrices, period);

        // 计算标准差
        double sumSquaredDiff = 0;
        for (double price : recentPrices) {
            double diff = price - sma;
            sumSquaredDiff += diff * diff;
        }
        double stdDev = Math.sqrt(sumSquaredDiff / period);

        double upperBand = sma + (stdDev * stdDevMultiplier);
        double lowerBand = sma - (stdDev * stdDevMultiplier);

        return new BollingerBandsResult(upperBand, sma, lowerBand);
    }

    /**
     * 计算平均真实波幅 (ATR)。
     */
    public static double calculateATR(List<DataVendor.Candle> candles, int period) {
        if (candles.size() < period + 1) {
            return Double.NaN;
        }

        double[] trueRanges = new double[candles.size() - 1];
        for (int i = 1; i < candles.size(); i++) {
            DataVendor.Candle current = candles.get(i);
            DataVendor.Candle previous = candles.get(i - 1);

            double highLow = current.high() - current.low();
            double highPrevClose = Math.abs(current.high() - previous.close());
            double lowPrevClose = Math.abs(current.low() - previous.close());

            trueRanges[i - 1] = Math.max(highLow, Math.max(highPrevClose, lowPrevClose));
        }

        return calculateEMAFromArray(trueRanges, period);
    }

    /**
     * 计算成交量加权平均价格 (VWAP)。
     */
    public static double calculateVWAP(List<DataVendor.Candle> candles) {
        double cumulativeTypicalPriceVolume = 0;
        double cumulativeVolume = 0;

        for (DataVendor.Candle candle : candles) {
            double typicalPrice = (candle.high() + candle.low() + candle.close()) / 3;
            cumulativeTypicalPriceVolume += typicalPrice * candle.volume();
            cumulativeVolume += candle.volume();
        }

        return cumulativeVolume > 0 ? cumulativeTypicalPriceVolume / cumulativeVolume : 0;
    }

    /**
     * 计算货币流量指数 (MFI)。
     */
    public static double calculateMFI(List<DataVendor.Candle> candles, int period) {
        if (candles.size() < period + 1) {
            return Double.NaN;
        }

        double positiveFlow = 0;
        double negativeFlow = 0;

        for (int i = candles.size() - period; i < candles.size(); i++) {
            double typicalPrice = (candles.get(i).high() + candles.get(i).low() + candles.get(i).close()) / 3;
            double prevTypicalPrice = (candles.get(i - 1).high() + candles.get(i - 1).low() + candles.get(i - 1).close()) / 3;
            double rawMoneyFlow = typicalPrice * candles.get(i).volume();

            if (typicalPrice > prevTypicalPrice) {
                positiveFlow += rawMoneyFlow;
            } else {
                negativeFlow += rawMoneyFlow;
            }
        }

        if (negativeFlow == 0) {
            return 100;
        }

        double moneyRatio = positiveFlow / negativeFlow;
        return 100 - (100 / (1 + moneyRatio));
    }

    // 辅助方法

    private static List<Double> calculateEMAList(List<Double> prices, int period) {
        if (prices.size() < period) {
            return List.of();
        }
        double multiplier = 2.0 / (period + 1);
        List<Double> emaList = new java.util.ArrayList<>();

        double ema = calculateSMA(prices.subList(0, period), period);
        emaList.add(ema);

        for (int i = period; i < prices.size(); i++) {
            ema = (prices.get(i) - ema) * multiplier + ema;
            emaList.add(ema);
        }
        return emaList;
    }

    private static double calculateEMAFromArray(double[] values, int period) {
        if (values.length < period) {
            return Double.NaN;
        }
        double multiplier = 2.0 / (period + 1);
        double ema = 0;
        for (int i = 0; i < period; i++) {
            ema += values[i];
        }
        ema /= period;

        for (int i = period; i < values.length; i++) {
            ema = (values[i] - ema) * multiplier + ema;
        }
        return ema;
    }

    // 结果类

    public record MACDResult(double macdLine, double signalLine, double histogram) {}
    public record BollingerBandsResult(double upperBand, double middleBand, double lowerBand) {}
}
