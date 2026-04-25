package com.tradingworld.domain.dom.quote;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 股票日 K 线数据对象。
 * 包含股票每个交易日的开盘价、收盘价、最高价、最低价、成交量等数据。
 *
 * <p>用于：
 * <ul>
 *   <li>技术分析（K 线图、指标计算）</li>
 *   <li>回测系统的历史数据</li>
 *   <li>趋势分析和预测</li>
 * </ul>
 *
 * @see StockSpotDO 实时行情
 * @see StockMinuteDO 分钟数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockDailyDO implements Serializable {

    /** 主键 ID */
    private Long id;

    /** 股票代码，如 "002606" */
    private String symbol;

    /** 交易日期 */
    private LocalDate tradeDate;

    /** 开盘价 */
    private Double open;

    /** 最高价 */
    private Double high;

    /** 最低价 */
    private Double low;

    /** 收盘价 */
    private Double close;

    /** 成交量（股） */
    private Double volume;

    /** 成交额（元） */
    private Double amount;

    /** 复权类型：N-不复权, F-前复权, B-后复权 */
    private String adjustFlag;

    /** 换手率（%） */
    private Double turnoverRate;

    /** 记录创建时间 */
    private LocalDateTime createdAt;

    /** 记录更新时间 */
    private LocalDateTime updatedAt;
}