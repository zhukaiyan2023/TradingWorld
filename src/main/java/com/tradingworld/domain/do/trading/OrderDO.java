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
public class OrderDO implements Serializable {
    private Long id;
    private String orderId;
    private String portfolioId;
    private String symbol;
    private String direction;
    private String orderType;
    private Double quantity;
    private Double price;
    private Double filledQuantity;
    private Double filledPrice;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}