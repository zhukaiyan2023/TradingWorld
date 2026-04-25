package com.tradingworld.domain.do.trading;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeDecisionDO implements Serializable {
    private Long id;
    private String symbol;
    private String tradeDate;
    private String decision;
    private String bullCase;
    private String bearCase;
    private Double targetPrice;
    private Double stopLoss;
    private String confidence;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}