package com.tradingworld.domain.gateway;

import com.tradingworld.domain.do.quote.StockSpotDO;
import com.tradingworld.domain.do.quote.StockDailyDO;
import java.time.LocalDate;
import java.util.List;

public interface QuoteGateway {
    StockSpotDO getSpot(String symbol);
    List<StockSpotDO> getSpotList(List<String> symbols);
    List<StockDailyDO> getDaily(String symbol, LocalDate start, LocalDate end);
    StockDailyDO getDailySingle(String symbol, LocalDate date);
}