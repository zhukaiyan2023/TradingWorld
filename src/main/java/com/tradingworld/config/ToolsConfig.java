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
 * VendorRouter 通过构造器注入，确保所有工具共享同一个实例。
 */
@Configuration
public class ToolsConfig {

    @Bean
    public MarketTools marketTools(VendorRouter vendorRouter) {
        return new MarketTools(vendorRouter);
    }

    @Bean
    public TechnicalTools technicalTools(VendorRouter vendorRouter) {
        return new TechnicalTools(vendorRouter);
    }

    @Bean
    public FundamentalTools fundamentalTools(VendorRouter vendorRouter) {
        return new FundamentalTools(vendorRouter);
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
