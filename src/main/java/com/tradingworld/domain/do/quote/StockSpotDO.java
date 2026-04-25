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
public class StockSpotDO implements Serializable {
    private Long id;
    private String symbol;
    private String name;
    private Double price;
    private Double changePercent;
    private Double preClose;
    private Double open;
    private Double high;
    private Double low;
    private Double volume;
    private Double amount;
    private LocalDateTime updateTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}