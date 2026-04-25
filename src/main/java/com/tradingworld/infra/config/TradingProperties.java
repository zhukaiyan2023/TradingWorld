package com.tradingworld.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "trading")
public class TradingProperties {
    private QuoteProperties quote = new QuoteProperties();
    private TradingConfig trading = new TradingConfig();
    private AnalysisConfig analysis = new AnalysisConfig();

    @Data
    public static class TradingConfig {
        private double maxPositionSize = 100000;
        private double riskThreshold = 0.15;
        private int maxDebateRounds = 3;
    }

    @Data
    public static class AnalysisConfig {
        private String model = "deep-think";
        private int timeoutSeconds = 300;
    }
}