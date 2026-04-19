package com.tradingworld.backtesting;

import com.tradingworld.backtesting.metrics.BacktestMetrics;
import com.tradingworld.dataflows.DataVendor;
import com.tradingworld.dataflows.VendorRouter;
import com.tradingworld.dto.TradeDecision;
import com.tradingworld.graph.state.AgentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 回测引擎核心类，负责在历史数据上执行交易策略并模拟交易过程。
 * 确保回测过程中严格防止前瞻偏差（look-ahead bias）。
 */
public class BacktestEngine {

    private static final Logger log = LoggerFactory.getLogger(BacktestEngine.class);

    private final VendorRouter vendorRouter;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final double initialCapital;
    private final TradingCostConfig costConfig;

    private List<TradeRecord> trades;
    private Map<String, Position> positions;
    private double cash;
    private double totalValue;

    /**
     * 创建回测引擎实例
     *
     * @param vendorRouter 数据源路由器，用于获取历史数据
     * @param startDate 回测开始日期
     * @param endDate 回测结束日期
     * @param initialCapital 初始资金
     */
    public BacktestEngine(VendorRouter vendorRouter, LocalDate startDate, LocalDate endDate, double initialCapital) {
        this(vendorRouter, startDate, endDate, initialCapital, TradingCostConfig.defaults());
    }

    /**
     * 创建回测引擎实例（带交易成本配置）
     *
     * @param vendorRouter 数据源路由器
     * @param startDate 回测开始日期
     * @param endDate 回测结束日期
     * @param initialCapital 初始资金
     * @param costConfig 交易成本配置
     */
    public BacktestEngine(VendorRouter vendorRouter, LocalDate startDate, LocalDate endDate,
                         double initialCapital, TradingCostConfig costConfig) {
        this.vendorRouter = vendorRouter;
        this.startDate = startDate;
        this.endDate = endDate;
        this.initialCapital = initialCapital;
        this.costConfig = costConfig;
        this.trades = new ArrayList<>();
        this.positions = new HashMap<>();
        this.cash = initialCapital;
        this.totalValue = initialCapital;
    }

    /**
     * 执行回测
     *
     * @param symbol 股票代码
     * @param strategy 交易策略函数接口
     * @return 回测结果报告
     */
    public BacktestResult run(String symbol, TradingStrategy strategy) {
        log.info("Starting backtest for {} from {} to {}", symbol, startDate, endDate);
        trades.clear();
        positions.clear();
        cash = initialCapital;

        List<HistoricalData> historicalData = loadHistoricalData(symbol);

        // 按时间顺序执行回测
        for (HistoricalData dataPoint : historicalData) {
            // 检查是否在回测日期范围内
            if (dataPoint.date.isBefore(startDate) || dataPoint.date.isAfter(endDate)) {
                continue;
            }

            // 执行策略获取交易信号
            String signal = strategy.generateSignal(dataPoint);

            // 处理交易信号
            processSignal(symbol, dataPoint, signal);

            // 更新总价值
            updateTotalValue(symbol, dataPoint.closePrice);
        }

        // 生成回测报告
        return generateReport(symbol, historicalData);
    }

    /**
     * 从AgentState执行回测（与TradingAgentsGraph集成）
     *
     * @param state 包含交易决策的AgentState
     * @return 回测结果报告
     */
    public BacktestResult runFromAgentState(AgentState state) {
        String symbol = state.getCompanyOfInterest();
        log.info("Running backtest from AgentState for {}", symbol);

        // 解析AgentState中的交易决策
        String decision = state.getTraderInvestmentPlan();
        if (decision == null || decision.isEmpty()) {
            log.warn("No trading decision found in AgentState");
            return new BacktestResult(symbol, new ArrayList<>(), initialCapital, 0);
        }

        // 创建简单的策略来解析决策
        TradingStrategy strategy = new AgentStateStrategy(decision);

        return run(symbol, strategy);
    }

    /**
     * 加载历史数据（使用DataVendor，自动过滤以防止前瞻偏差）
     */
    private List<HistoricalData> loadHistoricalData(String symbol) {
        List<HistoricalData> dataList = new ArrayList<>();

        try {
            // 使用VendorRouter获取数据，curr_date参数确保只返回历史数据
            String data = vendorRouter.getHistoricalPrices(symbol, startDate.toString(), endDate.toString());

            // 解析数据并过滤（确保防止前瞻偏差）
            dataList = parseAndFilterData(data, symbol);
        } catch (Exception e) {
            log.error("Error loading historical data for {}: {}", symbol, e.getMessage());
        }

        return dataList;
    }

