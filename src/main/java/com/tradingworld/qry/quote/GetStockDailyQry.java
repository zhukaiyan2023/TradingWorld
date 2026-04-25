package com.tradingworld.qry.quote;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

/**
 * 获取股票日 K 线查询。
 * 用于查询股票历史日 K 线数据。
 *
 * @author TradingWorld
 */
@Data
public class GetStockDailyQry {

    /** 股票代码 */
    @NotBlank(message = "股票代码不能为空")
    private String symbol;

    /** 查询开始日期 */
    private LocalDate startDate;

    /** 查询结束日期 */
    private LocalDate endDate;
}