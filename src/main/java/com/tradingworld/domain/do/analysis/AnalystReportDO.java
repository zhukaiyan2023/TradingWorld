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
public class AnalystReportDO implements Serializable {
    private Long id;
    private String symbol;
    private String tradeDate;
    private String marketAnalysis;
    private String sentimentAnalysis;
    private String newsAnalysis;
    private String fundamentalsAnalysis;
    private String overallRecommendation;
    private String confidence;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}