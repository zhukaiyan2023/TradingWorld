package com.tradingworld.config;

import com.tradingworld.dataflows.VendorRouter;
import com.tradingworld.tools.FundamentalTools;
import com.tradingworld.tools.InsiderTools;
import com.tradingworld.tools.MarketTools;
import com.tradingworld.tools.NewsTools;
import com.tradingworld.tools.TechnicalTools;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 交易工具的配置。
 * 工具供AI智能体获取金融数据使用。
 */
@Configuration
public class ToolsConfig {

    @Bean
    public MarketTools marketTools() {
        return new MarketTools();
    }

    @Bean
    public TechnicalTools technicalTools() {
        return new TechnicalTools();
    }

    @Bean
    public FundamentalTools fundamentalTools() {
        return new FundamentalTools();
    }

    @Bean
    public NewsTools newsTools(VendorRouter vendorRouter) {
        return new NewsTools(vendorRouter);
    }

    @Bean
    public InsiderTools insiderTools() {
        return new InsiderTools();
    }
}