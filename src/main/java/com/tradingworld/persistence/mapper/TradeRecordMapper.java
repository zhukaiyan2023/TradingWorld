package com.tradingworld.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradingworld.persistence.entity.TradeRecordEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 交易记录Mapper接口。
 * 继承BaseMapper提供基础的CRUD操作。
 */
@Mapper
public interface TradeRecordMapper extends BaseMapper<TradeRecordEntity> {
}
