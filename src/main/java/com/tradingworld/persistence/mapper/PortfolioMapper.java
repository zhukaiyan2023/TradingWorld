package com.tradingworld.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradingworld.persistence.entity.PortfolioEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PortfolioMapper extends BaseMapper<PortfolioEntity> {
}
