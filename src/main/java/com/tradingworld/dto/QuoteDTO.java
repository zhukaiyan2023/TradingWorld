package com.tradingworld.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 行情数据传输对象。
 * 用于 API 返回股票实时行情和 K 线数据。
 *
 * @author TradingWorld
 */
@Data
public class QuoteDTO {

    /** 股票代码 */
    private String symbol;

    /** 公司名称 */
    private String companyName;

    /** 当前价格 */
    private BigDecimal currentPrice;

    /** 涨跌幅（%） */
    private BigDecimal changePercent;

    /** 开盘价 */
    private BigDecimal openPrice;

    /** 最高价 */
    private BigDecimal highPrice;

    /** 最低价 */
    private BigDecimal lowPrice;

    /** 成交量 */
    private Long volume;

    /** 行情日期 */
    private LocalDate quoteDate;
}
