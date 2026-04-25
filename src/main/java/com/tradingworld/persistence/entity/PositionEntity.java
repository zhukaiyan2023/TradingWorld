package com.tradingworld.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 持仓信息实体。
 * 对应数据库中的 positions 表。
 *
 * <p>字段说明：
 * <ul>
 *   <li>portfolioId - 所属投资组合 ID</li>
 *   <li>symbol - 股票代码</li>
 *   <li>quantity - 持仓数量</li>
 *   <li>avgCost - 平均成本价</li>
 *   <li>currentPrice - 当前价格</li>
 *   <li>marketValue - 市值 = 数量 × 当前价格</li>
 *   <li>unrealizedPnl - 浮动盈亏</li>
 *   <li>realizedPnl - 已实现盈亏</li>
 * </ul>
 */
@Data
@TableName("positions")
public class PositionEntity {

    /** 主键 ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属投资组合 ID */
    private String portfolioId;

    /** 股票代码 */
    private String symbol;

    /** 持仓数量 */
    private Double quantity;

    /** 平均成本价 */
    private Double avgCost;

    /** 当前价格 */
    private Double currentPrice;

    /** 市值 */
    private Double marketValue;

    /** 浮动盈亏 */
    private Double unrealizedPnl;

    /** 已实现盈亏 */
    private Double realizedPnl;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
