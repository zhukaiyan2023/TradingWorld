package com.tradingworld.domain.do.analysis;

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
public class RiskAssessmentDO implements Serializable {
    private Long id;
    private String symbol;
    private String tradeDate;
    private String riskLevel;
    private String riskAssessment;
    private String riskFactors;
    private Double maxLossPercent;
    private Double recommendedPositionSize;
    private String finalDecision;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}