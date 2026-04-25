package com.tradingworld.domain.gateway;

import com.tradingworld.domain.dom.trading.OrderDO;
import com.tradingworld.domain.dom.trading.PositionDO;
import java.util.List;

/**
 * 交易网关接口。
 * 定义订单和持仓管理的核心操作。
 *
 * <p>提供订单创建、取消以及持仓查询、更新等功能。
 *
 * @see OrderDO 订单信息
 * @see PositionDO 持仓信息
 */
public interface TradingGateway {

    /**
     * 创建新订单。
     *
     * @param order 订单信息
     * @return 创建后的订单信息（包含生成的订单 ID）
     */
    OrderDO createOrder(OrderDO order);

    /**
     * 取消订单。
     *
     * @param orderId 订单 ID
     * @return 取消后的订单信息，若订单不存在返回 null
     */
    OrderDO cancelOrder(String orderId);

    /**
     * 查询投资组合的持仓列表。
     *
     * @param portfolioId 投资组合 ID
     * @return 持仓信息列表
     */
    List<PositionDO> getPositions(String portfolioId);

    /**
     * 更新持仓信息。
     * 若持仓已存在则更新，不存在则新增。
     *
     * @param position 持仓信息
     * @return 更新后的持仓信息
     */
    PositionDO updatePosition(PositionDO position);
}