    /**
     * 解析并过滤数据，确保不包含未来信息
     */
    private List<HistoricalData> parseAndFilterData(String data, String symbol) {
        List<HistoricalData> result = new ArrayList<>();

        if (data == null || data.isEmpty()) {
            return result;
        }

        // 简单的CSV解析（假设格式：Date,Open,High,Low,Close,Volume）
        String[] lines = data.split("\n");
        for (int i = 1; i < lines.length; i++) { // 跳过标题行
            String[] parts = lines[i].split(",");
            if (parts.length >= 5) {
                try {
                    LocalDate date = LocalDate.parse(parts[0].trim(), DateTimeFormatter.ISO_DATE);

                    // 关键：只添加end_date之前的数据，防止前瞻偏差
                    if (date.isAfter(endDate)) {
                        continue;
                    }

                    double closePrice = Double.parseDouble(parts[4].trim());
                    result.add(new HistoricalData(date, closePrice));
                } catch (Exception e) {
                    // 跳过无效行
                }
            }
        }

        return result;
    }

    /**
     * 处理交易信号
     */
    private void processSignal(String symbol, HistoricalData dataPoint, String signal) {
        if (signal == null || signal.isEmpty()) {
            return;
        }

        Position position = positions.get(symbol);
        if (position == null) {
            position = new Position(symbol, 0, 0);
            positions.put(symbol, position);
        }

        switch (signal.toUpperCase()) {
            case "BUY":
                executeBuy(symbol, dataPoint.closePrice, dataPoint.date);
                break;
            case "SELL":
                executeSell(symbol, dataPoint.closePrice, dataPoint.date);
                break;
            case "HOLD":
                // 不执行任何操作
                break;
        }
    }

    /**
     * 执行买入（含交易成本）
     */
    private void executeBuy(String symbol, double price, LocalDate date) {
        // 应用滑点：买入价格更高
        double actualBuyPrice = costConfig.getBuyPrice(price);

        if (cash <= 0) {
            log.debug("Insufficient cash to buy {} at {}", symbol, price);
            return;
        }

        int quantity = (int) (cash / actualBuyPrice);
        if (quantity > 0) {
            double grossCost = quantity * actualBuyPrice;
            double commission = costConfig.calculateCommission(grossCost);
            double totalCost = grossCost + commission;

            if (totalCost > cash) {
                // 调整数量以支付手续费
                totalCost = cash;
                grossCost = totalCost / (1 + costConfig.getCommissionRate());
                quantity = (int) (grossCost / actualBuyPrice);
                if (quantity <= 0) {
                    log.debug("Insufficient cash to buy {} after commission at {}", symbol, price);
                    return;
                }
            }

            cash -= totalCost;

            Position position = positions.get(symbol);
            if (position == null) {
                position = new Position(symbol, quantity, actualBuyPrice);
                positions.put(symbol, position);
            } else {
                position.addQuantity(quantity, actualBuyPrice);
            }

            trades.add(new TradeRecord(symbol, "BUY", quantity, actualBuyPrice, date, commission));
            log.debug("Bought {} shares of {} at {} (commission: {}) on {}", quantity, symbol, actualBuyPrice, commission, date);
        }
    }

    /**
     * 执行卖出（含交易成本）
     */
    private void executeSell(String symbol, double price, LocalDate date) {
        Position position = positions.get(symbol);
        if (position == null || position.getQuantity() == 0) {
            log.debug("No position to sell for {}", symbol);
            return;
        }

        // 应用滑点：卖出价格更低
        double actualSellPrice = costConfig.getSellPrice(price);

        int quantity = position.getQuantity();
        double grossRevenue = quantity * actualSellPrice;
        double commission = costConfig.calculateCommission(grossRevenue);
        double netRevenue = grossRevenue - commission;

        cash += netRevenue;
        position.reduceQuantity(quantity);
        trades.add(new TradeRecord(symbol, "SELL", quantity, actualSellPrice, date, commission));
        log.debug("Sold {} shares of {} at {} (commission: {}) on {}", quantity, symbol, actualSellPrice, commission, date);
    }

