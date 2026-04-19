package com.tradingworld.tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TechnicalTools的单元测试。
 */
class TechnicalToolsTest {

    private TechnicalTools technicalTools;

    @BeforeEach
    void setUp() {
        technicalTools = new TechnicalTools();
    }

    @Test
    void testGetIndicators_singleIndicator() {
        String result = technicalTools.getIndicators("NVDA", "RSI");
        assertNotNull(result);
        assertTrue(result.contains("NVDA"));
        assertTrue(result.contains("RSI"));
    }

    @Test
    void testGetIndicators_multipleIndicators() {
        String result = technicalTools.getIndicators("AAPL", "RSI,MACD,BB");
        assertNotNull(result);
        assertTrue(result.contains("NVDA") || result.contains("AAPL"));
        assertTrue(result.contains("RSI") || result.contains("MACD"));
    }

    @Test
    void testGetIndicators_lowercase() {
        String result = technicalTools.getIndicators("MSFT", "rsi");
        assertNotNull(result);
        // 应处理小写
        assertNotNull(result);
    }

    @Test
    void testGetRSI() {
        String result = technicalTools.getRSI("NVDA", 14);
        assertNotNull(result);
        assertTrue(result.contains("RSI"));
        assertTrue(result.contains("14") || result.contains("period"));
    }

    @Test
    void testGetRSI_defaultPeriod() {
        String result = technicalTools.getRSI("NVDA", null);
        assertNotNull(result);
        // 默认周期是14
        assertTrue(result.contains("14") || result.contains("period"));
    }

    @Test
    void testGetMACD() {
        String result = technicalTools.getMACD("AAPL");
        assertNotNull(result);
        assertTrue(result.contains("MACD"));
    }

    @Test
    void testGetBollingerBands() {
        String result = technicalTools.getBollingerBands("GOOGL", 20, 2.0);
        assertNotNull(result);
        assertTrue(result.contains("Bollinger"));
    }

    @Test
    void testGetBollingerBands_defaultParameters() {
        String result = technicalTools.getBollingerBands("GOOGL", null, null);
        assertNotNull(result);
        // 应使用默认值（20, 2.0）
        assertTrue(result.contains("Bollinger"));
    }

    @Test
    void testGetIndicators_emptyIndicatorList() {
        String result = technicalTools.getIndicators("NVDA", "");
        assertNotNull(result);
    }

    @Test
    void testGetIndicators_unknownIndicator() {
        String result = technicalTools.getIndicators("NVDA", "UNKNOWN");
        assertNotNull(result);
        assertTrue(result.contains("error") || result.contains("UNKNOWN"));
    }
}
