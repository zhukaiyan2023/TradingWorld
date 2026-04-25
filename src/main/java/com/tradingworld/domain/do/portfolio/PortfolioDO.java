package com.tradingworld.domain.do.portfolio;

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
public class PortfolioDO implements Serializable {
    private Long id;
    private String portfolioId;
    private String name;
    private String description;
    private Double totalValue;
    private Double cashBalance;
    private Double marketValue;
    private Double totalPnl;
    private Double totalPnlPercent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}