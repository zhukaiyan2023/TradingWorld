package com.tradingworld.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tradingworld.persistence.entity.AnalysisReportEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分析报告Mapper接口。
 * 继承BaseMapper提供基础的CRUD操作。
 */
@Mapper
public interface AnalysisReportMapper extends BaseMapper<AnalysisReportEntity> {
}
