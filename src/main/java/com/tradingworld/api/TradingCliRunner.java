package com.tradingworld.api;

import com.tradingworld.config.AppConfig;
import com.tradingworld.graph.StateLogger;
import com.tradingworld.graph.TradingAgentsGraph;
import com.tradingworld.memory.ReflectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * 批量交易分析的CLI运行器。
 * 允许从命令行运行多个股票代码。
 */
@Component
public class TradingCliRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(TradingCliRunner.class);

    private final TradingAgentsGraph tradingGraph;
    private final ReflectionManager reflectionManager;
    private final StateLogger stateLogger;

    public TradingCliRunner(
            TradingAgentsGraph tradingGraph,
            ReflectionManager reflectionManager,
            StateLogger stateLogger) {
        this.tradingGraph = tradingGraph;
        this.reflectionManager = reflectionManager;
        this.stateLogger = stateLogger;
    }

    @Override
    public void run(String... args) {
        if (args.length == 0) {
            log.info("No CLI arguments provided. Use --tickers=TICKER1,TICKER2 or --interactive");
            log.info("Example: java -jar tradingworld.jar --tickers=NVDA,AAPL,MSFT --date=2026-01-15");
            return;
        }

        // 解析命令行参数
        for (String arg : args) {
            if (arg.startsWith("--tickers=")) {
                String tickers = arg.substring("--tickers=".length());
                String date = extractDateArg(args);
                processTickers(Arrays.asList(tickers.split(",")), date);
            } else if (arg.equals("--interactive")) {
                runInteractiveMode();
            }
        }
    }

    private String extractDateArg(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("--date=")) {
                return arg.substring("--date=".length());
            }
        }
        // 默认为今天
        return java.time.LocalDate.now().toString();
    }

    private void processTickers(List<String> tickers, String date) {
        log.info("Processing {} tickers for date {}", tickers.size(), date);
        for (String ticker : tickers) {
            ticker = ticker.trim();
            if (!ticker.isEmpty()) {
                log.info("Processing ticker: {}", ticker);
                try {
                    var state = tradingGraph.propagate(ticker, date);
                    stateLogger.logState(state);
                    log.info("Completed: {} - Decision: {}",
                            ticker,
                            state.getFinalTradeDecision() != null ?
                                    state.getFinalTradeDecision().substring(0, Math.min(100, state.getFinalTradeDecision().length())) : "N/A");
                } catch (Exception e) {
                    log.error("Error processing ticker {}: {}", ticker, e.getMessage());
                }
            }
        }
        log.info("Batch processing complete");
    }

    private void runInteractiveMode() {
        Scanner scanner = new Scanner(System.in);
        log.info("Interactive TradingAgents CLI");
        log.info("Enter ticker symbols (comma-separated), 'quit' to exit:");

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
                break;
            }

            if (line.isEmpty()) {
                continue;
            }

            List<String> tickers = Arrays.stream(line.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();

            log.info("Processing: {}", tickers);
            processTickers(tickers, java.time.LocalDate.now().toString());
        }

        scanner.close();
        log.info("Exiting interactive mode");
    }
}
