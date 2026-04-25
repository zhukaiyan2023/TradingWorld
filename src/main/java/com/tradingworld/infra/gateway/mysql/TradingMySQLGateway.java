package com.tradingworld.infra.gateway.mysql;

import com.tradingworld.domain.do.trading.OrderDO;
import com.tradingworld.domain.do.trading.PositionDO;
import com.tradingworld.domain.gateway.TradingGateway;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;

@Component
public class TradingMySQLGateway implements TradingGateway {

    @Override
    public OrderDO createOrder(OrderDO order) {
        // TODO: Implement with OrderMapper
        return order;
    }

    @Override
    public OrderDO cancelOrder(String orderId) {
        // TODO: Implement with OrderMapper
        return null;
    }

    @Override
    public List<PositionDO> getPositions(String portfolioId) {
        // TODO: Implement with PositionMapper
        return Collections.emptyList();
    }

    @Override
    public PositionDO updatePosition(PositionDO position) {
        // TODO: Implement with PositionMapper
        return position;
    }
}