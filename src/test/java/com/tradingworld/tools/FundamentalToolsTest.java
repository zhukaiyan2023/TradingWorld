package com.tradingworld.tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FundamentalTools的单元测试。
 */
class FundamentalToolsTest {

    private FundamentalTools fundamentalTools;

    @BeforeEach
    void setUp() {
        fundamentalTools = new FundamentalTools();
    }

    @Test
    void testGetFundamentals() {
        String result = fundamentalTools.getFundamentals("NVDA");
        assertNotNull(result);
        assertTrue(result.contains("NVDA"));
    }

    @Test
    void testGetBalanceSheet() {
        String result = fundamentalTools.getBalanceSheet("AAPL");
        assertNotNull(result);
        assertTrue(result.contains("AAPL") || result.contains("balanceSheet"));
    }

    @Test
    void testGetCashflow() {
        String result = fundamentalTools.getCashflow("MSFT");
        assertNotNull(result);
        assertTrue(result.contains("MSFT") || result.contains("cashflow"));
    }

    @Test
    void testGetIncomeStatement() {
        String result = fundamentalTools.getIncomeStatement("GOOGL");
        assertNotNull(result);
        assertTrue(result.contains("GOOGL") || result.contains("incomeStatement"));
    }

    @Test
    void testGetKeyStatistics() {
        String result = fundamentalTools.getKeyStatistics("TSLA");
        assertNotNull(result);
        assertTrue(result.contains("TSLA") || result.contains("statistics"));
    }

    @Test
    void testGetFundamentals_invalidTicker() {
        String result = fundamentalTools.getFundamentals("INVALID_TICKER");
        assertNotNull(result);
    }

    @Test
    void testGetBalanceSheet_emptyTicker() {
        String result = fundamentalTools.getBalanceSheet("");
        assertNotNull(result);
    }
}
