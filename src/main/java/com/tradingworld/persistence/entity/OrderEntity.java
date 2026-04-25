package com.tradingworld.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 订单实体。
 * 对应数据库中的 orders 表。
 *
 * <p>字段说明：
 * <ul>
 *   <li>orderId - 订单唯一标识符</li>
 *   <li>portfolioId - 所属投资组合 ID</li>
 *   <li>symbol - 股票代码</li>
 *   <li>direction - 交易方向：BUY/SELL</li>
 *   <li>orderType - 订单类型：MARKET/LIMIT</li>
 *   <li>quantity - 委托数量</li>
 *   <li>price - 委托价格</li>
 *   <li>filledQuantity - 已成交数量</li>
 *   <li>filledPrice - 已成交价格</li>
 *   <li>status - 订单状态：PENDING/FILLED/CANCELLED</li>
 * </ul>
 */
@Data
@TableName("orders")
public class OrderEntity {

    /** 主键 ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单唯一标识符 */
    private String orderId;

    /** 所属投资组合 ID */
    private String portfolioId;

    /** 股票代码 */
    private String symbol;

    /** 交易方向：BUY/SELL */
    private String direction;

    /** 订单类型：MARKET/LIMIT */
    private String orderType;

    /** 委托数量 */
    private Double quantity;

    /** 委托价格 */
    private Double price;

    /** 已成交数量 */
    private Double filledQuantity;

    /** 已成交价格 */
    private Double filledPrice;

    /** 订单状态：PENDING/FILLED/CANCELLED */
    private String status;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
