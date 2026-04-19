package com.tradingworld.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingworld.dataflows.TechnicalIndicatorCalculator;
import com.tradingworld.dataflows.VendorRouter;
import com.tradingworld.dataflows.AlphaVantageVendor;
import com.tradingworld.dataflows.YFinanceVendor;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.P;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 用于计算技术指标的工具。
 * 使用TechnicalIndicatorCalculator进行计算。
 */
@Component
public class TechnicalTools {

    private static final Logger log = LoggerFactory.getLogger(TechnicalTools.class);

    private final VendorRouter vendorRouter;
    private final ObjectMapper objectMapper;

    public TechnicalTools() {
        YFinanceVendor yFinanceVendor = new YFinanceVendor();
        AlphaVantageVendor alphaVantageVendor = new AlphaVantageVendor();
        this.vendorRouter = new VendorRouter(List.of(yFinanceVendor, alphaVantageVendor));
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 获取股票的技术指标。
     */
    @Tool("Get technical indicators (RSI, MACD, Bollinger Bands, SMA, EMA) for a stock")
    public String getIndicators(
            @P("Stock ticker symbol (e.g., NVDA)") String ticker,
            @P("Comma-separated list of indicators: RSI, MACD, BB, SMA, EMA") String indicators) {
        log.debug("Fetching technical indicators for {}: {}", ticker, indicators);

        List<String> indicatorList = List.of(indicators.toUpperCase().split(","));
        StringBuilder result = new StringBuilder();
        result.append("{\"ticker\": \"").append(ticker).append("\", \"indicators\": {");

        for (int i = 0; i < indicatorList.size(); i++) {
            String indicator = indicatorList.get(i).trim();
            if (i > 0) result.append(", ");
            result.append("\"").append(indicator).append("\": ");
            result.append(getIndicatorValue(ticker, indicator));
        }

        result.append("}}");
        return result.toString();
    }

    private String getIndicatorValue(String ticker, String indicator) {
        var candles = vendorRouter.getHistorical(ticker.toUpperCase(), "1m");
        if (candles.isEmpty()) {
            return "{\"error\": \"No historical data available\"}";
        }

        List<Double> closes = candles.get().stream()
                .map(c -> c.close())
                .toList();

        return switch (indicator.trim()) {
            case "RSI" -> {
                double rsi = TechnicalIndicatorCalculator.calculateRSI(closes, 14);
                yield String.format("{\"value\": %.2f, \"signal\": \"%s\"}",
                        rsi, rsi > 70 ? "overbought" : rsi < 30 ? "oversold" : "neutral");
            }
            case "MACD" -> {
                var macd = TechnicalIndicatorCalculator.calculateMACD(closes, 12, 26, 9);
                yield String.format("{\"macd\": %.4f, \"signal\": %.4f, \"histogram\": %.4f}",
                        macd.macdLine(), macd.signalLine(), macd.histogram());
            }
            case "BB", "BOLLINGER" -> {
                var bb = TechnicalIndicatorCalculator.calculateBollingerBands(closes, 20, 2.0);
                yield String.format("{\"upper\": %.2f, \"middle\": %.2f, \"lower\": %.2f}",
                        bb.upperBand(), bb.middleBand(), bb.lowerBand());
            }
            case "SMA" -> {
                double sma = TechnicalIndicatorCalculator.calculateSMA(closes, 20);
                yield String.format("{\"value\": %.2f}", sma);
            }
            case "EMA" -> {
                double ema = TechnicalIndicatorCalculator.calculateEMA(closes, 20);
                yield String.format("{\"value\": %.2f}", ema);
            }
            case "ATR" -> {
                double atr = TechnicalIndicatorCalculator.calculateATR(candles.get(), 14);
                yield String.format("{\"value\": %.2f}", atr);
            }
            case "VWAP" -> {
                double vwap = TechnicalIndicatorCalculator.calculateVWAP(candles.get());
                yield String.format("{\"value\": %.2f}", vwap);
            }
            case "MFI" -> {
                double mfi = TechnicalIndicatorCalculator.calculateMFI(candles.get(), 14);
                yield String.format("{\"value\": %.2f, \"signal\": \"%s\"}",
                        mfi, mfi > 80 ? "overbought" : mfi < 20 ? "oversold" : "neutral");
            }
            default -> "{\"error\": \"Unknown indicator: " + indicator + "\"}";
        };
    }

    /**
     * 获取股票的RSI（相对强弱指数）。
     */
    @Tool("Get RSI (Relative Strength Index) for a stock")
    public String getRSI(
            @P("Stock ticker symbol") String ticker,
            @P("Period for calculation (default 14)") Integer period) {
        int p = (period != null) ? period : 14;
        log.debug("Fetching RSI for ticker: {} with period: {}", ticker, p);

        var candles = vendorRouter.getHistorical(ticker.toUpperCase(), "1m");
        if (candles.isEmpty()) {
            return "{\"error\": \"No historical data available for ticker: " + ticker + "\"}";
        }

        List<Double> closes = candles.get().stream().map(c -> c.close()).toList();
        double rsi = TechnicalIndicatorCalculator.calculateRSI(closes, p);

        String signal = rsi > 70 ? "overbought" : rsi < 30 ? "oversold" : "neutral";

        return String.format("""
            {
                "ticker": "%s",
                "indicator": "RSI",
                "period": %d,
                "value": %.2f,
                "signal": "%s"
            }
            """, ticker.toUpperCase(), p, rsi, signal);
    }

    /**
     * 获取股票的MACD（移动平均收敛散度）。
     */
    @Tool("Get MACD (Moving Average Convergence Divergence) for a stock")
    public String getMACD(@P("Stock ticker symbol") String ticker) {
        log.debug("Fetching MACD for ticker: {}", ticker);

        var candles = vendorRouter.getHistorical(ticker.toUpperCase(), "1m");
        if (candles.isEmpty()) {
            return "{\"error\": \"No historical data available for ticker: " + ticker + "\"}";
        }

        List<Double> closes = candles.get().stream().map(c -> c.close()).toList();
        var macd = TechnicalIndicatorCalculator.calculateMACD(closes, 12, 26, 9);

        return String.format("""
            {
                "ticker": "%s",
                "indicator": "MACD",
                "macd": %.4f,
                "signal": %.4f,
                "histogram": %.4f
            }
            """, ticker.toUpperCase(), macd.macdLine(), macd.signalLine(), macd.histogram());
    }

    /**
     * 获取股票的布林带。
     */
    @Tool("Get Bollinger Bands for a stock")
    public String getBollingerBands(
            @P("Stock ticker symbol") String ticker,
            @P("Period for SMA (default 20)") Integer period,
            @P("Standard deviations (default 2)") Double standardDeviations) {
        int p = (period != null) ? period : 20;
        double sd = (standardDeviations != null) ? standardDeviations : 2.0;

        log.debug("Fetching Bollinger Bands for {} period {} sd {}", ticker, p, sd);

        var candles = vendorRouter.getHistorical(ticker.toUpperCase(), "1m");
        if (candles.isEmpty()) {
            return "{\"error\": \"No historical data available for ticker: " + ticker + "\"}";
        }

        List<Double> closes = candles.get().stream().map(c -> c.close()).toList();
        var bb = TechnicalIndicatorCalculator.calculateBollingerBands(closes, p, sd);

        return String.format("""
            {
                "ticker": "%s",
                "indicator": "BollingerBands",
                "period": %d,
                "upper": %.2f,
                "middle": %.2f,
                "lower": %.2f
            }
            """, ticker.toUpperCase(), p, bb.upperBand(), bb.middleBand(), bb.lowerBand());
    }
}
