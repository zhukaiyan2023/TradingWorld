package com.tradingworld.persistence.entity.astock;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A股实时行情实体。
 * 对应数据库中的 astock_spot 表。
 *
 * <p>字段说明：
 * <ul>
 *   <li>symbol - 股票代码，如 002606</li>
 *   <li>name - 股票名称</li>
 *   <li>close - 最新价</li>
 *   <li>changePercent - 涨跌幅（%）</li>
 *   <li>preClose - 昨收价</li>
 *   <li>open - 开盘价</li>
 *   <li>high - 最高价</li>
 *   <li>low - 最低价</li>
 *   <li>volume - 成交量</li>
 *   <li>amount - 成交额</li>
 *   <li>tradeDate - 交易日期</li>
 * </ul>
 */
@Data
@TableName("astock_spot")
public class AstockSpotEntity {

    /** 主键 ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 股票代码 */
    private String symbol;

    /** 股票名称 */
    private String name;

    /** 最新价 */
    private BigDecimal close;

    /** 涨跌幅（%） */
    private BigDecimal changePercent;

    /** 昨收价 */
    private BigDecimal preClose;

    /** 开盘价 */
    private BigDecimal open;

    /** 最高价 */
    private BigDecimal high;

    /** 最低价 */
    private BigDecimal low;

    /** 成交量 */
    private BigDecimal volume;

    /** 成交额 */
    private BigDecimal amount;

    /** 交易日期 */
    private LocalDate tradeDate;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}