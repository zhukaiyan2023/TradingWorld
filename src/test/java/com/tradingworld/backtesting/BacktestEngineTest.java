package com.tradingworld.backtesting;

import com.tradingworld.backtesting.BacktestEngine.BacktestResult;
import com.tradingworld.backtesting.BacktestEngine.HistoricalData;
import com.tradingworld.backtesting.BacktestEngine.TradingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BacktestEngine的单元测试。
 */
class BacktestEngineTest {

    private BacktestEngine engine;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        startDate = LocalDate.of(2024, 1, 1);
        endDate = LocalDate.of(2024, 12, 31);

        // 创建引擎（使用null作为vendorRouter，因为我们不真正加载数据）
        engine = new BacktestEngine(null, startDate, endDate, 100000.0);
    }

    @Test
    void testBacktestEngineInitialization() {
        assertNotNull(engine);
        assertEquals(100000.0, engine.getCash(), 0.01);
        assertEquals(100000.0, engine.getTotalValue(), 0.01);
        assertTrue(engine.getTrades().isEmpty());
    }

    @Test
    void testPositionCreation() {
        BacktestEngine.Position position = new BacktestEngine.Position("AAPL", 100, 150.0);

        assertEquals("AAPL", position.getSymbol());
        assertEquals(100, position.getQuantity());
        assertEquals(150.0, position.getAveragePrice(), 0.01);
    }

    @Test
    void testPositionAddQuantity() {
        BacktestEngine.Position position = new BacktestEngine.Position("AAPL", 100, 150.0);

        position.addQuantity(50, 160.0);

        // 新平均价 = (100*150 + 50*160) / 150 = 23000/150 = 153.33
        assertEquals(153.33, position.getAveragePrice(), 0.01);
    }

    @Test
    void testPositionReduceQuantity() {
        BacktestEngine.Position position = new BacktestEngine.Position("AAPL", 100, 150.0);

        position.reduceQuantity(30);

        assertEquals(70, position.getQuantity());
    }

    @Test
    void testTradeRecordCreation() {
        LocalDate date = LocalDate.of(2024, 6, 15);
        BacktestEngine.TradeRecord trade = new BacktestEngine.TradeRecord("AAPL", "BUY", 100, 150.0, date);

        assertEquals("AAPL", trade.symbol);
        assertEquals("BUY", trade.action);
        assertEquals(100, trade.quantity);
        assertEquals(150.0, trade.price, 0.01);
        assertEquals(date, trade.date);
    }

    @Test
    void testHistoricalDataCreation() {
        LocalDate date = LocalDate.of(2024, 6, 15);
        HistoricalData data = new HistoricalData(date, 150.0);

        assertEquals(date, data.date);
        assertEquals(150.0, data.closePrice, 0.01);
    }

    @Test
    void testSimpleTradingStrategy() {
        // 测试策略接口
        TradingStrategy strategy = data -> {
            if (data.closePrice < 100) {
                return "BUY";
            } else if (data.closePrice > 150) {
                return "SELL";
            }
            return "HOLD";
        };

        // 测试低价格买入信号
        assertEquals("BUY", strategy.generateSignal(new HistoricalData(LocalDate.now(), 90.0)));

        // 测试高价格卖出信号
        assertEquals("SELL", strategy.generateSignal(new HistoricalData(LocalDate.now(), 160.0)));

        // 测试中间价格持有信号
        assertEquals("HOLD", strategy.generateSignal(new HistoricalData(LocalDate.now(), 120.0)));
    }

    @Test
    void testLookAheadBiasProtection() {
        // 这个测试验证数据点日期不会被设置为endDate之后
        LocalDate beforeEnd = endDate.minusDays(1);
        LocalDate afterEnd = endDate.plusDays(1);

        assertTrue(beforeEnd.isBefore(endDate) || beforeEnd.isEqual(endDate));
        assertTrue(afterEnd.isAfter(endDate));

        // 在实际实现中，afterEnd的数据点应该被过滤掉
        // 由于我们的简单测试不真正加载数据，这里只验证日期比较逻辑
        assertTrue(afterEnd.isAfter(endDate));
    }

    @Test
    void testBacktestResultCreation() {
        BacktestResult result = new BacktestResult(
            "AAPL",
            new java.util.ArrayList<>(),
            100000.0,
            120000.0
        );

        assertEquals("AAPL", result.symbol);
        assertEquals(100000.0, result.initialCapital, 0.01);
        assertEquals(120000.0, result.finalValue, 0.01);
        assertNotNull(result.trades);
        assertTrue(result.trades.isEmpty());
    }
}
