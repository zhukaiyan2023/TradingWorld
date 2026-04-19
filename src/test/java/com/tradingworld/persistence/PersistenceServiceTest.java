package com.tradingworld.persistence;

import com.tradingworld.persistence.entity.AnalysisReportEntity;
import com.tradingworld.persistence.entity.BacktestResultEntity;
import com.tradingworld.persistence.entity.TradeRecordEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Persistence实体类单元测试。
 */
class PersistenceServiceTest {

    @Test
    void testTradeRecordEntityCreation() {
        TradeRecordEntity entity = new TradeRecordEntity();
        entity.setId(1L);
        entity.setSymbol("AAPL");
        entity.setAction("BUY");
        entity.setQuantity(100);
        entity.setPrice(150.0);
        entity.setTotalAmount(15000.0);
        entity.setTradeDate(LocalDate.of(2024, 6, 15));
        entity.setCreatedAt(LocalDateTime.now());

        assertEquals(1L, entity.getId());
        assertEquals("AAPL", entity.getSymbol());
        assertEquals("BUY", entity.getAction());
        assertEquals(100, entity.getQuantity());
        assertEquals(150.0, entity.getPrice(), 0.01);
        assertEquals(15000.0, entity.getTotalAmount(), 0.01);
        assertEquals(LocalDate.of(2024, 6, 15), entity.getTradeDate());
    }

    @Test
    void testAnalysisReportEntityCreation() {
        AnalysisReportEntity entity = new AnalysisReportEntity();
        entity.setId(1L);
        entity.setSymbol("AAPL");
        entity.setReportType("MARKET");
        entity.setContent("Market analysis content");
        entity.setAnalysisDate(LocalDate.of(2024, 6, 15));
        entity.setCreatedAt(LocalDateTime.now());
        entity.setSuccess(true);

        assertEquals(1L, entity.getId());
        assertEquals("AAPL", entity.getSymbol());
        assertEquals("MARKET", entity.getReportType());
        assertEquals("Market analysis content", entity.getContent());
        assertTrue(entity.getSuccess());
    }

    @Test
    void testBacktestResultEntityCreation() {
        BacktestResultEntity entity = new BacktestResultEntity();
        entity.setId(1L);
        entity.setSymbol("AAPL");
        entity.setStartDate(LocalDate.of(2024, 1, 1));
        entity.setEndDate(LocalDate.of(2024, 12, 31));
        entity.setInitialCapital(100000.0);
        entity.setFinalValue(120000.0);
        entity.setTotalReturn(20.0);
        entity.setAnnualizedReturn(20.0);
        entity.setSharpeRatio(1.5);
        entity.setMaxDrawdown(10.0);
        entity.setWinRate(60.0);
        entity.setProfitLossRatio(2.0);
        entity.setTotalTrades(10);
        entity.setWinningTrades(6);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setStatus("SUCCESS");

        assertEquals(1L, entity.getId());
        assertEquals("AAPL", entity.getSymbol());
        assertEquals(100000.0, entity.getInitialCapital(), 0.01);
        assertEquals(120000.0, entity.getFinalValue(), 0.01);
        assertEquals(20.0, entity.getTotalReturn(), 0.01);
        assertEquals(1.5, entity.getSharpeRatio(), 0.01);
        assertEquals(10, entity.getTotalTrades());
        assertEquals(6, entity.getWinningTrades());
        assertEquals("SUCCESS", entity.getStatus());
    }

    @Test
    void testBacktestMetricsCalculation() {
        // 验证回测指标计算的正确性
        BacktestResultEntity entity = new BacktestResultEntity();
        entity.setInitialCapital(100000.0);
        entity.setFinalValue(110000.0);

        // 计算总收益率：(110000 - 100000) / 100000 * 100 = 10%
        double expectedReturn = ((entity.getFinalValue() - entity.getInitialCapital()) / entity.getInitialCapital()) * 100;
        assertEquals(10.0, expectedReturn, 0.01);
    }

    @Test
    void testWinRateCalculation() {
        // 验证胜率计算
        int totalTrades = 10;
        int winningTrades = 6;
        double expectedWinRate = (double) winningTrades / totalTrades * 100;
        assertEquals(60.0, expectedWinRate, 0.01);
    }

    @Test
    void testProfitLossRatioCalculation() {
        // 验证盈亏比计算
        double avgProfit = 2000.0;
        double avgLoss = 1000.0;
        double expectedRatio = avgProfit / avgLoss;
        assertEquals(2.0, expectedRatio, 0.01);
    }

    @Test
    void testEntityNullHandling() {
        TradeRecordEntity entity = new TradeRecordEntity();

        assertNull(entity.getId());
        assertNull(entity.getSymbol());
        assertNull(entity.getAction());
        assertNull(entity.getTradeDate());
    }

    @Test
    void testBacktestResultStatus() {
        BacktestResultEntity successResult = new BacktestResultEntity();
        successResult.setStatus("SUCCESS");
        assertEquals("SUCCESS", successResult.getStatus());

        BacktestResultEntity failedResult = new BacktestResultEntity();
        failedResult.setStatus("FAILED");
        failedResult.setErrorMessage("Insufficient data");
        assertEquals("FAILED", failedResult.getStatus());
        assertEquals("Insufficient data", failedResult.getErrorMessage());
    }
}
