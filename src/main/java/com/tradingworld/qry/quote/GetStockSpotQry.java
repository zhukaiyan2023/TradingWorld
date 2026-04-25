package com.tradingworld.qry.quote;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 获取股票实时行情查询。
 * 用于查询单只股票的实时行情数据。
 *
 * @author TradingWorld
 */
@Data
public class GetStockSpotQry {

    /** 股票代码 */
    @NotBlank(message = "股票代码不能为空")
    private String symbol;
}