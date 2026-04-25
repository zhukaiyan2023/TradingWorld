package com.tradingworld.infra.gateway.mysql;

import com.tradingworld.domain.do.analysis.AnalystReportDO;
import com.tradingworld.domain.do.analysis.DebateMessageDO;
import com.tradingworld.domain.gateway.AnalysisGateway;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Component
public class AnalysisMySQLGateway implements AnalysisGateway {

    @Override
    public AnalystReportDO getReport(String symbol, LocalDate date) {
        // TODO: Implement with AnalysisReportMapper
        return null;
    }

    @Override
    public List<AnalystReportDO> getReports(String symbol, int days) {
        // TODO: Implement with AnalysisReportMapper
        return Collections.emptyList();
    }

    @Override
    public List<DebateMessageDO> getDebateHistory(String symbol, LocalDate date) {
        // TODO: Implement with DebateMessageMapper
        return Collections.emptyList();
    }
}