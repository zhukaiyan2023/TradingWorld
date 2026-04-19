package com.tradingworld.dataflows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TechnicalIndicatorCalculator的单元测试。
 */
class TechnicalIndicatorCalculatorTest {

    private List<Double> samplePrices;

    @BeforeEach
    void setUp() {
        // 用于测试的样本OHLCV收盘价
        samplePrices = List.of(
            100.0, 102.0, 101.5, 103.0, 104.5,
            105.0, 104.0, 106.0, 108.0, 107.5,
            109.0, 110.0, 108.5, 111.0, 112.0,
            111.5, 113.0, 114.0, 113.5, 115.0
        );
    }

    @Test
    void testCalculateSMA() {
        double sma20 = TechnicalIndicatorCalculator.calculateSMA(samplePrices, 20);
        assertEquals(108.75, sma20, 0.01);

        double sma5 = TechnicalIndicatorCalculator.calculateSMA(samplePrices, 5);
        assertEquals(114.0, sma5, 0.01);
    }

    @Test
    void testCalculateSMA_insufficientData() {
        double sma = TechnicalIndicatorCalculator.calculateSMA(samplePrices.subList(0, 5), 20);
        assertTrue(Double.isNaN(sma));
    }

    @Test
    void testCalculateEMA() {
        double ema20 = TechnicalIndicatorCalculator.calculateEMA(samplePrices, 20);
        assertFalse(Double.isNaN(ema20));
        assertTrue(ema20 > 0);
    }

    @Test
    void testCalculateRSI() {
        double rsi = TechnicalIndicatorCalculator.calculateRSI(samplePrices, 14);
        assertFalse(Double.isNaN(rsi));
        assertTrue(rsi >= 0 && rsi <= 100);
    }

    @Test
    void testCalculateRSI_insufficientData() {
        double rsi = TechnicalIndicatorCalculator.calculateRSI(samplePrices.subList(0, 5), 14);
        assertTrue(Double.isNaN(rsi));
    }

    @Test
    void testCalculateMACD() {
        var macd = TechnicalIndicatorCalculator.calculateMACD(samplePrices, 12, 26, 9);
        assertNotNull(macd);
        assertFalse(Double.isNaN(macd.macdLine()));
        assertFalse(Double.isNaN(macd.signalLine()));
        assertFalse(Double.isNaN(macd.histogram()));
    }

    @Test
    void testCalculateBollingerBands() {
        var bb = TechnicalIndicatorCalculator.calculateBollingerBands(samplePrices, 20, 2.0);
        assertNotNull(bb);
        assertTrue(bb.upperBand() > bb.middleBand());
        assertTrue(bb.middleBand() > bb.lowerBand());
    }

    @Test
    void testCalculateBollingerBands_insufficientData() {
        var bb = TechnicalIndicatorCalculator.calculateBollingerBands(
                samplePrices.subList(0, 5), 20, 2.0);
        assertTrue(Double.isNaN(bb.upperBand()));
    }

    @Test
    void testCalculateVWAP() {
        List<DataVendor.Candle> candles = List.of(
            new DataVendor.Candle(java.time.LocalDateTime.now(), 100, 101, 99, 100.5, 1000000),
            new DataVendor.Candle(java.time.LocalDateTime.now(), 101, 102, 100.5, 101.5, 1100000),
            new DataVendor.Candle(java.time.LocalDateTime.now(), 102, 103, 101.5, 102.5, 1200000)
        );

        double vwap = TechnicalIndicatorCalculator.calculateVWAP(candles);
        assertTrue(vwap > 0);
    }

    @Test
    void testCalculateMFI() {
        List<DataVendor.Candle> candles = List.of(
            new DataVendor.Candle(java.time.LocalDateTime.now(), 100, 101, 99, 100.5, 1000000),
            new DataVendor.Candle(java.time.LocalDateTime.now(), 101, 102, 100.5, 101.5, 1100000),
            new DataVendor.Candle(java.time.LocalDateTime.now(), 102, 103, 101.5, 102.5, 1200000),
            new DataVendor.Candle(java.time.LocalDateTime.now(), 103, 104, 102.5, 103.5, 1300000),
            new DataVendor.Candle(java.time.LocalDateTime.now(), 104, 105, 103.5, 104.5, 1400000),
            new DataVendor.Candle(java.time.LocalDateTime.now(), 105, 106, 104.5, 105.5, 1500000),
            new DataVendor.Candle(java.time.LocalDateTime.now(), 106, 107, 105.5, 106.5, 1600000),
            new DataVendor.Candle(java.time.LocalDateTime.now(), 107, 108, 106.5, 107.5, 1700000),
            new DataVendor.Candle(java.time.LocalDateTime.now(), 108, 109, 107.5, 108.5, 1800000),
            new DataVendor.Candle(java.time.LocalDateTime.now(), 109, 110, 108.5, 109.5, 1900000),
            new DataVendor.Candle(java.time.LocalDateTime.now(), 110, 111, 109.5, 110.5, 2000000),
            new DataVendor.Candle(java.time.LocalDateTime.now(), 111, 112, 110.5, 111.5, 2100000),
            new DataVendor.Candle(java.time.LocalDateTime.now(), 112, 113, 111.5, 112.5, 2200000),
            new DataVendor.Candle(java.time.LocalDateTime.now(), 113, 114, 112.5, 113.5, 2300000),
            new DataVendor.Candle(java.time.LocalDateTime.now(), 114, 115, 113.5, 114.5, 2400000)
        );

        double mfi = TechnicalIndicatorCalculator.calculateMFI(candles, 14);
        assertFalse(Double.isNaN(mfi));
        assertTrue(mfi >= 0 && mfi <= 100);
    }
}
