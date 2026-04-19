package com.tradingworld.graph;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tradingworld.config.AppConfig;
import com.tradingworld.graph.state.AgentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理状态日志记录用于调试和分析。
 * 将完整状态记录到结果目录中的JSON文件。
 */
@Component
public class StateLogger {

    private static final Logger log = LoggerFactory.getLogger(StateLogger.class);

    private final AppConfig config;
    private final ObjectMapper objectMapper;

    public StateLogger(AppConfig config) {
        this.config = config;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * 将最终状态记录到JSON文件。
     */
    public void logState(AgentState state) {
        try {
            Map<String, Object> stateMap = buildStateMap(state);
            String json = objectMapper.writeValueAsString(stateMap);

            String ticker = state.getCompanyOfInterest();
            String tradeDate = state.getTradeDate();
            String fileName = String.format("full_states_log_%s.json", tradeDate);

            Path directory = Paths.get(config.getPaths().getResultsDir(), ticker, "TradingAgentsStrategy_logs");
            Files.createDirectories(directory);

            Path filePath = directory.resolve(fileName);
            Files.writeString(filePath, json);

            log.info("State logged to: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to log state: {}", e.getMessage(), e);
        }
    }

    private Map<String, Object> buildStateMap(AgentState state) {
        Map<String, Object> map = new HashMap<>();
        map.put("company_of_interest", state.getCompanyOfInterest());
        map.put("trade_date", state.getTradeDate());
        map.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // 分析师报告
        map.put("market_report", state.getMarketReport());
        map.put("sentiment_report", state.getSentimentReport());
        map.put("news_report", state.getNewsReport());
        map.put("fundamentals_report", state.getFundamentalsReport());

        // 投资辩论状态
        if (state.getInvestmentDebateState() != null) {
            Map<String, Object> investDebate = new HashMap<>();
            investDebate.put("bull_history", state.getInvestmentDebateState().getBullHistory());
            investDebate.put("bear_history", state.getInvestmentDebateState().getBearHistory());
            investDebate.put("history", state.getInvestmentDebateState().getHistory());
            investDebate.put("current_response", state.getInvestmentDebateState().getCurrentResponse());
            investDebate.put("judge_decision", state.getInvestmentDebateState().getJudgeDecision());
            map.put("investment_debate_state", investDebate);
        }

        // 交易员决策
        map.put("trader_investment_plan", state.getTraderInvestmentPlan());
        map.put("investment_plan", state.getInvestmentPlan());

        // 风险辩论状态
        if (state.getRiskDebateState() != null) {
            Map<String, Object> riskDebate = new HashMap<>();
            riskDebate.put("aggressive_history", state.getRiskDebateState().getAggressiveHistory());
            riskDebate.put("conservative_history", state.getRiskDebateState().getConservativeHistory());
            riskDebate.put("neutral_history", state.getRiskDebateState().getNeutralHistory());
            riskDebate.put("history", state.getRiskDebateState().getHistory());
            riskDebate.put("judge_decision", state.getRiskDebateState().getJudgeDecision());
            map.put("risk_debate_state", riskDebate);
        }

        // 最终决策
        map.put("final_trade_decision", state.getFinalTradeDecision());

        return map;
    }

    /**
     * 加载先前记录的状态。
     */
    public AgentState loadState(String ticker, String tradeDate) {
        try {
            String fileName = String.format("full_states_log_%s.json", tradeDate);
            Path filePath = Paths.get(config.getPaths().getResultsDir(), ticker, "TradingAgentsStrategy_logs", fileName);

            if (!Files.exists(filePath)) {
                log.warn("No saved state found for {} on {}", ticker, tradeDate);
                return null;
            }

            String json = Files.readString(filePath);
            // 在实际实现中，会反序列化回AgentState
            log.info("Loaded state from: {}", filePath);
            return null; // 占位符 - 完整的反序列化需要自定义逻辑
        } catch (IOException e) {
            log.error("Failed to load state: {}", e.getMessage(), e);
            return null;
        }
    }
}
