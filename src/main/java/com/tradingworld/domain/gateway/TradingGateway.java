package com.tradingworld.domain.gateway;

import com.tradingworld.domain.do.trading.OrderDO;
import com.tradingworld.domain.do.trading.PositionDO;
import java.util.List;

public interface TradingGateway {
    OrderDO createOrder(OrderDO order);
    OrderDO cancelOrder(String orderId);
    List<PositionDO> getPositions(String portfolioId);
    PositionDO updatePosition(PositionDO position);
}