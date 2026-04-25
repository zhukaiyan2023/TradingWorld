package com.tradingworld.persistence.entity.astock;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A股日 K 线实体。
 * 对应数据库中的 astock_daily 表。
 *
 * <p>字段说明：
 * <ul>
 *   <li>symbol - 股票代码</li>
 *   <li>tradeDate - 交易日期</li>
 *   <li>open - 开盘价</li>
 *   <li>high - 最高价</li>
 *   <li>low - 最低价</li>
 *   <li>close - 收盘价</li>
 *   <li>volume - 成交量</li>
 *   <li>amount - 成交额</li>
 *   <li>adjustFlag - 复权标志</li>
 *   <li>turnoverRate - 换手率</li>
 * </ul>
 */
@Data
@TableName("astock_daily")
public class AstockDailyEntity {

    /** 主键 ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 股票代码 */
    private String symbol;

    /** 交易日期 */
    private LocalDate tradeDate;

    /** 开盘价 */
    private BigDecimal open;

    /** 最高价 */
    private BigDecimal high;

    /** 最低价 */
    private BigDecimal low;

    /** 收盘价 */
    private BigDecimal close;

    /** 成交量 */
    private BigDecimal volume;

    /** 成交额 */
    private BigDecimal amount;

    /** 复权标志 */
    private String adjustFlag;

    /** 换手率 */
    private BigDecimal turnoverRate;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}