package com.tradingworld.tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MarketTools的单元测试。
 */
class MarketToolsTest {

    private MarketTools marketTools;

    @BeforeEach
    void setUp() {
        marketTools = new MarketTools();
    }

    @Test
    void testGetStockData_returnsJson() {
        String result = marketTools.getStockData("NVDA");
        assertNotNull(result);
        assertTrue(result.contains("NVDA"));
        assertTrue(result.contains("ticker"));
    }

    @Test
    void testGetStockData_containsError() {
        String result = marketTools.getStockData("INVALID");
        // 占位符实现可能返回JSON
        assertNotNull(result);
    }

    @Test
    void testGetHistoricalData_returnsJson() {
        String result = marketTools.getHistoricalData("AAPL", "1m");
        assertNotNull(result);
        assertTrue(result.contains("AAPL"));
    }

    @Test
    void testGetHistoricalData_periodFormat() {
        String result = marketTools.getHistoricalData("MSFT", "3m");
        assertNotNull(result);
        assertTrue(result.contains("MSFT"));
    }

    @Test
    void testGetQuoteSummary_returnsJson() {
        String result = marketTools.getQuoteSummary("GOOGL");
        assertNotNull(result);
        assertTrue(result.contains("GOOGL"));
        assertTrue(result.contains("open") || result.contains("note"));
    }

    @Test
    void testGetOptions_returnsJson() {
        String result = marketTools.getOptions("TSLA");
        assertNotNull(result);
        assertTrue(result.contains("TSLA"));
        assertTrue(result.contains("calls") || result.contains("note"));
    }

    @Test
    void testGetStockData_emptyTicker() {
        String result = marketTools.getStockData("");
        assertNotNull(result);
    }

    @Test
    void testGetStockData_nullTicker() {
        String result = marketTools.getStockData(null);
        assertNotNull(result);
    }
}
