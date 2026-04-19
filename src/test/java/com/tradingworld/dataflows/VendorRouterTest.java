package com.tradingworld.dataflows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * VendorRouter的单元测试。
 */
class VendorRouterTest {

    private VendorRouter vendorRouter;
    private MockDataVendor primaryVendor;
    private MockDataVendor fallbackVendor;

    @BeforeEach
    void setUp() {
        primaryVendor = new MockDataVendor("Primary", true);
        fallbackVendor = new MockDataVendor("Fallback", true);
        vendorRouter = new VendorRouter(List.of(primaryVendor, fallbackVendor));
    }

    @Test
    void testGetStockQuote_primaryVendorSuccess() {
        primaryVendor.setStockQuote(new DataVendor.StockQuote(
                "AAPL", 150.0, 2.5, 1.7, 1000000, LocalDateTime.now()));

        Optional<DataVendor.StockQuote> result = vendorRouter.getStockQuote("AAPL");

        assertTrue(result.isPresent());
        assertEquals("AAPL", result.get().symbol());
        assertEquals(150.0, result.get().price());
        assertEquals("Primary", primaryVendor.getLastCall());
    }

    @Test
    void testGetStockQuote_fallbackOnPrimaryFailure() {
        primaryVendor.setAvailable(false);
        primaryVendor.setStockQuote(new DataVendor.StockQuote(
                "AAPL", 150.0, 2.5, 1.7, 1000000, LocalDateTime.now()));

        Optional<DataVendor.StockQuote> result = vendorRouter.getStockQuote("AAPL");

        assertTrue(result.isPresent());
        assertEquals("Fallback", fallbackVendor.getLastCall());
    }

    @Test
    void testGetStockQuote_allVendorsFail() {
        primaryVendor.setAvailable(false);
        fallbackVendor.setAvailable(false);

        Optional<DataVendor.StockQuote> result = vendorRouter.getStockQuote("AAPL");

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetHistorical_primaryVendorSuccess() {
        primaryVendor.setHistorical(List.of(
                new DataVendor.Candle(LocalDateTime.now(), 100, 101, 99, 100, 1000000)));

        Optional<List<DataVendor.Candle>> result = vendorRouter.getHistorical("AAPL", "1m");

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
    }

    @Test
    void testGetNews_fallbackOnPrimaryFailure() {
        primaryVendor.setAvailable(false);
        fallbackVendor.setNews(List.of(
                new DataVendor.NewsArticle("Test", "Content", "http://url", "Source", LocalDateTime.now())));

        Optional<List<DataVendor.NewsArticle>> result = vendorRouter.getNews("AAPL");

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
    }

    // 用于测试的模拟供应商
    private static class MockDataVendor implements DataVendor {
        private final String name;
        private boolean available;
        private DataVendor.StockQuote stockQuote;
        private List<DataVendor.Candle> historical;
        private List<DataVendor.NewsArticle> news;
        private String lastCall = "";

        public MockDataVendor(String name, boolean available) {
            this.name = name;
            this.available = available;
        }

        public void setAvailable(boolean available) { this.available = available; }
        public void setStockQuote(DataVendor.StockQuote quote) { this.stockQuote = quote; }
        public void setHistorical(List<DataVendor.Candle> hist) { this.historical = hist; }
        public void setNews(List<DataVendor.NewsArticle> news) { this.news = news; }
        public String getLastCall() { return lastCall; }

        @Override
        public String getName() { return name; }

        @Override
        public boolean isAvailable() { return available; }

        @Override
        public Optional<StockQuote> getStockQuote(String symbol) {
            lastCall = name;
            return Optional.ofNullable(stockQuote);
        }

        @Override
        public Optional<List<Candle>> getHistorical(String symbol, String period) {
            lastCall = name;
            return Optional.ofNullable(historical);
        }

        @Override
        public Optional<BalanceSheet> getBalanceSheet(String symbol) {
            return Optional.empty();
        }

        @Override
        public Optional<IncomeStatement> getIncomeStatement(String symbol) {
            return Optional.empty();
        }

        @Override
        public Optional<Cashflow> getCashflow(String symbol) {
            return Optional.empty();
        }

        @Override
        public Optional<List<InsiderTransaction>> getInsiderTransactions(String symbol) {
            return Optional.empty();
        }

        @Override
        public Optional<List<NewsArticle>> getNews(String symbol) {
            lastCall = name;
            return Optional.ofNullable(news);
        }
    }
}