    /**
     * 更新投资组合总价值
     */
    private void updateTotalValue(String symbol, double currentPrice) {
        totalValue = cash;

        for (Map.Entry<String, Position> entry : positions.entrySet()) {
            Position pos = entry.getValue();
            totalValue += pos.getQuantity() * currentPrice;
        }
    }

    /**
     * 生成回测报告
     */
    private BacktestResult generateReport(String symbol, List<HistoricalData> historicalData) {
        BacktestMetrics metrics = new BacktestMetrics(trades, initialCapital, totalValue, historicalData);

        return new BacktestResult(
            symbol,
            new ArrayList<>(trades),
            initialCapital,
            totalValue,
            metrics
        );
    }

    /**
     * 获取所有交易记录
     */
    public List<TradeRecord> getTrades() {
        return new ArrayList<>(trades);
    }

    /**
     * 获取最终总价值
     */
    public double getTotalValue() {
        return totalValue;
    }

    /**
     * 获取当前现金
     */
    public double getCash() {
        return cash;
    }

    // ==================== 内部类 ====================

    /**
     * 历史数据点
     */
    public static class HistoricalData {
        public final LocalDate date;
        public final double closePrice;

        public HistoricalData(LocalDate date, double closePrice) {
            this.date = date;
            this.closePrice = closePrice;
        }
    }

    /**
     * 持仓状态
     */
    public static class Position {
        private final String symbol;
        private int quantity;
        private double averagePrice;

        public Position(String symbol, int quantity, double averagePrice) {
            this.symbol = symbol;
            this.quantity = quantity;
            this.averagePrice = averagePrice;
        }

        public void addQuantity(int qty, double price) {
            double totalCost = (quantity * averagePrice) + (qty * price);
            quantity += qty;
            averagePrice = quantity > 0 ? totalCost / quantity : 0;
        }

        public void reduceQuantity(int qty) {
            quantity -= qty;
            if (quantity < 0) quantity = 0;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getAveragePrice() {
            return averagePrice;
        }

        public String getSymbol() {
            return symbol;
        }
    }

    /**
     * 交易记录
     */
    public static class TradeRecord {
        public final String symbol;
        public final String action; // BUY or SELL
        public final int quantity;
        public final double price;    // 执行价格（含滑点）
        public final LocalDate date;
        public final double commission; // 手续费

        public TradeRecord(String symbol, String action, int quantity, double price, LocalDate date) {
            this(symbol, action, quantity, price, date, 0.0);
        }

        public TradeRecord(String symbol, String action, int quantity, double price, LocalDate date, double commission) {
            this.symbol = symbol;
            this.action = action;
            this.quantity = quantity;
            this.price = price;
            this.date = date;
            this.commission = commission;
        }

        /**
         * 获取总交易金额（含手续费）
         */
        public double getTotalValue() {
            return quantity * price + commission;
        }
    }

    /**
     * 回测结果
     */
    public static class BacktestResult {
        public final String symbol;
        public final List<TradeRecord> trades;
        public final double initialCapital;
        public final double finalValue;
        public final BacktestMetrics metrics;

        public BacktestResult(String symbol, List<TradeRecord> trades, double initialCapital, double finalValue) {
            this(symbol, trades, initialCapital, finalValue, null);
        }

        public BacktestResult(String symbol, List<TradeRecord> trades, double initialCapital, double finalValue, BacktestMetrics metrics) {
            this.symbol = symbol;
            this.trades = trades;
            this.initialCapital = initialCapital;
            this.finalValue = finalValue;
            this.metrics = metrics;
        }
    }

    /**
     * 交易策略接口
     */
    public interface TradingStrategy {
        String generateSignal(HistoricalData data);
    }

    /**
     * 基于AgentState决策的策略实现
     */
    private static class AgentStateStrategy implements TradingStrategy {
        private final String decision;

        public AgentStateStrategy(String decision) {
            this.decision = decision;
        }

        @Override
        public String generateSignal(HistoricalData data) {
            if (decision == null || decision.isEmpty()) {
                return "HOLD";
            }

            String upperDecision = decision.toUpperCase();
            if (upperDecision.contains("BUY")) {
                return "BUY";
            } else if (upperDecision.contains("SELL")) {
                return "SELL";
            }
            return "HOLD";
        }
    }
}
