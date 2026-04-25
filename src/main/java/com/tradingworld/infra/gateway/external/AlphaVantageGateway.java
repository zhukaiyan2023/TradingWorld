package com.tradingworld.infra.gateway.external;

import com.tradingworld.domain.do.quote.StockSpotDO;
import com.tradingworld.domain.do.quote.StockDailyDO;
import com.tradingworld.domain.gateway.QuoteGateway;
import com.tradingworld.dataflows.AlphaVantageVendor;
import com.tradingworld.dataflows.DataVendor;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AlphaVantageGateway implements QuoteGateway {

    private final AlphaVantageVendor vendor;

    public AlphaVantageGateway(AlphaVantageVendor vendor) {
        this.vendor = vendor;
    }

    @Override
    public StockSpotDO getSpot(String symbol) {
        if (!vendor.isAvailable()) {
            return null;
        }
        return vendor.getStockQuote(symbol)
                .map(this::toSpotDO)
                .orElse(null);
    }

    @Override
    public List<StockSpotDO> getSpotList(List<String> symbols) {
        if (!vendor.isAvailable()) {
            return List.of();
        }
        return symbols.stream()
                .map(vendor::getStockQuote)
                .filter(Optional::isPresent)
                .map(opt -> toSpotDO(opt.get()))
                .collect(Collectors.toList());
    }

    @Override
    public List<StockDailyDO> getDaily(String symbol, LocalDate start, LocalDate end) {
        if (!vendor.isAvailable()) {
            return List.of();
        }
        return vendor.getDailyTimeSeries(symbol, start, end)
                .stream()
                .map(this::toDailyDO)
                .collect(Collectors.toList());
    }

    @Override
    public StockDailyDO getDailySingle(String symbol, LocalDate date) {
        var list = getDaily(symbol, date, date);
        return list.isEmpty() ? null : list.get(0);
    }

    private StockSpotDO toSpotDO(DataVendor.StockQuote quote) {
        return StockSpotDO.builder()
                .symbol(quote.getSymbol())
                .name(quote.getSymbol())
                .price(quote.getPrice())
                .changePercent(quote.getChangePercent())
                .open(quote.getOpen())
                .high(quote.getHigh())
                .low(quote.getLow())
                .volume(quote.getVolume())
                .build();
    }

    private StockDailyDO toDailyDO(DataVendor.DailyBar bar) {
        return StockDailyDO.builder()
                .symbol(bar.getSymbol())
                .tradeDate(bar.getDate())
                .open(bar.getOpen())
                .high(bar.getHigh())
                .low(bar.getLow())
                .close(bar.getClose())
                .volume((double) bar.getVolume())
                .build();
    }
}