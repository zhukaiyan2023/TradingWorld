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
public class DebateMessageDO implements Serializable {
    private Long id;
    private String symbol;
    private String tradeDate;
    private String debateRound;
    private String speaker;
    private String perspective;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}