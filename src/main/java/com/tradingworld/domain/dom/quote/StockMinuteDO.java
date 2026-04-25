package com.tradingworld.domain.dom.quote;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 股票分钟级行情数据对象。
 * 包含股票在特定时间点的分时数据。
 *
 * <p>用于：
 * <ul>
 *   <li>盘中实时监控</li>
 *   <li>分钟级技术指标计算</li>
 *   <li>高频交易策略回测</li>
 * </ul>
 *
 * @see StockSpotDO 实时行情
 * @see StockDailyDO 日 K 线数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMinuteDO implements Serializable {

    /** 主键 ID */
    private Long id;

    /** 股票代码 */
    private String symbol;

    /** 交易时间（精确到分钟） */
    private LocalDateTime tradeTime;

    /** 开盘价（该分钟开盘价） */
    private Double open;

    /** 最高价（该分钟内最高价） */
    private Double high;

    /** 最低价（该分钟内最低价） */
    private Double low;

    /** 收盘价（该分钟收盘价） */
    private Double close;

    /** 成交量（该分钟成交量） */
    private Double volume;

    /** 成交额（该分钟成交额） */
    private Double amount;

    /** 记录创建时间 */
    private LocalDateTime createdAt;

    /** 记录更新时间 */
    private LocalDateTime updatedAt;
}