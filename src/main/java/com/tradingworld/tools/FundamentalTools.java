package com.tradingworld.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingworld.dataflows.VendorRouter;
import com.tradingworld.dataflows.AlphaVantageVendor;
import com.tradingworld.dataflows.YFinanceVendor;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.P;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用于获取基本面金融数据的工具。
 * 通过VendorRouter使用Yahoo Finance。
 */
@Component
public class FundamentalTools {

    private static final Logger log = LoggerFactory.getLogger(FundamentalTools.class);

    private final VendorRouter vendorRouter;
    private final ObjectMapper objectMapper;

    public FundamentalTools() {
        YFinanceVendor yFinanceVendor = new YFinanceVendor();
        AlphaVantageVendor alphaVantageVendor = new AlphaVantageVendor();
        this.vendorRouter = new VendorRouter(List.of(yFinanceVendor, alphaVantageVendor));
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 获取公司的基本面数据摘要。
     */
    @Tool("Get fundamental data summary (P/E, EPS, dividend yield, etc.) for a company")
    public String getFundamentals(@P("Stock ticker symbol (e.g., NVDA)") String ticker) {
        log.debug("Fetching fundamental data for ticker: {}", ticker);
        try {
            var balanceSheet = vendorRouter.getBalanceSheet(ticker.toUpperCase());
            var incomeStmt = vendorRouter.getIncomeStatement(ticker.toUpperCase());
            var cashflow = vendorRouter.getCashflow(ticker.toUpperCase());

            StringBuilder sb = new StringBuilder();
            sb.append("{\"ticker\": \"").append(ticker.toUpperCase()).append("\", \"data\": {");

            boolean hasData = false;

            if (balanceSheet.isPresent()) {
                var bs = balanceSheet.get();
                sb.append("\"balanceSheet\": {");
                sb.append("\"totalAssets\": ").append(bs.totalAssets()).append(", ");
                sb.append("\"totalLiabilities\": ").append(bs.totalLiabilities()).append(", ");
                sb.append("\"totalEquity\": ").append(bs.totalEquity());
                sb.append("}");
                hasData = true;
            }

            if (incomeStmt.isPresent()) {
                if (hasData) sb.append(", ");
                var inc = incomeStmt.get();
                sb.append("\"incomeStatement\": {");
                sb.append("\"revenue\": ").append(inc.revenue()).append(", ");
                sb.append("\"netIncome\": ").append(inc.netIncome()).append(", ");
                sb.append("\"eps\": ").append(inc.eps());
                sb.append("}");
                hasData = true;
            }

            if (cashflow.isPresent()) {
                if (hasData) sb.append(", ");
                var cf = cashflow.get();
                sb.append("\"cashflow\": {");
                sb.append("\"operatingCashflow\": ").append(cf.operatingCashflow()).append(", ");
                sb.append("\"investingCashflow\": ").append(cf.investingCashflow()).append(", ");
                sb.append("\"financingCashflow\": ").append(cf.financingCashflow());
                sb.append("}");
            }

            sb.append("}}");
            return sb.toString();
        } catch (Exception e) {
            log.error("Error fetching fundamental data for {}: {}", ticker, e.getMessage());
            return "{\"error\": \"Failed to fetch fundamental data: " + e.getMessage() + "\"}";
        }
    }

    /**
     * 获取公司的资产负债表数据。
     */
    @Tool("Get balance sheet data (assets, liabilities, equity) for a company")
    public String getBalanceSheet(@P("Stock ticker symbol") String ticker) {
        log.debug("Fetching balance sheet for ticker: {}", ticker);
        try {
            return vendorRouter.getBalanceSheet(ticker.toUpperCase())
                    .map(bs -> String.format("""
                        {
                            "ticker": "%s",
                            "statement": "balanceSheet",
                            "totalAssets": %.2f,
                            "totalLiabilities": %.2f,
                            "totalEquity": %.2f,
                            "reportDate": "%s"
                        }
                        """,
                            bs.symbol(),
                            bs.totalAssets(),
                            bs.totalLiabilities(),
                            bs.totalEquity(),
                            bs.reportDate().toString()))
                    .orElse("{\"error\": \"Balance sheet data not available for ticker: " + ticker + "\"}");
        } catch (Exception e) {
            log.error("Error fetching balance sheet for {}: {}", ticker, e.getMessage());
            return "{\"error\": \"Failed to fetch balance sheet: " + e.getMessage() + "\"}";
        }
    }

    /**
     * 获取公司的现金流量表。
     */
    @Tool("Get cashflow statement (operating, investing, financing) for a company")
    public String getCashflow(@P("Stock ticker symbol") String ticker) {
        log.debug("Fetching cashflow for ticker: {}", ticker);
        try {
            return vendorRouter.getCashflow(ticker.toUpperCase())
                    .map(cf -> String.format("""
                        {
                            "ticker": "%s",
                            "statement": "cashflow",
                            "operatingCashflow": %.2f,
                            "investingCashflow": %.2f,
                            "financingCashflow": %.2f,
                            "reportDate": "%s"
                        }
                        """,
                            cf.symbol(),
                            cf.operatingCashflow(),
                            cf.investingCashflow(),
                            cf.financingCashflow(),
                            cf.reportDate().toString()))
                    .orElse("{\"error\": \"Cashflow data not available for ticker: " + ticker + "\"}");
        } catch (Exception e) {
            log.error("Error fetching cashflow for {}: {}", ticker, e.getMessage());
            return "{\"error\": \"Failed to fetch cashflow: " + e.getMessage() + "\"}";
        }
    }

    /**
     * 获取公司的利润表。
     */
    @Tool("Get income statement (revenue, expenses, earnings) for a company")
    public String getIncomeStatement(@P("Stock ticker symbol") String ticker) {
        log.debug("Fetching income statement for ticker: {}", ticker);
        try {
            return vendorRouter.getIncomeStatement(ticker.toUpperCase())
                    .map(is -> String.format("""
                        {
                            "ticker": "%s",
                            "statement": "incomeStatement",
                            "revenue": %.2f,
                            "netIncome": %.2f,
                            "eps": %.2f,
                            "reportDate": "%s"
                        }
                        """,
                            is.symbol(),
                            is.revenue(),
                            is.netIncome(),
                            is.eps(),
                            is.reportDate().toString()))
                    .orElse("{\"error\": \"Income statement data not available for ticker: " + ticker + "\"}");
        } catch (Exception e) {
            log.error("Error fetching income statement for {}: {}", ticker, e.getMessage());
            return "{\"error\": \"Failed to fetch income statement: " + e.getMessage() + "\"}";
        }
    }

    /**
     * 获取公司的关键统计信息。
     */
    @Tool("Get key statistics (shares outstanding, float, etc.) for a company")
    public String getKeyStatistics(@P("Stock ticker symbol") String ticker) {
        log.debug("Fetching key statistics for ticker: {}", ticker);
        // 关键统计信息需要额外的API调用或从StockQuote解析
        // 返回组合可用数据的占位符
        var quote = vendorRouter.getStockQuote(ticker.toUpperCase());
        if (quote.isEmpty()) {
            return "{\"error\": \"No data available for ticker: " + ticker + "\"}";
        }

        var q = quote.get();
        return String.format("""
            {
                "ticker": "%s",
                "currentPrice": %.2f,
                "volume": %d,
                "change": %.2f,
                "changePercent": %.2f,
                "timestamp": "%s"
            }
            """,
                q.symbol(),
                q.price(),
                q.volume(),
                q.change(),
                q.changePercent(),
                q.timestamp().toString());
    }
}
