package com.tradingworld.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 交易配置属性。
 * 映射 application.yml 中 trading 前缀的配置。
 *
 * <p>包含子配置：
 * <ul>
 *   <li>{@link QuoteProperties} - 行情配置</li>
 *   <li>{@link TradingConfig} - 交易配置</li>
 *   <li>{@link AnalysisConfig} - 分析配置</li>
 * </ul>
 */
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