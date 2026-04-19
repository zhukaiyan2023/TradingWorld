package com.tradingworld.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradingworld.persistence.entity.BacktestResultEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 回测结果Mapper接口。
 * 继承BaseMapper提供基础的CRUD操作。
 */
@Mapper
public interface BacktestResultMapper extends BaseMapper<BacktestResultEntity> {
}
