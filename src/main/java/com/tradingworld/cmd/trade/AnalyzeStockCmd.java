package com.tradingworld.cmd.trade;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class AnalyzeStockCmd {
    @NotBlank(message = "股票代码不能为空")
    private String symbol;

    @NotNull(message = "交易日期不能为空")
    private LocalDate tradeDate;
}