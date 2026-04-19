package com.tradingworld.dataflows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AShareVendor的单元测试。
 */
class AShareVendorTest {

    private AShareVendor aShareVendor;

    @BeforeEach
    void setUp() {
        aShareVendor = new AShareVendor();
        ReflectionTestUtils.setField(aShareVendor, "enabled", true);
        ReflectionTestUtils.setField(aShareVendor, "baseUrl", "https://push2.eastmoney.com");
        ReflectionTestUtils.setField(aShareVendor, "timeout", 30);
    }

    @Test
    void testGetName() {
        assertEquals("East Money A-Share", aShareVendor.getName());
    }

    @Test
    void testIsAvailable_whenEnabled() {
        assertTrue(aShareVendor.isAvailable());
    }

    @Test
    void testIsAvailable_whenDisabled() {
        ReflectionTestUtils.setField(aShareVendor, "enabled", false);
        assertFalse(aShareVendor.isAvailable());
    }

    // 股票代码规范化测试

    @Test
    void testNormalizeTicker_ShanghaiCode() {
        assertEquals("600000.SS", aShareVendor.normalizeTicker("600000"));
    }

    @Test
    void testNormalizeTicker_ShanghaiSTARCode() {
        assertEquals("688000.SS", aShareVendor.normalizeTicker("688000"));
    }

    @Test
    void testNormalizeTicker_ShenzhenCode() {
        assertEquals("000001.SZ", aShareVendor.normalizeTicker("000001"));
    }

    @Test
    void testNormalizeTicker_ShenzhenChiNextCode() {
        assertEquals("300001.SZ", aShareVendor.normalizeTicker("300001"));
    }

    @Test
    void testNormalizeTicker_AlreadyNormalizedShanghai() {
        assertEquals("600000.SS", aShareVendor.normalizeTicker("600000.SS"));
    }

    @Test
    void testNormalizeTicker_AlreadyNormalizedShenzhen() {
        assertEquals("000001.SZ", aShareVendor.normalizeTicker("000001.SZ"));
    }

    @Test
    void testNormalizeTicker_WithWhitespace() {
        assertEquals("600000.SS", aShareVendor.normalizeTicker("  600000  "));
    }

    @Test
    void testNormalizeTicker_WithPrefix() {
        assertEquals("600000.SS", aShareVendor.normalizeTicker("sh600000"));
    }

    @Test
    void testNormalizeTicker_NullInput() {
        assertNull(aShareVendor.normalizeTicker(null));
    }

    @Test
    void testNormalizeTicker_EmptyInput() {
        assertEquals("", aShareVendor.normalizeTicker(""));
    }

    // 数据获取测试 - 由于没有真实API将返回空

    @Test
    void testGetStockQuote_InvalidTicker() {
        Optional<DataVendor.StockQuote> result = aShareVendor.getStockQuote("INVALID");
        // 无效股票代码应返回空
        assertTrue(result.isEmpty() || result.get().price() <= 0);
    }

    @Test
    void testGetHistorical_InvalidTicker() {
        var result = aShareVendor.getHistorical("INVALID", "1m");
        // 无效股票代码应返回空
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetBalanceSheet_InvalidTicker() {
        Optional<DataVendor.BalanceSheet> result = aShareVendor.getBalanceSheet("INVALID");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetIncomeStatement_InvalidTicker() {
        Optional<DataVendor.IncomeStatement> result = aShareVendor.getIncomeStatement("INVALID");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetCashflow_NotAvailable() {
        // 现金流通过东方财富简单API不可用
        Optional<DataVendor.Cashflow> result = aShareVendor.getCashflow("600000.SS");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetInsiderTransactions_NotAvailable() {
        // 内部交易通过东方财富API不可用
        var result = aShareVendor.getInsiderTransactions("600000.SS");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetNews_InvalidTicker() {
        var result = aShareVendor.getNews("INVALID");
        // 可能为空或包含文章
        assertNotNull(result);
    }
}
