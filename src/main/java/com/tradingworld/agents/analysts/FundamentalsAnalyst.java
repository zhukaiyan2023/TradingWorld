package com.tradingworld.agents.analysts;

import com.tradingworld.agents.BaseAgent;
import com.tradingworld.graph.state.AgentState;
import com.tradingworld.tools.FundamentalTools;
import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基本面分析师智能体，评估公司财务状况和业绩指标。
 * 识别内在价值和潜在风险信号。
 */
public class FundamentalsAnalyst implements BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(FundamentalsAnalyst.class);

    private static final String NAME = "FundamentalsAnalyst";
    private static final String SYSTEM_PROMPT = """
        你是一位专注于财务报表分析的基本面分析师。
        你的职责是评估公司财务状况、评估内在价值，并识别资产负债表、利润表和现金流量表中的潜在危险信号。
        分析P/E比率、盈利、收入增长、利润率和负债水平。
        提供关于公司财务健康状况和估值 的见解。
        """;

    private final FundamentalTools fundamentalTools;

    public FundamentalsAnalyst(FundamentalTools fundamentalTools) {
        this.fundamentalTools = fundamentalTools;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getSystemPrompt() {
        return SYSTEM_PROMPT;
    }

    @Override
    public String execute(AgentState state) {
        log.info("Executing Fundamentals Analyst for: {}", state.getCompanyOfInterest());
        try {
            String ticker = state.getCompanyOfInterest();

            // 获取基本面数据
            String fundamentals = fundamentalTools.getFundamentals(ticker);

            // 获取资产负债表
            String balanceSheet = fundamentalTools.getBalanceSheet(ticker);

            // 获取现金流量表
            String cashflow = fundamentalTools.getCashflow(ticker);

            // 获取利润表
            String incomeStatement = fundamentalTools.getIncomeStatement(ticker);

            // 构建基本面报告
            String report = buildFundamentalsReport(ticker, fundamentals, balanceSheet, cashflow, incomeStatement);

            // 更新状态
            state.setFundamentalsReport(report);
            state.setSender(NAME);

            return report;
        } catch (Exception e) {
            log.error("Error executing Fundamentals Analyst: {}", e.getMessage(), e);
            return "Error: Failed to perform fundamental analysis - " + e.getMessage();
        }
    }

    private String buildFundamentalsReport(String ticker, String fundamentals,
            String balanceSheet, String cashflow, String incomeStatement) {
        return String.format("""
            Fundamentals Analysis Report for %s
            ===================================

            Key Fundamental Metrics:
            %s

            Balance Sheet:
            %s

            Cashflow Statement:
            %s

            Income Statement:
            %s

            Financial Analysis:
            [To be filled by LLM with interpretation of financials and valuation]
            """, ticker, fundamentals, balanceSheet, cashflow, incomeStatement);
    }

    @Tool("Get fundamental data")
    public String getFundamentals(String ticker) {
        return fundamentalTools.getFundamentals(ticker);
    }

    @Tool("Get balance sheet")
    public String getBalanceSheet(String ticker) {
        return fundamentalTools.getBalanceSheet(ticker);
    }

    @Tool("Get cashflow statement")
    public String getCashflow(String ticker) {
        return fundamentalTools.getCashflow(ticker);
    }

    @Tool("Get income statement")
    public String getIncomeStatement(String ticker) {
        return fundamentalTools.getIncomeStatement(ticker);
    }
}
