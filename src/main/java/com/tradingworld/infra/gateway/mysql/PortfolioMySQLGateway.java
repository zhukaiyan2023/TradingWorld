package com.tradingworld.infra.gateway.mysql;

import com.tradingworld.domain.dom.portfolio.PortfolioDO;
import com.tradingworld.domain.dom.portfolio.PerformanceDO;
import com.tradingworld.domain.gateway.PortfolioGateway;
import com.tradingworld.persistence.entity.PortfolioEntity;
import com.tradingworld.persistence.mapper.PortfolioMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MySQL 组合网关实现。
 * 通过 MyBatis-Plus 访问 MySQL 数据库获取投资组合数据。
 *
 * @see PortfolioGateway 组合网关接口
 * @see PortfolioEntity 投资组合实体
 */
@Component
public class PortfolioMySQLGateway implements PortfolioGateway {

    private final PortfolioMapper portfolioMapper;

    public PortfolioMySQLGateway(PortfolioMapper portfolioMapper) {
        this.portfolioMapper = portfolioMapper;
    }

    @Override
    public PortfolioDO getPortfolio(String portfolioId) {
        var wrapper = new LambdaQueryWrapper<PortfolioEntity>()
                .eq(PortfolioEntity::getPortfolioId, portfolioId);
        var entity = portfolioMapper.selectOne(wrapper);
        return entity == null ? null : toPortfolioDO(entity);
    }

    @Override
    public List<PortfolioDO> listPortfolios() {
        var entities = portfolioMapper.selectList(new LambdaQueryWrapper<>());
        return entities.stream().map(this::toPortfolioDO).collect(Collectors.toList());
    }

    @Override
    public PerformanceDO getPerformance(String portfolioId, LocalDate start, LocalDate end) {
        return null;
    }

    private PortfolioDO toPortfolioDO(PortfolioEntity entity) {
        return PortfolioDO.builder()
                .id(entity.getId())
                .portfolioId(entity.getPortfolioId())
                .name(entity.getName())
                .description(entity.getDescription())
                .totalValue(entity.getTotalValue())
                .cashBalance(entity.getCashBalance())
                .marketValue(entity.getMarketValue())
                .totalPnl(entity.getTotalPnl())
                .totalPnlPercent(entity.getTotalPnlPercent())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
