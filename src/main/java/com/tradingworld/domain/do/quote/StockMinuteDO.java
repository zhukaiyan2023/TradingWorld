package com.tradingworld.domain.do.quote;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMinuteDO implements Serializable {
    private Long id;
    private String symbol;
    private LocalDateTime tradeTime;
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Double volume;
    private Double amount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}