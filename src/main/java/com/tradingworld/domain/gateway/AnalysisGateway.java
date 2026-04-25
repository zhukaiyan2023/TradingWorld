package com.tradingworld.domain.gateway;

import com.tradingworld.domain.do.analysis.AnalystReportDO;
import com.tradingworld.domain.do.analysis.DebateMessageDO;
import java.time.LocalDate;
import java.util.List;

public interface AnalysisGateway {
    AnalystReportDO getReport(String symbol, LocalDate date);
    List<AnalystReportDO> getReports(String symbol, int days);
    List<DebateMessageDO> getDebateHistory(String symbol, LocalDate date);
}