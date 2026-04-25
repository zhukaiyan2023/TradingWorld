package com.tradingworld.infra.gateway.mysql;

import com.tradingworld.domain.dom.trading.OrderDO;
import com.tradingworld.domain.dom.trading.PositionDO;
import com.tradingworld.domain.gateway.TradingGateway;
import com.tradingworld.persistence.entity.OrderEntity;
import com.tradingworld.persistence.entity.PositionEntity;
import com.tradingworld.persistence.mapper.OrderMapper;
import com.tradingworld.persistence.mapper.PositionMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * MySQL 交易网关实现。
 * 通过 MyBatis-Plus 访问 MySQL 数据库管理订单和持仓数据。
 *
 * @see TradingGateway 交易网关接口
 * @see OrderEntity 订单实体
 * @see PositionEntity 持仓实体
 */
@Component
public class TradingMySQLGateway implements TradingGateway {

    private final OrderMapper orderMapper;
    private final PositionMapper positionMapper;

    public TradingMySQLGateway(OrderMapper orderMapper, PositionMapper positionMapper) {
        this.orderMapper = orderMapper;
        this.positionMapper = positionMapper;
    }

    @Override
    public OrderDO createOrder(OrderDO order) {
        OrderEntity entity = toOrderEntity(order);
        entity.setOrderId(UUID.randomUUID().toString());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setStatus("PENDING");
        orderMapper.insert(entity);
        return toOrderDO(entity);
    }

    @Override
    public OrderDO cancelOrder(String orderId) {
        var wrapper = new LambdaQueryWrapper<OrderEntity>()
                .eq(OrderEntity::getOrderId, orderId);
        var entity = orderMapper.selectOne(wrapper);
        if (entity == null) {
            return null;
        }
        entity.setStatus("CANCELLED");
        entity.setUpdatedAt(LocalDateTime.now());
        orderMapper.updateById(entity);
        return toOrderDO(entity);
    }

    @Override
    public List<PositionDO> getPositions(String portfolioId) {
        var wrapper = new LambdaQueryWrapper<PositionEntity>()
                .eq(PositionEntity::getPortfolioId, portfolioId);
        var entities = positionMapper.selectList(wrapper);
        return entities.stream().map(this::toPositionDO).collect(Collectors.toList());
    }

    @Override
    public PositionDO updatePosition(PositionDO position) {
        var wrapper = new LambdaQueryWrapper<PositionEntity>()
                .eq(PositionEntity::getPortfolioId, position.getPortfolioId())
                .eq(PositionEntity::getSymbol, position.getSymbol());
        var existing = positionMapper.selectOne(wrapper);

        PositionEntity entity = toPositionEntity(position);
        if (existing != null) {
            entity.setId(existing.getId());
            entity.setUpdatedAt(LocalDateTime.now());
            positionMapper.updateById(entity);
        } else {
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
            positionMapper.insert(entity);
        }
        return toPositionDO(entity);
    }

    private OrderDO toOrderDO(OrderEntity entity) {
        return OrderDO.builder()
                .id(entity.getId())
                .orderId(entity.getOrderId())
                .portfolioId(entity.getPortfolioId())
                .symbol(entity.getSymbol())
                .direction(entity.getDirection())
                .orderType(entity.getOrderType())
                .quantity(entity.getQuantity())
                .price(entity.getPrice())
                .filledQuantity(entity.getFilledQuantity())
                .filledPrice(entity.getFilledPrice())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private OrderEntity toOrderEntity(OrderDO order) {
        OrderEntity entity = new OrderEntity();
        entity.setPortfolioId(order.getPortfolioId());
        entity.setSymbol(order.getSymbol());
        entity.setDirection(order.getDirection());
        entity.setOrderType(order.getOrderType());
        entity.setQuantity(order.getQuantity());
        entity.setPrice(order.getPrice());
        entity.setFilledQuantity(order.getFilledQuantity());
        entity.setFilledPrice(order.getFilledPrice());
        return entity;
    }

    private PositionDO toPositionDO(PositionEntity entity) {
        return PositionDO.builder()
                .id(entity.getId())
                .portfolioId(entity.getPortfolioId())
                .symbol(entity.getSymbol())
                .quantity(entity.getQuantity())
                .avgCost(entity.getAvgCost())
                .currentPrice(entity.getCurrentPrice())
                .marketValue(entity.getMarketValue())
                .unrealizedPnl(entity.getUnrealizedPnl())
                .realizedPnl(entity.getRealizedPnl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private PositionEntity toPositionEntity(PositionDO position) {
        PositionEntity entity = new PositionEntity();
        entity.setPortfolioId(position.getPortfolioId());
        entity.setSymbol(position.getSymbol());
        entity.setQuantity(position.getQuantity());
        entity.setAvgCost(position.getAvgCost());
        entity.setCurrentPrice(position.getCurrentPrice());
        entity.setMarketValue(position.getMarketValue());
        entity.setUnrealizedPnl(position.getUnrealizedPnl());
        entity.setRealizedPnl(position.getRealizedPnl());
        return entity;
    }
}
