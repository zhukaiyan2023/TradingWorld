package com.tradingworld.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradingworld.persistence.entity.PositionEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PositionMapper extends BaseMapper<PositionEntity> {
}
