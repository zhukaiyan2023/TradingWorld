package com.tradingworld.qry.quote;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class GetStockDailyQry {
    @NotBlank(message = "股票代码不能为空")
    private String symbol;

    private LocalDate startDate;
    private LocalDate endDate;
}