package com.tradingworld;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.tradingworld.config.AppConfig;

@SpringBootApplication
@EnableConfigurationProperties(AppConfig.class)
@MapperScan("com.tradingworld.persistence.mapper")
public class TradingAgentsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradingAgentsApplication.class, args);
    }
}
