package com.tradingworld.domain.do.trading;

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
public class PositionDO implements Serializable {
    private Long id;
    private String portfolioId;
    private String symbol;
    private Double quantity;
    private Double avgCost;
    private Double currentPrice;
    private Double marketValue;
    private Double unrealizedPnl;
    private Double realizedPnl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}