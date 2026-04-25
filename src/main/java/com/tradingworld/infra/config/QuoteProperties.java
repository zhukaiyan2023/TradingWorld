package com.tradingworld.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 行情配置属性。
 * 映射 application.yml 中 trading.quote 前缀的配置。
 *
 * @see TradingProperties 交易配置属性
 */
@Data
@ConfigurationProperties(prefix = "trading.quote")
public class QuoteProperties {
    private boolean cacheEnabled = true;
    private int cacheTtl = 300;
    private String defaultSymbol;
}