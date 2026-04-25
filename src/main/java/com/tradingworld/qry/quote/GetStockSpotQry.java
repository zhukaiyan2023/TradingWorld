package com.tradingworld.qry.quote;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class GetStockSpotQry {
    @NotBlank(message = "股票代码不能为空")
    private String symbol;
}