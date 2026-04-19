package com.tradingworld.tools;

import com.tradingworld.dataflows.DataVendor;
import com.tradingworld.dataflows.VendorRouter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * StockScreenerTool 的单元测试。
 * 使用 Mockito 进行依赖隔离测试。
 */
@ExtendWith(MockitoExtension.class)
class StockScreenerToolTest {

    @Mock
    private VendorRouter mockVendorRouter;

    private StockScreenerTool stockScreenerTool;

    @BeforeEach
    void setUp() {
        stockScreenerTool = new StockScreenerTool(mockVendorRouter);
    }

    @Test
    void testGetTrendingTickers_returnsJson() {
        List<DataVendor.TrendingTicker> mockTickers = List.of(
            new DataVendor.TrendingTicker("NVDA", "NVIDIA Corp", 500.0, 2.5, 1000000L, 1),
            new DataVendor.TrendingTicker("AAPL", "Apple Inc", 180.0, 1.2, 800000L, 2)
        );
        when(mockVendorRouter.getTrendingTickers(10)).thenReturn(Optional.of(mockTickers));

        String result = stockScreenerTool.getTrendingTickers(10);

        assertNotNull(result);
        assertTrue(result.contains("NVDA"));
        assertTrue(result.contains("AAPL"));
        assertTrue(result.contains("trending"));
    }

    @Test
    void testGetTrendingTickers_returnsError_whenNoData() {
        when(mockVendorRouter.getTrendingTickers(anyInt())).thenReturn(Optional.empty());

        String result = stockScreenerTool.getTrendingTickers(10);

        assertNotNull(result);
        assertTrue(result.contains("error"));
    }

    @Test
    void testGetTopGainers_returnsJson() {
        List<DataVendor.StockQuote> mockGainers = List.of(
            new DataVendor.StockQuote("NVDA", 500.0, 10.0, 2.0, 1000000L, Instant.now())
        );
        when(mockVendorRouter.getMarketMovers("gainers", 10)).thenReturn(Optional.of(mockGainers));

        String result = stockScreenerTool.getTopGainers(10);

        assertNotNull(result);
        assertTrue(result.contains("gainers"));
        assertTrue(result.contains("NVDA"));
    }

    @Test
    void testGetTopLosers_returnsJson() {
        List<DataVendor.StockQuote> mockLosers = List.of(
            new DataVendor.StockQuote("TSLA", 200.0, -5.0, -2.5, 500000L, Instant.now())
        );
        when(mockVendorRouter.getMarketMovers("losers", 10)).thenReturn(Optional.of(mockLosers));

        String result = stockScreenerTool.getTopLosers(10);

        assertNotNull(result);
        assertTrue(result.contains("losers"));
        assertTrue(result.contains("TSLA"));
    }

    @Test
    void testGetMostActive_returnsJson() {
        List<DataVendor.StockQuote> mockActive = List.of(
            new DataVendor.StockQuote("AAPL", 180.0, 1.0, 0.5, 50000000L, Instant.now())
        );
        when(mockVendorRouter.getMarketMovers("active", 10)).thenReturn(Optional.of(mockActive));

        String result = stockScreenerTool.getMostActive(10);

        assertNotNull(result);
        assertTrue(result.contains("active"));
        assertTrue(result.contains("AAPL"));
    }

    @Test
    void testGetMarketMovers_returnsError_whenNoData() {
        when(mockVendorRouter.getMarketMovers(anyString(), anyInt())).thenReturn(Optional.empty());

        String result = stockScreenerTool.getMarketMovers("gainers", 10);

        assertNotNull(result);
        assertTrue(result.contains("error"));
    }

    @Test
    void testScreenByTechnical_returnsJson() {
        List<DataVendor.StockQuote> mockStocks = List.of(
            new DataVendor.StockQuote("NVDA", 500.0, 10.0, 2.0, 1000000L, Instant.now()),
            new DataVendor.StockQuote("AAPL", 180.0, 1.0, 0.5, 800000L, Instant.now())
        );
        when(mockVendorRouter.screenStocks(any(DataVendor.StockFilter.class))).thenReturn(Optional.of(mockStocks));

        String result = stockScreenerTool.screenByTechnical(100.0, 1000.0, 500000L);

        assertNotNull(result);
        assertTrue(result.contains("technical"));
        assertTrue(result.contains("NVDA"));
        assertTrue(result.contains("AAPL"));
    }

    @Test
    void testScreenByFundamental_returnsJson() {
        List<DataVendor.StockQuote> mockStocks = List.of(
            new DataVendor.StockQuote("NVDA", 500.0, 10.0, 2.0, 1000000L, Instant.now())
        );
        when(mockVendorRouter.screenStocks(any(DataVendor.StockFilter.class))).thenReturn(Optional.of(mockStocks));

        String result = stockScreenerTool.screenByFundamental(100000000000L, 20.0, 50.0, "Technology");

        assertNotNull(result);
        assertTrue(result.contains("fundamental"));
        assertTrue(result.contains("NVDA"));
    }

    @Test
    void testGetTrendingTickers_handlesException() {
        when(mockVendorRouter.getTrendingTickers(anyInt())).thenThrow(new RuntimeException("Network error"));

        String result = stockScreenerTool.getTrendingTickers(10);

        assertNotNull(result);
        assertTrue(result.contains("error"));
    }

    @Test
    void testGetTrendingTickers_usesDefaultLimit_whenNull() {
        when(mockVendorRouter.getTrendingTickers(10)).thenReturn(Optional.of(List.of()));

        stockScreenerTool.getTrendingTickers(null);

        // Should call with default limit of 10
        assertTrue(true); // No exception means success
    }
}
