package com.tradingworld.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "trading.quote")
public class QuoteProperties {
    private boolean cacheEnabled = true;
    private int cacheTtl = 300;
    private String defaultSymbol;
}