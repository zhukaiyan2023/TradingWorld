package com.tradingworld.domain.gateway;

import com.tradingworld.domain.dom.analysis.AnalystReportDO;
import com.tradingworld.domain.dom.analysis.DebateMessageDO;
import java.time.LocalDate;
import java.util.List;

/**
 * 分析网关接口。
 * 定义分析师报告和辩论历史的查询能力。
 *
 * @see AnalystReportDO 分析师报告
 * @see DebateMessageDO 辩论消息
 */
public interface AnalysisGateway {

    /**
     * 获取指定日期的分析师报告。
     *
     * @param symbol 股票代码
     * @param date   交易日期
     * @return 分析师报告，若不存在返回 null
     */
    AnalystReportDO getReport(String symbol, LocalDate date);

    /**
     * 获取指定天数内的分析师报告列表。
     *
     * @param symbol 股票代码
     * @param days   天数
     * @return 分析师报告列表
     */
    List<AnalystReportDO> getReports(String symbol, int days);

    /**
     * 获取指定日期的辩论历史。
     *
     * @param symbol 股票代码
     * @param date   交易日期
     * @return 辩论消息列表
     */
    List<DebateMessageDO> getDebateHistory(String symbol, LocalDate date);
}