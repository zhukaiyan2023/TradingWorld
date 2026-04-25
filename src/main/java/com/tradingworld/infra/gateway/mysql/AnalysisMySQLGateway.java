package com.tradingworld.infra.gateway.mysql;

import com.tradingworld.domain.dom.analysis.AnalystReportDO;
import com.tradingworld.domain.dom.analysis.DebateMessageDO;
import com.tradingworld.domain.gateway.AnalysisGateway;
import com.tradingworld.persistence.entity.AnalysisReportEntity;
import com.tradingworld.persistence.mapper.AnalysisReportMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MySQL 分析网关实现。
 * 通过 MyBatis-Plus 访问 MySQL 数据库获取分析师报告数据。
 *
 * @see AnalysisGateway 分析网关接口
 * @see AnalysisReportEntity 分析报告实体
 */
@Component
public class AnalysisMySQLGateway implements AnalysisGateway {

    private final AnalysisReportMapper reportMapper;

    public AnalysisMySQLGateway(AnalysisReportMapper reportMapper) {
        this.reportMapper = reportMapper;
    }

    @Override
    public AnalystReportDO getReport(String symbol, LocalDate date) {
        var wrapper = new LambdaQueryWrapper<AnalysisReportEntity>()
                .eq(AnalysisReportEntity::getSymbol, symbol)
                .eq(AnalysisReportEntity::getAnalysisDate, date);
        var entity = reportMapper.selectOne(wrapper);
        return entity == null ? null : toReportDO(entity);
    }

    @Override
    public List<AnalystReportDO> getReports(String symbol, int days) {
        LocalDate startDate = LocalDate.now().minusDays(days);
        var wrapper = new LambdaQueryWrapper<AnalysisReportEntity>()
                .eq(AnalysisReportEntity::getSymbol, symbol)
                .ge(AnalysisReportEntity::getAnalysisDate, startDate)
                .orderByDesc(AnalysisReportEntity::getAnalysisDate);
        var entities = reportMapper.selectList(wrapper);
        return entities.stream().map(this::toReportDO).collect(Collectors.toList());
    }

    @Override
    public List<DebateMessageDO> getDebateHistory(String symbol, LocalDate date) {
        return List.of();
    }

    private AnalystReportDO toReportDO(AnalysisReportEntity entity) {
        return AnalystReportDO.builder()
                .id(entity.getId())
                .symbol(entity.getSymbol())
                .tradeDate(entity.getAnalysisDate() != null ? entity.getAnalysisDate().toString() : null)
                .marketAnalysis(entity.getContent())
                .overallRecommendation(entity.getReportType())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
