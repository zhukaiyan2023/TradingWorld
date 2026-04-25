package com.tradingworld.persistence.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tradingworld.persistence.entity.AnalysisReportEntity;
import com.tradingworld.persistence.entity.BacktestResultEntity;
import com.tradingworld.persistence.entity.TradeRecordEntity;
import com.tradingworld.persistence.mapper.AnalysisReportMapper;
import com.tradingworld.persistence.mapper.BacktestResultMapper;
import com.tradingworld.persistence.mapper.TradeRecordMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据持久化服务类。
 * 提供交易记录、分析报告和回测结果的保存和查询功能。
 */
@Service
public class PersistenceService {

    private final TradeRecordMapper tradeRecordMapper;
    private final AnalysisReportMapper analysisReportMapper;
    private final BacktestResultMapper backtestResultMapper;

    public PersistenceService(
            TradeRecordMapper tradeRecordMapper,
            AnalysisReportMapper analysisReportMapper,
            BacktestResultMapper backtestResultMapper) {
        this.tradeRecordMapper = tradeRecordMapper;
        this.analysisReportMapper = analysisReportMapper;
        this.backtestResultMapper = backtestResultMapper;
    }

    // ==================== 交易记录操作 ====================

    /**
     * 保存交易记录
     */
    public TradeRecordEntity saveTradeRecord(TradeRecordEntity record) {
        record.setCreatedAt(LocalDateTime.now());
        tradeRecordMapper.insert(record);
        return record;
    }

    /**
     * 批量保存交易记录
     */
    public void saveTradeRecords(List<TradeRecordEntity> records) {
        for (TradeRecordEntity record : records) {
            record.setCreatedAt(LocalDateTime.now());
            tradeRecordMapper.insert(record);
        }
    }

    /**
     * 查询某股票的所有交易记录
     */
    public List<TradeRecordEntity> getTradeRecordsBySymbol(String symbol) {
        QueryWrapper<TradeRecordEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("symbol", symbol);
        wrapper.orderByDesc("trade_date");
        return tradeRecordMapper.selectList(wrapper);
    }

    /**
     * 查询某日期范围的交易记录
     */
    public List<TradeRecordEntity> getTradeRecordsByDateRange(LocalDate startDate, LocalDate endDate) {
        QueryWrapper<TradeRecordEntity> wrapper = new QueryWrapper<>();
        wrapper.between("trade_date", startDate, endDate);
        wrapper.orderByDesc("trade_date");
        return tradeRecordMapper.selectList(wrapper);
    }

    /**
     * 根据回测结果ID查询交易记录
     */
    public List<TradeRecordEntity> getTradeRecordsByBacktestId(Long backtestResultId) {
        QueryWrapper<TradeRecordEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("backtest_result_id", backtestResultId);
        wrapper.orderByDesc("trade_date");
        return tradeRecordMapper.selectList(wrapper);
    }

    // ==================== 分析报告操作 ====================

    /**
     * 保存分析报告
     */
    public AnalysisReportEntity saveAnalysisReport(AnalysisReportEntity report) {
        report.setCreatedAt(LocalDateTime.now());
        analysisReportMapper.insert(report);
        return report;
    }

    /**
     * 查询某股票的分析报告
     */
    public List<AnalysisReportEntity> getAnalysisReportsBySymbol(String symbol) {
        QueryWrapper<AnalysisReportEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("symbol", symbol);
        wrapper.orderByDesc("analysis_date");
        return analysisReportMapper.selectList(wrapper);
    }

    /**
     * 查询某日期的分析报告
     */
    public List<AnalysisReportEntity> getAnalysisReportsByDate(LocalDate date) {
        QueryWrapper<AnalysisReportEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("analysis_date", date);
        wrapper.orderByDesc("created_at");
        return analysisReportMapper.selectList(wrapper);
    }

    /**
     * 查询某类型的分析报告
     */
    public List<AnalysisReportEntity> getAnalysisReportsByType(String reportType) {
        QueryWrapper<AnalysisReportEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("report_type", reportType);
        wrapper.orderByDesc("analysis_date");
        return analysisReportMapper.selectList(wrapper);
    }

    // ==================== 回测结果操作 ====================

    /**
     * 保存回测结果
     */
    public BacktestResultEntity saveBacktestResult(BacktestResultEntity result) {
        result.setCreatedAt(LocalDateTime.now());
        backtestResultMapper.insert(result);
        return result;
    }

    /**
     * 更新回测结果
     */
    public void updateBacktestResult(BacktestResultEntity result) {
        backtestResultMapper.updateById(result);
    }

    /**
     * 查询某股票的回测结果
     */
    public List<BacktestResultEntity> getBacktestResultsBySymbol(String symbol) {
        QueryWrapper<BacktestResultEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("symbol", symbol);
        wrapper.orderByDesc("created_at");
        return backtestResultMapper.selectList(wrapper);
    }

    /**
     * 查询最新的回测结果
     */
    public BacktestResultEntity getLatestBacktestResult(String symbol) {
        QueryWrapper<BacktestResultEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("symbol", symbol);
        wrapper.eq("status", "SUCCESS");
        wrapper.orderByDesc("created_at");
        wrapper.last("LIMIT 1");
        return backtestResultMapper.selectOne(wrapper);
    }

    /**
     * 查询所有回测结果
     */
    public List<BacktestResultEntity> getAllBacktestResults() {
        QueryWrapper<BacktestResultEntity> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("created_at");
        return backtestResultMapper.selectList(wrapper);
    }

    /**
     * 删除回测结果
     */
    public void deleteBacktestResult(Long id) {
        backtestResultMapper.deleteById(id);
    }
}
