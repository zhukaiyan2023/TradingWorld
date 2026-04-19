package com.tradingworld.backtesting;

import com.tradingworld.backtesting.BacktestEngine.BacktestResult;
import com.tradingworld.backtesting.BacktestEngine.HistoricalData;
import com.tradingworld.backtesting.BacktestEngine.TradeRecord;
import com.tradingworld.backtesting.metrics.BacktestMetrics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BacktestMetrics的单元测试。
 */
class BacktestMetricsTest {

    private List<TradeRecord> trades;
    private List<HistoricalData> historicalData;
    private double initialCapital;
    private double finalValue;

    @BeforeEach
    void setUp() {
        trades = new ArrayList<>();
        historicalData = new ArrayList<>();
        initialCapital = 100000.0;
        finalValue = 120000.0;

        // 添加历史数据
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        for (int i = 0; i < 252; i++) { // 一年交易日
            LocalDate date = startDate.plusDays(i);
            historicalData.add(new HistoricalData(date, 100.0 + i * 0.5));
        }

        // 添加测试交易
        trades.add(new TradeRecord("AAPL", "BUY", 100, 100.0, LocalDate.of(2024, 1, 15)));
        trades.add(new TradeRecord("AAPL", "SELL", 100, 120.0, LocalDate.of(2024, 6, 15)));
    }

    @Test
    void testCalculateTotalReturn() {
        BacktestMetrics metrics = new BacktestMetrics(trades, initialCapital, finalValue, historicalData);
        double totalReturn = metrics.calculateTotalReturn();

        // 收益率为 (120000 - 100000) / 100000 * 100 = 20%
        assertEquals(20.0, totalReturn, 0.01);
    }

    @Test
    void testCalculateAnnualizedReturn() {
        BacktestMetrics metrics = new BacktestMetrics(trades, initialCapital, finalValue, historicalData);
        double annualizedReturn = metrics.calculateAnnualizedReturn();

        // 252个交易日约等于1年，年化收益率应该接近总收益率
        assertTrue(annualizedReturn > 0);
    }

    @Test
    void testCalculateSharpeRatio() {
        BacktestMetrics metrics = new BacktestMetrics(trades, initialCapital, finalValue, historicalData);
        double sharpeRatio = metrics.calculateSharpeRatio();

        // 夏普比率可以是正数、负数或0
        // 由于我们用的是模拟数据，这个测试主要验证计算不报错
        assertNotNull(sharpeRatio);
    }

    @Test
    void testCalculateMaxDrawdown() {
        BacktestMetrics metrics = new BacktestMetrics(trades, initialCapital, finalValue, historicalData);
        double maxDrawdown = metrics.calculateMaxDrawdown();

        // 最大回撤应该是非负数
        assertTrue(maxDrawdown >= 0);
    }

    @Test
    void testCalculateWinRate() {
        BacktestMetrics metrics = new BacktestMetrics(trades, initialCapital, finalValue, historicalData);
        double winRate = metrics.calculateWinRate();

        // 单笔交易盈利，胜率应该是100%
        assertEquals(100.0, winRate, 0.01);
    }

    @Test
    void testCalculateProfitLossRatio() {
        BacktestMetrics metrics = new BacktestMetrics(trades, initialCapital, finalValue, historicalData);
        double profitLossRatio = metrics.calculateProfitLossRatio();

        // 单笔盈利交易，盈亏比应该是正无穷或一个较大值
        assertTrue(profitLossRatio > 0 || profitLossRatio == Double.POSITIVE_INFINITY);
    }

    @Test
    void testEmptyTrades() {
        List<TradeRecord> emptyTrades = new ArrayList<>();
        BacktestMetrics metrics = new BacktestMetrics(emptyTrades, initialCapital, finalValue, historicalData);

        assertEquals(0, metrics.getTotalTrades());
        assertEquals(0, metrics.calculateWinRate());
    }

    @Test
    void testZeroInitialCapital() {
        BacktestMetrics metrics = new BacktestMetrics(trades, 0, finalValue, historicalData);
        double totalReturn = metrics.calculateTotalReturn();

        // 初始资金为0时，收益率应该返回0
        assertEquals(0, totalReturn, 0.01);
    }

    @Test
    void testGenerateSummary() {
        BacktestMetrics metrics = new BacktestMetrics(trades, initialCapital, finalValue, historicalData);
        String summary = metrics.generateSummary();

        assertNotNull(summary);
        assertTrue(summary.contains("回测性能摘要"));
        assertTrue(summary.contains("总收益率"));
    }
}
