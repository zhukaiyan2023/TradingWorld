package com.tradingworld.config;

import com.tradingworld.agents.analysts.FundamentalsAnalyst;
import com.tradingworld.agents.analysts.MarketAnalyst;
import com.tradingworld.agents.analysts.NewsAnalyst;
import com.tradingworld.agents.analysts.SentimentAnalyst;
import com.tradingworld.agents.researchers.BearResearcher;
import com.tradingworld.agents.researchers.BullResearcher;
import com.tradingworld.agents.risk_managers.AggressiveRiskManager;
import com.tradingworld.agents.risk_managers.ConservativeRiskManager;
import com.tradingworld.agents.risk_managers.NeutralRiskManager;
import com.tradingworld.agents.trader.Trader;
import com.tradingworld.graph.TradingAgentsGraph;
import com.tradingworld.memory.FinancialSituationMemory;
import com.tradingworld.memory.ReflectionManager;
import com.tradingworld.tools.FundamentalTools;
import com.tradingworld.tools.InsiderTools;
import com.tradingworld.tools.MarketTools;
import com.tradingworld.tools.NewsTools;
import com.tradingworld.tools.TechnicalTools;
import dev.langchain4j.model.chat.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * TradingAgents图形组件的Spring配置。
 */
@Configuration
public class GraphConfig {

    // 工具bean在ToolsConfig中创建

    // 分析师bean
    @Bean
    public MarketAnalyst marketAnalyst(MarketTools marketTools, TechnicalTools technicalTools) {
        return new MarketAnalyst(marketTools, technicalTools);
    }

    @Bean
    public SentimentAnalyst sentimentAnalyst(NewsTools newsTools) {
        return new SentimentAnalyst(newsTools);
    }

    @Bean
    public NewsAnalyst newsAnalyst(NewsTools newsTools, InsiderTools insiderTools) {
        return new NewsAnalyst(newsTools, insiderTools);
    }

    @Bean
    public FundamentalsAnalyst fundamentalsAnalyst(FundamentalTools fundamentalTools) {
        return new FundamentalsAnalyst(fundamentalTools);
    }

    // 研究员bean
    @Bean
    public BullResearcher bullResearcher(FinancialSituationMemory bullMemory, AppConfig config) {
        return new BullResearcher(bullMemory, config.getAgents().getMaxDebateRounds());
    }

    @Bean
    public BearResearcher bearResearcher(FinancialSituationMemory bearMemory, AppConfig config) {
        return new BearResearcher(bearMemory, config.getAgents().getMaxDebateRounds());
    }

    // 交易员bean
    @Bean
    public Trader trader(FinancialSituationMemory traderMemory) {
        return new Trader(traderMemory);
    }

    // 风险管理器bean
    @Bean
    public AggressiveRiskManager aggressiveRiskManager() {
        return new AggressiveRiskManager();
    }

    @Bean
    public ConservativeRiskManager conservativeRiskManager() {
        return new ConservativeRiskManager();
    }

    @Bean
    public NeutralRiskManager neutralRiskManager() {
        return new NeutralRiskManager();
    }

    // 反思管理器bean
    @Bean
    public ReflectionManager reflectionManager(
            FinancialSituationMemory bullMemory,
            FinancialSituationMemory bearMemory,
            FinancialSituationMemory traderMemory,
            FinancialSituationMemory investJudgeMemory,
            FinancialSituationMemory portfolioManagerMemory) {
        return new ReflectionManager(
                bullMemory, bearMemory, traderMemory,
                investJudgeMemory, portfolioManagerMemory);
    }

    // 主图形bean
    @Bean
    public TradingAgentsGraph tradingAgentsGraph(
            AppConfig config,
            @Qualifier("deepThinkChatModel") ChatModel deepThinkModel,
            @Qualifier("quickThinkChatModel") ChatModel quickThinkModel,
            MarketAnalyst marketAnalyst,
            SentimentAnalyst sentimentAnalyst,
            NewsAnalyst newsAnalyst,
            FundamentalsAnalyst fundamentalsAnalyst,
            BullResearcher bullResearcher,
            BearResearcher bearResearcher,
            Trader trader,
            AggressiveRiskManager aggressiveRiskManager,
            ConservativeRiskManager conservativeRiskManager,
            NeutralRiskManager neutralRiskManager,
            FinancialSituationMemory bullMemory,
            FinancialSituationMemory bearMemory,
            FinancialSituationMemory traderMemory,
            FinancialSituationMemory investJudgeMemory,
            FinancialSituationMemory portfolioManagerMemory) {

        return TradingAgentsGraph.builder()
                .config(config)
                .deepThinkModel(deepThinkModel)
                .quickThinkModel(quickThinkModel)
                .marketAnalyst(marketAnalyst)
                .sentimentAnalyst(sentimentAnalyst)
                .newsAnalyst(newsAnalyst)
                .fundamentalsAnalyst(fundamentalsAnalyst)
                .bullResearcher(bullResearcher)
                .bearResearcher(bearResearcher)
                .trader(trader)
                .aggressiveRiskManager(aggressiveRiskManager)
                .conservativeRiskManager(conservativeRiskManager)
                .neutralRiskManager(neutralRiskManager)
                .bullMemory(bullMemory)
                .bearMemory(bearMemory)
                .traderMemory(traderMemory)
                .investJudgeMemory(investJudgeMemory)
                .portfolioManagerMemory(portfolioManagerMemory)
                .build();
    }
}
