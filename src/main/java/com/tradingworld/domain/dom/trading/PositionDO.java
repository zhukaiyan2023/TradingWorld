package com.tradingworld.domain.dom.trading;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 持仓信息数据对象。
 * 记录投资组合中某只股票的持仓状态。
 *
 * <p>包含：
 * <ul>
 *   <li>持仓数量和平均成本</li>
 *   <li>当前市值和盈亏情况</li>
 *   <li>持仓成本分析</li>
 * </ul>
 *
 * @see TradeDecisionDO 交易决策
 * @see OrderDO 订单信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PositionDO implements Serializable {

    /** 主键 ID */
    private Long id;

    /** 投资组合 ID */
    private String portfolioId;

    /** 股票代码 */
    private String symbol;

    /** 持仓数量（股） */
    private Double quantity;

    /** 平均持仓成本（元） */
    private Double avgCost;

    /** 当前价格（元） */
    private Double currentPrice;

    /** 当前市值（元） */
    private Double marketValue;

    /** 浮动盈亏（元），正值盈利，负值亏损 */
    private Double unrealizedPnl;

    /** 已实现盈亏（元） */
    private Double realizedPnl;

    /** 记录创建时间 */
    private LocalDateTime createdAt;

    /** 记录更新时间 */
    private LocalDateTime updatedAt;
}