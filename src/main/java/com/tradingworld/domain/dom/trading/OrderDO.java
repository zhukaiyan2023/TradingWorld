package com.tradingworld.domain.dom.trading;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单信息数据对象。
 * 记录交易订单的完整信息。
 *
 * <p>订单状态流转：
 * <ul>
 *   <li>PENDING → 待成交</li>
 *   <li>PARTIAL → 部分成交</li>
 *   <li>FILLED → 全部成交</li>
 *   <li>CANCELLED → 已取消</li>
 *   <li>REJECTED → 已拒绝</li>
 * </ul>
 *
 * @see PositionDO 持仓信息
 * @see TradeDecisionDO 交易决策
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDO implements Serializable {

    /** 主键 ID */
    private Long id;

    /** 订单唯一标识符（UUID） */
    private String orderId;

    /** 投资组合 ID */
    private String portfolioId;

    /** 股票代码 */
    private String symbol;

    /** 交易方向：BUY-买入, SELL-卖出 */
    private String direction;

    /** 订单类型：MARKET-市价单, LIMIT-限价单 */
    private String orderType;

    /** 委托数量（股） */
    private Double quantity;

    /** 委托价格（元，限价单有效） */
    private Double price;

    /** 已成交数量（股） */
    private Double filledQuantity;

    /** 成交价格（元） */
    private Double filledPrice;

    /** 订单状态：PENDING/FILLED/CANCELLED/REJECTED */
    private String status;

    /** 记录创建时间 */
    private LocalDateTime createdAt;

    /** 记录更新时间 */
    private LocalDateTime updatedAt;
}