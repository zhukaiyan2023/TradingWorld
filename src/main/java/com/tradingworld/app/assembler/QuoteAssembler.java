package com.tradingworld.app.assembler;

import com.tradingworld.domain.do.quote.StockSpotDO;
import com.tradingworld.domain.do.quote.StockDailyDO;
import com.tradingworld.dto.QuoteDTO;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class QuoteAssembler {

    public QuoteDTO toDTO(StockSpotDO spot) {
        if (spot == null) return null;
        QuoteDTO dto = new QuoteDTO();
        dto.setSymbol(spot.getSymbol());
        dto.setCompanyName(spot.getName());
        dto.setCurrentPrice(spot.getPrice() != null ? BigDecimal.valueOf(spot.getPrice()) : null);
        dto.setChangePercent(spot.getChangePercent() != null ? BigDecimal.valueOf(spot.getChangePercent()) : null);
        dto.setOpenPrice(spot.getOpen() != null ? BigDecimal.valueOf(spot.getOpen()) : null);
        dto.setHighPrice(spot.getHigh() != null ? BigDecimal.valueOf(spot.getHigh()) : null);
        dto.setLowPrice(spot.getLow() != null ? BigDecimal.valueOf(spot.getLow()) : null);
        dto.setVolume(spot.getVolume() != null ? spot.getVolume().longValue() : null);
        return dto;
    }

    public QuoteDTO toDTO(StockDailyDO daily) {
        if (daily == null) return null;
        QuoteDTO dto = new QuoteDTO();
        dto.setSymbol(daily.getSymbol());
        dto.setQuoteDate(daily.getTradeDate());
        dto.setOpenPrice(daily.getOpen() != null ? BigDecimal.valueOf(daily.getOpen()) : null);
        dto.setHighPrice(daily.getHigh() != null ? BigDecimal.valueOf(daily.getHigh()) : null);
        dto.setLowPrice(daily.getLow() != null ? BigDecimal.valueOf(daily.getLow()) : null);
        dto.setVolume(daily.getVolume() != null ? daily.getVolume().longValue() : null);
        return dto;
    }
}