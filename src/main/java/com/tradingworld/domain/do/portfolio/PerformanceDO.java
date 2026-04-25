package com.tradingworld.domain.do.portfolio;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceDO implements Serializable {
    private Long id;
    private String portfolioId;
    private LocalDate reportDate;
    private Double totalReturn;
    private Double dailyReturn;
    private Double weeklyReturn;
    private Double monthlyReturn;
    private Double annualReturn;
    private Double sharpeRatio;
    private Double maxDrawdown;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}