package com.tradingworld.config;

import com.tradingworld.memory.FinancialSituationMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BM25内存组件的配置。
 * 为不同的智能体管理FinancialSituationMemory实例。
 */
@Configuration
public class MemoryConfig {

    @Bean
    public FinancialSituationMemory bullMemory(AppConfig appConfig) {
        return new FinancialSituationMemory(
            "bull_memory",
            appConfig.getMemory().getBm25K1(),
            appConfig.getMemory().getBm25B()
        );
    }

    @Bean
    public FinancialSituationMemory bearMemory(AppConfig appConfig) {
        return new FinancialSituationMemory(
            "bear_memory",
            appConfig.getMemory().getBm25K1(),
            appConfig.getMemory().getBm25B()
        );
    }

    @Bean
    public FinancialSituationMemory traderMemory(AppConfig appConfig) {
        return new FinancialSituationMemory(
            "trader_memory",
            appConfig.getMemory().getBm25K1(),
            appConfig.getMemory().getBm25B()
        );
    }

    @Bean
    public FinancialSituationMemory investJudgeMemory(AppConfig appConfig) {
        return new FinancialSituationMemory(
            "invest_judge_memory",
            appConfig.getMemory().getBm25K1(),
            appConfig.getMemory().getBm25B()
        );
    }

    @Bean
    public FinancialSituationMemory portfolioManagerMemory(AppConfig appConfig) {
        return new FinancialSituationMemory(
            "portfolio_manager_memory",
            appConfig.getMemory().getBm25K1(),
            appConfig.getMemory().getBm25B()
        );
    }

    @Bean
    public Map<String, FinancialSituationMemory> allMemories(
            FinancialSituationMemory bullMemory,
            FinancialSituationMemory bearMemory,
            FinancialSituationMemory traderMemory,
            FinancialSituationMemory investJudgeMemory,
            FinancialSituationMemory portfolioManagerMemory) {
        return new ConcurrentHashMap<>(Map.of(
            "bull", bullMemory,
            "bear", bearMemory,
            "trader", traderMemory,
            "invest_judge", investJudgeMemory,
            "portfolio_manager", portfolioManagerMemory
        ));
    }
}
