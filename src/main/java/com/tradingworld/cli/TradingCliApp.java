package com.tradingworld.cli;

import com.tradingworld.backtesting.BacktestEngine;
import com.tradingworld.backtesting.BacktestReportGenerator;
import com.tradingworld.backtesting.metrics.BacktestMetrics;
import com.tradingworld.graph.state.AgentState;
import com.tradingworld.persistence.entity.AnalysisReportEntity;
import com.tradingworld.persistence.entity.TradeRecordEntity;
import com.tradingworld.persistence.service.PersistenceService;
import com.tradingworld.TradingAgentsApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * TradingWorld命令行应用。
 * 提供交互式CLI界面，支持股票分析、交易决策和回测功能。
 */
@Component
public class TradingCliApp implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(TradingCliApp.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;

    private final TradingAgentsApplication tradingApplication;
    private final PersistenceService persistenceService;

    public TradingCliApp(
            TradingAgentsApplication tradingApplication,
            PersistenceService persistenceService) {
        this.tradingApplication = tradingApplication;
        this.persistenceService = persistenceService;
    }

    @Override
    public void run(String... args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        String command = args[0];

        switch (command.toLowerCase()) {
            case "analyze" -> handleAnalyze(args);
            case "backtest" -> handleBacktest(args);
            case "history" -> handleHistory(args);
            case "interactive" -> runInteractiveMode();
            case "help" -> printUsage();
            default -> {
                log.warn("Unknown command: {}", command);
                printUsage();
            }
        }
    }

    /**
     * 打印使用说明
     */
    private void printUsage() {
        System.out.println("""
            ╔════════════════════════════════════════════════════════════════╗
            ║            TradingWorld CLI - 使用说明                         ║
            ╚════════════════════════════════════════════════════════════════╝

            用法: java -jar tradingworld.jar <command> [options]

            命令:
              analyze    分析单只或多只股票
              backtest  运行回测
              history    查看历史记录
              interactive  启动交互模式
              help       显示此帮助信息

            示例:
              java -jar tradingworld.jar analyze --tickers=AAPL,NVDA
              java -jar tradingworld.jar analyze --tickers=AAPL --date=2026-01-15
              java -jar tradingworld.jar backtest --symbol=AAPL --start=2024-01-01 --end=2024-12-31
              java -jar tradingworld.jar interactive

            ═══════════════════════════════════════════════════════════════════
            """);
    }

    /**
     * 处理股票分析命令
     */
    private void handleAnalyze(String[] args) {
        String tickers = null;
        String date = LocalDate.now().toString();

        for (String arg : args) {
            if (arg.startsWith("--tickers=")) {
                tickers = arg.substring("--tickers=".length());
            } else if (arg.startsWith("--date=")) {
                date = arg.substring("--date=".length());
            }
        }

        if (tickers == null || tickers.isEmpty()) {
            log.error("必须指定股票代码，使用 --tickers=AAPL,NVDA");
            return;
        }

        List<String> tickerList = Arrays.stream(tickers.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        log.info("开始分析 {} 只股票，日期: {}", tickerList.size(), date);

        for (String ticker : tickerList) {
            try {
                System.out.println("\n═══════════════════════════════════════════════════");
                System.out.println("  分析股票: " + ticker);
                System.out.println("═══════════════════════════════════════════════════\n");

                AgentState state = tradingApplication.analyze(ticker, date);

                // 保存分析报告
                saveAnalysisReports(ticker, date, state);

                // 显示结果
                displayAnalysisResult(ticker, state);

            } catch (Exception e) {
                log.error("分析 {} 失败: {}", ticker, e.getMessage());
            }
        }

        System.out.println("\n分析完成！");
    }

    /**
     * 处理回测命令
     */
    private void handleBacktest(String[] args) {
        String symbol = null;
        String startDate = LocalDate.now().minusYears(1).toString();
        String endDate = LocalDate.now().toString();
        double initialCapital = 100000.0;

        for (String arg : args) {
            if (arg.startsWith("--symbol=")) {
                symbol = arg.substring("--symbol=".length());
            } else if (arg.startsWith("--start=")) {
                startDate = arg.substring("--start=".length());
            } else if (arg.startsWith("--end=")) {
                endDate = arg.substring("--end=".length());
            } else if (arg.startsWith("--capital=")) {
                initialCapital = Double.parseDouble(arg.substring("--capital=".length()));
            }
        }

        if (symbol == null || symbol.isEmpty()) {
            log.error("必须指定股票代码，使用 --symbol=AAPL");
            return;
        }

        log.info("开始回测 {} 从 {} 到 {}", symbol, startDate, endDate);
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("  回测股票: " + symbol);
        System.out.println("  回测区间: " + startDate + " 至 " + endDate);
        System.out.println("  初始资金: $" + initialCapital);
        System.out.println("═══════════════════════════════════════════════════\n");

        // TODO: 实现完整的回测逻辑
        System.out.println("回测功能开发中...");
    }

    /**
     * 处理历史记录查询命令
     */
    private void handleHistory(String[] args) {
        String type = "trades";
        String symbol = null;

        for (String arg : args) {
            if (arg.startsWith("--type=")) {
                type = arg.substring("--type=".length());
            } else if (arg.startsWith("--symbol=")) {
                symbol = arg.substring("--symbol=".length());
            }
        }

        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("  历史记录查询 - 类型: " + type);
        if (symbol != null) {
            System.out.println("  股票代码: " + symbol);
        }
        System.out.println("═══════════════════════════════════════════════════\n");

        switch (type.toLowerCase()) {
            case "trades" -> {
                if (symbol != null) {
                    List<TradeRecordEntity> trades = persistenceService.getTradeRecordsBySymbol(symbol);
                    displayTradeHistory(trades);
                } else {
                    log.info("请指定股票代码: --symbol=AAPL");
                }
            }
            case "reports" -> {
                if (symbol != null) {
                    List<AnalysisReportEntity> reports = persistenceService.getAnalysisReportsBySymbol(symbol);
                    displayReportHistory(reports);
                } else {
                    log.info("请指定股票代码: --symbol=AAPL");
                }
            }
            default -> log.warn("未知的记录类型: {}", type);
        }
    }

    /**
     * 运行交互式模式
     */
    private void runInteractiveMode() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("""
            ╔════════════════════════════════════════════════════════════════╗
            ║          TradingWorld 交互式 CLI                             ║
            ║          输入 'help' 查看命令，输入 'quit' 退出                  ║
            ╚════════════════════════════════════════════════════════════════╝
            """);

        while (true) {
            System.out.print("tradingworld> ");
            String line = scanner.nextLine().trim();

            if (line.isEmpty()) {
                continue;
            }

            if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
                System.out.println("再见！");
                break;
            }

            if (line.equalsIgnoreCase("help")) {
                printInteractiveHelp();
                continue;
            }

            // 解析命令
            String[] parts = line.split("\\s+");
            String[] args = new String[parts.length - 1];
            System.arraycopy(parts, 1, args, 0, parts.length - 1);

            try {
                run(parts[0], args);
            } catch (Exception e) {
                log.error("执行命令失败: {}", e.getMessage());
            }
        }

        scanner.close();
    }

    /**
     * 打印交互模式帮助
     */
    private void printInteractiveHelp() {
        System.out.println("""
            可用命令:
              analyze <tickers> [--date=YYYY-MM-DD]  分析股票
              backtest <symbol> [--start=date] [--end=date]  运行回测
              history <type> [--symbol=symbol]  查看历史
              help     显示此帮助
              quit     退出程序
            """);
    }

    /**
     * 显示分析结果
     */
    private void displayAnalysisResult(String ticker, AgentState state) {
        System.out.println("【分析结果】");
        System.out.println("股票: " + ticker);
        System.out.println("日期: " + state.getTradeDate());

        if (state.getMarketReport() != null) {
            System.out.println("\n市场分析:\n" + truncate(state.getMarketReport(), 500));
        }

        if (state.getFinalTradeDecision() != null) {
            System.out.println("\n最终交易决策:\n" + state.getFinalTradeDecision());
        } else {
            System.out.println("\n最终交易决策: 分析中...");
        }
    }

    /**
     * 显示交易历史
     */
    private void displayTradeHistory(List<TradeRecordEntity> trades) {
        if (trades.isEmpty()) {
            System.out.println("暂无交易记录");
            return;
        }

        System.out.println("┌────────────┬────────┬────────┬────────┬────────┬──────────────────┐");
        System.out.println("│   日期     │ 股票   │ 操作   │ 数量   │ 价格   │     金额         │");
        System.out.println("├────────────┼────────┼────────┼────────┼────────┼──────────────────┤");

        for (TradeRecordEntity trade : trades) {
            System.out.printf("│ %-10s │ %-6s │ %-6s │ %6d │ %6.2f │ %16.2f │%n",
                    trade.getTradeDate(),
                    trade.getSymbol(),
                    trade.getAction(),
                    trade.getQuantity(),
                    trade.getPrice(),
                    trade.getTotalAmount());
        }

        System.out.println("└────────────┴────────┴────────┴────────┴────────┴──────────────────┘");
    }

    /**
     * 显示报告历史
     */
    private void displayReportHistory(List<AnalysisReportEntity> reports) {
        if (reports.isEmpty()) {
            System.out.println("暂无分析报告");
            return;
        }

        System.out.println("┌────────────┬────────┬────────────────┬────────┬────────┐");
        System.out.println("│   日期     │ 股票   │     类型       │ 状态   │ 耗时   │");
        System.out.println("├────────────┼────────┼────────────────┼────────┼────────┤");

        for (AnalysisReportEntity report : reports) {
            System.out.printf("│ %-10s │ %-6s │ %-14s │ %-6s │ %6dms │%n",
                    report.getAnalysisDate(),
                    report.getSymbol(),
                    report.getReportType(),
                    report.getSuccess() ? "成功" : "失败",
                    report.getExecutionTimeMs());
        }

        System.out.println("└────────────┴────────┴────────────────┴────────┴────────┘");
    }

    /**
     * 保存分析报告到数据库
     */
    private void saveAnalysisReports(String symbol, String date, AgentState state) {
        if (state.getMarketReport() != null) {
            AnalysisReportEntity report = new AnalysisReportEntity();
            report.setSymbol(symbol);
            report.setReportType("MARKET");
            report.setContent(state.getMarketReport());
            report.setAnalysisDate(LocalDate.parse(date));
            report.setSuccess(true);
            persistenceService.saveAnalysisReport(report);
        }
    }

    /**
     * 截断字符串
     */
    private String truncate(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength) + "...";
    }
}
