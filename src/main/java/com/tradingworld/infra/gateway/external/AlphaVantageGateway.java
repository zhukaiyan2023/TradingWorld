package com.tradingworld.infra.gateway.external;

import com.tradingworld.domain.dom.quote.StockSpotDO;
import com.tradingworld.domain.dom.quote.StockDailyDO;
import com.tradingworld.domain.gateway.QuoteGateway;
import com.tradingworld.dataflows.AlphaVantageVendor;
import com.tradingworld.dataflows.DataVendor;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * AlphaVantage 行情网关实现。
 * 通过 AlphaVantage API 获取美股行情数据。
 *
 * <p>注意：此实现仅用于美股数据，A 股数据应使用 {@link com.tradingworld.infra.gateway.mysql.QuoteMySQLGateway}。
 *
 * @see QuoteGateway 行情网关接口
 * @see AlphaVantageVendor AlphaVantage 数据供应商
 */
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
                .map(quote -> toSpotDO(quote, symbol))
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
                .map(Optional::get)
                .map(quote -> toSpotDO(quote, quote.symbol()))
                .collect(Collectors.toList());
    }

    @Override
    public List<StockDailyDO> getDaily(String symbol, LocalDate start, LocalDate end) {
        if (!vendor.isAvailable()) {
            return List.of();
        }
        return vendor.getHistorical(symbol, "full")
                .orElse(List.of())
                .stream()
                .filter(candle -> {
                    LocalDate date = candle.datetime().toLocalDate();
                    return !date.isBefore(start) && !date.isAfter(end);
                })
                .map(candle -> toDailyDO(candle, symbol))
                .collect(Collectors.toList());
    }

    @Override
    public StockDailyDO getDailySingle(String symbol, LocalDate date) {
        var list = getDaily(symbol, date, date);
        return list.isEmpty() ? null : list.get(0);
    }

    private StockSpotDO toSpotDO(DataVendor.StockQuote quote, String symbol) {
        return StockSpotDO.builder()
                .symbol(symbol)
                .name(symbol)
                .price(quote.price())
                .changePercent(quote.changePercent())
                .volume((double) quote.volume())
                .build();
    }

    private StockDailyDO toDailyDO(DataVendor.Candle candle, String symbol) {
        return StockDailyDO.builder()
                .symbol(symbol)
                .tradeDate(candle.datetime().toLocalDate())
                .open(candle.open())
                .high(candle.high())
                .low(candle.low())
                .close(candle.close())
                .volume((double) candle.volume())
                .build();
    }
}
