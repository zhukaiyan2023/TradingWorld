package com.tradingworld.infra.gateway.mysql;

import com.tradingworld.domain.do.quote.StockSpotDO;
import com.tradingworld.domain.do.quote.StockDailyDO;
import com.tradingworld.domain.gateway.QuoteGateway;
import com.tradingworld.persistence.entity.astock.AstockSpotEntity;
import com.tradingworld.persistence.entity.astock.AstockDailyEntity;
import com.tradingworld.persistence.mapper.astock.AstockSpotMapper;
import com.tradingworld.persistence.mapper.astock.AstockDailyMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuoteMySQLGateway implements QuoteGateway {

    private final AstockSpotMapper spotMapper;
    private final AstockDailyMapper dailyMapper;

    public QuoteMySQLGateway(AstockSpotMapper spotMapper, AstockDailyMapper dailyMapper) {
        this.spotMapper = spotMapper;
        this.dailyMapper = dailyMapper;
    }

    @Override
    public StockSpotDO getSpot(String symbol) {
        var wrapper = new LambdaQueryWrapper<AstockSpotEntity>().eq(AstockSpotEntity::getSymbol, symbol);
        var entity = spotMapper.selectOne(wrapper);
        return entity == null ? null : toSpotDO(entity);
    }

    @Override
    public List<StockSpotDO> getSpotList(List<String> symbols) {
        var wrapper = new LambdaQueryWrapper<AstockSpotEntity>().in(AstockSpotEntity::getSymbol, symbols);
        var entities = spotMapper.selectList(wrapper);
        return entities.stream().map(this::toSpotDO).collect(Collectors.toList());
    }

    @Override
    public List<StockDailyDO> getDaily(String symbol, java.time.LocalDate start, java.time.LocalDate end) {
        var wrapper = new LambdaQueryWrapper<AstockDailyEntity>()
                .eq(AstockDailyEntity::getSymbol, symbol)
                .between(AstockDailyEntity::getTradeDate, start, end)
                .orderByAsc(AstockDailyEntity::getTradeDate);
        var entities = dailyMapper.selectList(wrapper);
        return entities.stream().map(this::toDailyDO).collect(Collectors.toList());
    }

    @Override
    public StockDailyDO getDailySingle(String symbol, java.time.LocalDate date) {
        var wrapper = new LambdaQueryWrapper<AstockDailyEntity>()
                .eq(AstockDailyEntity::getSymbol, symbol)
                .eq(AstockDailyEntity::getTradeDate, date);
        var entity = dailyMapper.selectOne(wrapper);
        return entity == null ? null : toDailyDO(entity);
    }

    private StockSpotDO toSpotDO(AstockSpotEntity entity) {
        return StockSpotDO.builder()
                .id(entity.getId())
                .symbol(entity.getSymbol())
                .name(entity.getName())
                .price(entity.getClose() != null ? entity.getClose().doubleValue() : null)
                .changePercent(entity.getChangePercent() != null ? entity.getChangePercent().doubleValue() : null)
                .preClose(entity.getPreClose() != null ? entity.getPreClose().doubleValue() : null)
                .open(entity.getOpen() != null ? entity.getOpen().doubleValue() : null)
                .high(entity.getHigh() != null ? entity.getHigh().doubleValue() : null)
                .low(entity.getLow() != null ? entity.getLow().doubleValue() : null)
                .volume(entity.getVolume() != null ? entity.getVolume().doubleValue() : null)
                .amount(entity.getAmount() != null ? entity.getAmount().doubleValue() : null)
                .updateTime(entity.getTradeDate().atTime(15, 0))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private StockDailyDO toDailyDO(AstockDailyEntity entity) {
        return StockDailyDO.builder()
                .id(entity.getId())
                .symbol(entity.getSymbol())
                .tradeDate(entity.getTradeDate())
                .open(entity.getOpen() != null ? entity.getOpen().doubleValue() : null)
                .high(entity.getHigh() != null ? entity.getHigh().doubleValue() : null)
                .low(entity.getLow() != null ? entity.getLow().doubleValue() : null)
                .close(entity.getClose() != null ? entity.getClose().doubleValue() : null)
                .volume(entity.getVolume() != null ? entity.getVolume().doubleValue() : null)
                .amount(entity.getAmount() != null ? entity.getAmount().doubleValue() : null)
                .adjustFlag(entity.getAdjustFlag())
                .turnoverRate(entity.getTurnoverRate() != null ? entity.getTurnoverRate().doubleValue() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}