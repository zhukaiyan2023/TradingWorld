package com.tradingworld.backtesting;

import com.tradingworld.backtesting.BacktestEngine.BacktestResult;
import com.tradingworld.backtesting.BacktestEngine.HistoricalData;
import com.tradingworld.backtesting.BacktestEngine.TradeRecord;
import com.tradingworld.backtesting.metrics.BacktestMetrics;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 回测报告生成器。
 * 生成格式化的回测结果报告。
 */
public class BacktestReportGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;

    /**
     * 生成完整的回测报告
     *
     * @param result 回测结果
     * @return 格式化的报告字符串
     */
    public String generateReport(BacktestResult result) {
        StringBuilder report = new StringBuilder();

        // 报告头部
        report.append("╔══════════════════════════════════════════════════════════════╗\n");
        report.append("║                     回测报告                                    ║\n");
        report.append("╚══════════════════════════════════════════════════════════════╝\n\n");

        // 基本信息
        report.append("【基本信息】\n");
        report.append(String.format("  股票代码: %s\n", result.symbol));
        report.append(String.format("  初始资金: $%.2f\n", result.initialCapital));
        report.append(String.format("  最终价值: $%.2f\n", result.finalValue));
        report.append(String.format("  总收益: $%.2f\n", result.finalValue - result.initialCapital));
        report.append(String.format("  总交易次数: %d\n", result.trades.size()));
        report.append("\n");

        // 性能指标
        if (result.metrics != null) {
            report.append("【性能指标】\n");
            report.append(String.format("  总收益率: %.2f%%\n", result.metrics.calculateTotalReturn()));
            report.append(String.format("  年化收益率: %.2f%%\n", result.metrics.calculateAnnualizedReturn()));
            report.append(String.format("  夏普比率: %.2f\n", result.metrics.calculateSharpeRatio()));
            report.append(String.format("  最大回撤: %.2f%%\n", result.metrics.calculateMaxDrawdown()));
            report.append(String.format("  胜率: %.2f%%\n", result.metrics.calculateWinRate()));
            report.append(String.format("  盈亏比: %.2f\n", result.metrics.calculateProfitLossRatio()));
            report.append("\n");
        }

        // 交易记录
        report.append("【交易记录】\n");
        if (result.trades.isEmpty()) {
            report.append("  (无交易)\n");
        } else {
            for (TradeRecord trade : result.trades) {
                report.append(String.format("  [%s] %s %d股 @ $%.2f (日期: %s)\n",
                    trade.action,
                    trade.symbol,
                    trade.quantity,
                    trade.price,
                    trade.date.format(DATE_FORMATTER)));
            }
        }
        report.append("\n");

        // 报告尾部
        report.append("═══════════════════════════════════════════════════════════════\n");
        report.append(String.format("  报告生成时间: %s\n", java.time.LocalDateTime.now()));

        return report.toString();
    }

    /**
     * 生成简化的回测摘要
     *
     * @param result 回测结果
     * @return 简化的报告字符串
     */
    public String generateSummary(BacktestResult result) {
        if (result.metrics == null) {
            return String.format("[%s] 初始: $%.2f -> 最终: $%.2f (交易次数: %d)",
                result.symbol,
                result.initialCapital,
                result.finalValue,
                result.trades.size());
        }

        return String.format(
            "[%s] 收益率: %.2f%% | 夏普比率: %.2f | 最大回撤: %.2f%% | 胜率: %.2f%% | 交易: %d次",
            result.symbol,
            result.metrics.calculateTotalReturn(),
            result.metrics.calculateSharpeRatio(),
            result.metrics.calculateMaxDrawdown(),
            result.metrics.calculateWinRate(),
            result.trades.size()
        );
    }

    /**
     * 生成CSV格式的交易记录
     *
     * @param trades 交易记录列表
     * @return CSV格式的字符串
     */
    public String generateTradesCsv(List<TradeRecord> trades) {
        StringBuilder csv = new StringBuilder();

        // CSV头部
        csv.append("Date,Symbol,Action,Quantity,Price,Total Value\n");

        // 数据行
        for (TradeRecord trade : trades) {
            double totalValue = trade.quantity * trade.price;
            csv.append(String.format("%s,%s,%s,%d,%.2f,%.2f\n",
                trade.date.format(DATE_FORMATTER),
                trade.symbol,
                trade.action,
                trade.quantity,
                trade.price,
                totalValue));
        }

        return csv.toString();
    }

    /**
     * 生成HTML格式的回测报告
     *
     * @param result 回测结果
     * @return HTML格式的字符串
     */
    public String generateHtmlReport(BacktestResult result) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n<head>\n");
        html.append("<meta charset=\"UTF-8\">\n");
        html.append("<title>回测报告 - ").append(result.symbol).append("</title>\n");
        html.append("<style>\n");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; }\n");
        html.append("h1 { color: #333; }\n");
        html.append("table { border-collapse: collapse; width: 100%; margin: 20px 0; }\n");
        html.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n");
        html.append("th { background-color: #4CAF50; color: white; }\n");
        html.append(".metric { display: inline-block; margin: 10px 20px; padding: 15px; background: #f5f5f5; border-radius: 5px; }\n");
        html.append(".metric-value { font-size: 24px; font-weight: bold; color: #4CAF50; }\n");
        html.append(".metric-label { font-size: 12px; color: #666; }\n");
        html.append("</style>\n");
        html.append("</head>\n<body>\n");

        html.append("<h1>回测报告 - ").append(result.symbol).append("</h1>\n");

        // 基本信息
        html.append("<h2>基本信息</h2>\n");
        html.append("<div class='metric'>\n");
        html.append("<div class='metric-value'>").append(String.format("$%.2f", result.finalValue)).append("</div>\n");
        html.append("<div class='metric-label'>最终价值</div>\n");
        html.append("</div>\n");

        html.append("<div class='metric'>\n");
        html.append("<div class='metric-value'>").append(String.format("$%.2f", result.finalValue - result.initialCapital)).append("</div>\n");
        html.append("<div class='metric-label'>总收益</div>\n");
        html.append("</div>\n");

        html.append("<div class='metric'>\n");
        html.append("<div class='metric-value'>").append(result.trades.size()).append("</div>\n");
        html.append("<div class='metric-label'>交易次数</div>\n");
        html.append("</div>\n");

        // 性能指标
        if (result.metrics != null) {
            html.append("<h2>性能指标</h2>\n");
            html.append("<table>\n");
            html.append("<tr><th>指标</th><th>数值</th></tr>\n");
            html.append(String.format("<tr><td>总收益率</td><td>%.2f%%</td></tr>\n", result.metrics.calculateTotalReturn()));
            html.append(String.format("<tr><td>年化收益率</td><td>%.2f%%</td></tr>\n", result.metrics.calculateAnnualizedReturn()));
            html.append(String.format("<tr><td>夏普比率</td><td>%.2f</td></tr>\n", result.metrics.calculateSharpeRatio()));
            html.append(String.format("<tr><td>最大回撤</td><td>%.2f%%</td></tr>\n", result.metrics.calculateMaxDrawdown()));
            html.append(String.format("<tr><td>胜率</td><td>%.2f%%</td></tr>\n", result.metrics.calculateWinRate()));
            html.append(String.format("<tr><td>盈亏比</td><td>%.2f</td></tr>\n", result.metrics.calculateProfitLossRatio()));
            html.append("</table>\n");
        }

        // 交易记录
        html.append("<h2>交易记录</h2>\n");
        html.append("<table>\n");
        html.append("<tr><th>日期</th><th>股票</th><th>操作</th><th>数量</th><th>价格</th><th>总额</th></tr>\n");
        for (TradeRecord trade : result.trades) {
            double totalValue = trade.quantity * trade.price;
            html.append(String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%d</td><td>$%.2f</td><td>$%.2f</td></tr>\n",
                trade.date.format(DATE_FORMATTER),
                trade.symbol,
                trade.action,
                trade.quantity,
                trade.price,
                totalValue));
        }
        html.append("</table>\n");

        html.append("</body>\n</html>");

        return html.toString();
    }
}
