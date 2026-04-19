package com.tradingworld.config;

import com.tradingworld.cache.CacheManager;
import com.tradingworld.dataflows.AlphaVantageVendor;
import com.tradingworld.dataflows.AShareVendor;
import com.tradingworld.dataflows.CachedVendorRouter;
import com.tradingworld.dataflows.VendorRouter;
import com.tradingworld.dataflows.YFinanceVendor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

/**
 * 数据流配置。
 * 提供共享的 VendorRouter Bean 给所有需要数据访问的组件。
 */
@Configuration
public class DataflowsConfig {

    @Bean
    public VendorRouter vendorRouter() {
        YFinanceVendor yFinanceVendor = new YFinanceVendor();
        AlphaVantageVendor alphaVantageVendor = new AlphaVantageVendor();
        AShareVendor aShareVendor = new AShareVendor();
        return new VendorRouter(List.of(yFinanceVendor, alphaVantageVendor, aShareVendor));
    }

    @Bean
    public CacheManager cacheManager() {
        // 默认 5 分钟缓存
        return new CacheManager(Duration.ofMinutes(5));
    }

    @Bean
    public CachedVendorRouter cachedVendorRouter(VendorRouter vendorRouter, CacheManager cacheManager) {
        return new CachedVendorRouter(vendorRouter, cacheManager);
    }
}
