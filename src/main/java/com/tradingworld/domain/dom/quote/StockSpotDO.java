package com.tradingworld.domain.dom.quote;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 股票实时行情数据对象。
 * 包含股票的实时价格、涨跌幅、成交量等关键行情信息。
 *
 * <p>用于：
 * <ul>
 *   <li>获取个股实时报价</li>
 *   <li>展示股票当前市场状态</li>
 *   <li>支持交易决策的价格参考</li>
 * </ul>
 *
 * @see StockDailyDO 日 K 线数据
 * @see StockMinuteDO 分钟数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockSpotDO implements Serializable {

    /** 主键 ID */
    private Long id;

    /** 股票代码，如 "002606" */
    private String symbol;

    /** 股票名称 */
    private String name;

    /** 当前价格（元） */
    private Double price;

    /** 涨跌幅（%），正数为上涨，负数为下跌 */
    private Double changePercent;

    /** 昨收价格（前一日收盘价） */
    private Double preClose;

    /** 今日开盘价 */
    private Double open;

    /** 今日最高价 */
    private Double high;

    /** 今日最低价 */
    private Double low;

    /** 成交量（股） */
    private Double volume;

    /** 成交额（元） */
    private Double amount;

    /** 数据更新时间 */
    private LocalDateTime updateTime;

    /** 记录创建时间 */
    private LocalDateTime createdAt;

    /** 记录更新时间 */
    private LocalDateTime updatedAt;
}