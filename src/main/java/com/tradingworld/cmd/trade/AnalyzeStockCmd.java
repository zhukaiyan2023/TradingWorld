package com.tradingworld.cmd.trade;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 股票分析命令。
 * 用于触发股票分析流程的请求参数。
 *
 * @author TradingWorld
 * @see com.tradingworld.adapter.controller.StockRecommendationController
 */
@Data
public class AnalyzeStockCmd {

    /** 股票代码，如 002606 */
    @NotBlank(message = "股票代码不能为空")
    private String symbol;

    /** 交易日期 */
    @NotNull(message = "交易日期不能为空")
    private LocalDate tradeDate;
}