package com.tradingworld.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;

/**
 * TradingAgents应用程序的主要配置属性。
 * 映射到application.yml中的'trading'前缀。
 */
@ConfigurationProperties(prefix = "trading")
@Validated
public class AppConfig {

    private AgentsConfig agents = new AgentsConfig();
    private LlmConfig llm = new LlmConfig();
    private MemoryConfig memory = new MemoryConfig();
    private PathsConfig paths = new PathsConfig();

    // 嵌套配置类

    public static class AgentsConfig {
        private List<String> selectedAnalysts = List.of("market", "sentiment", "news", "fundamentals");

        @Positive
        private int maxDebateRounds = 1;

        @Positive
        private int maxRiskDiscussRounds = 1;

        @Positive
        private int maxRecurLimit = 100;

        public List<String> getSelectedAnalysts() { return selectedAnalysts; }
        public void setSelectedAnalysts(List<String> selectedAnalysts) { this.selectedAnalysts = selectedAnalysts; }
        public int getMaxDebateRounds() { return maxDebateRounds; }
        public void setMaxDebateRounds(int maxDebateRounds) { this.maxDebateRounds = maxDebateRounds; }
        public int getMaxRiskDiscussRounds() { return maxRiskDiscussRounds; }
        public void setMaxRiskDiscussRounds(int maxRiskDiscussRounds) { this.maxRiskDiscussRounds = maxRiskDiscussRounds; }
        public int getMaxRecurLimit() { return maxRecurLimit; }
        public void setMaxRecurLimit(int maxRecurLimit) { this.maxRecurLimit = maxRecurLimit; }
    }

    public static class LlmConfig {
        @NotBlank
        private String provider = "openai";

        @NotBlank
        private String deepThinkModel = "gpt-4.5";

        @NotBlank
        private String quickThinkModel = "gpt-4.5-mini";

        @NotBlank
        private String outputLanguage = "English";

        // 提供商特定设置
        private String backendUrl;
        private String googleThinkingLevel;
        private String openaiReasoningEffort;
        private String anthropicEffort;

        // 国内提供商模型默认值
        private String minimaxModel = "minimax-01";
        private String deepseekModel = "deepseek-chat";
        private String qwenModel = "qwen-max";
        private String ernieModel = "ernie-4.0-8k-latest";
        private String glmModel = "glm-4";
        private String doubaoModel = "doubao-pro-32k";

        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }
        public String getDeepThinkModel() { return deepThinkModel; }
        public void setDeepThinkModel(String deepThinkModel) { this.deepThinkModel = deepThinkModel; }
        public String getQuickThinkModel() { return quickThinkModel; }
        public void setQuickThinkModel(String quickThinkModel) { this.quickThinkModel = quickThinkModel; }
        public String getOutputLanguage() { return outputLanguage; }
        public void setOutputLanguage(String outputLanguage) { this.outputLanguage = outputLanguage; }
        public String getBackendUrl() { return backendUrl; }
        public void setBackendUrl(String backendUrl) { this.backendUrl = backendUrl; }
        public String getGoogleThinkingLevel() { return googleThinkingLevel; }
        public void setGoogleThinkingLevel(String googleThinkingLevel) { this.googleThinkingLevel = googleThinkingLevel; }
        public String getOpenaiReasoningEffort() { return openaiReasoningEffort; }
        public void setOpenaiReasoningEffort(String openaiReasoningEffort) { this.openaiReasoningEffort = openaiReasoningEffort; }
        public String getAnthropicEffort() { return anthropicEffort; }
        public void setAnthropicEffort(String anthropicEffort) { this.anthropicEffort = anthropicEffort; }
        public String getMinimaxModel() { return minimaxModel; }
        public void setMinimaxModel(String minimaxModel) { this.minimaxModel = minimaxModel; }
        public String getDeepseekModel() { return deepseekModel; }
        public void setDeepseekModel(String deepseekModel) { this.deepseekModel = deepseekModel; }
        public String getQwenModel() { return qwenModel; }
        public void setQwenModel(String qwenModel) { this.qwenModel = qwenModel; }
        public String getErnieModel() { return ernieModel; }
        public void setErnieModel(String ernieModel) { this.ernieModel = ernieModel; }
        public String getGlmModel() { return glmModel; }
        public void setGlmModel(String glmModel) { this.glmModel = glmModel; }
        public String getDoubaoModel() { return doubaoModel; }
        public void setDoubaoModel(String doubaoModel) { this.doubaoModel = doubaoModel; }
    }

    public static class MemoryConfig {
        private double bm25K1 = 1.5;
        private double bm25B = 0.75;
        private int maxSimilarities = 3;

        public double getBm25K1() { return bm25K1; }
        public void setBm25K1(double bm25K1) { this.bm25K1 = bm25K1; }
        public double getBm25B() { return bm25B; }
        public void setBm25B(double bm25B) { this.bm25B = bm25B; }
        public int getMaxSimilarities() { return maxSimilarities; }
        public void setMaxSimilarities(int maxSimilarities) { this.maxSimilarities = maxSimilarities; }
    }

    public static class PathsConfig {
        private String resultsDir = System.getProperty("user.home") + "/.tradingworld/logs";
        private String dataCacheDir = System.getProperty("user.home") + "/.tradingworld/cache";

        public String getResultsDir() { return resultsDir; }
        public void setResultsDir(String resultsDir) { this.resultsDir = resultsDir; }
        public String getDataCacheDir() { return dataCacheDir; }
        public void setDataCacheDir(String dataCacheDir) { this.dataCacheDir = dataCacheDir; }
    }

    // 顶层配置的getter和setter
    public AgentsConfig getAgents() { return agents; }
    public void setAgents(AgentsConfig agents) { this.agents = agents; }
    public LlmConfig getLlm() { return llm; }
    public void setLlm(LlmConfig llm) { this.llm = llm; }
    public MemoryConfig getMemory() { return memory; }
    public void setMemory(MemoryConfig memory) { this.memory = memory; }
    public PathsConfig getPaths() { return paths; }
    public void setPaths(PathsConfig paths) { this.paths = paths; }
}
