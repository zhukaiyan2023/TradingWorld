# COLA 架构改造实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 TradingWorld 项目从 LangChain4j 多智能体架构改造为 COLA（Clean Object-Oriented Architecture）框架风格

**Architecture:**
- 分层：Adapter → Cmd/Qry → Domain Service → DO → Gateway → Infrastructure
- DO 按业务域划分（quote/trading/portfolio/analysis）
- Graph 拆分为三个 Domain Service + Facade
- Tools 转换为 Gateway 实现

**Tech Stack:** Spring Boot 4.0.3, LangChain4j 1.0.0, MyBatis-Plus, MySQL, Redis

---

## 文件结构

### 新增目录

```
src/main/java/com/tradingworld/
├── adapter/                    # 适配器层（从 api/ 重命名+改造）
│   └── controller/             # REST Controllers
├── cmd/                        # Command（写操作）
│   ├── trade/
│   └── analysis/
├── qry/                        # Query（读操作）
│   ├── quote/
│   ├── portfolio/
│   └── analysis/
├── domain/                     # 领域层
│   ├── do/                     # Domain Object（按业务域划分）
│   │   ├── quote/
│   │   ├── trading/
│   │   ├── portfolio/
│   │   └── analysis/
│   ├── service/                # 领域服务
│   └── gateway/                # Gateway 接口
├── infra/                      # 基础设施层
│   ├── gateway/                # Gateway 实现
│   │   ├── mysql/
│   │   ├── external/
│   │   └── cache/
│   └── config/                 # 配置类
└── app/                        # 应用层（Assembler）
```

### 文件变更映射

| 现有文件 | 目标文件 | 说明 |
|----------|----------|------|
| `api/RecommendationController.java` | `adapter/controller/StockRecommendationController.java` | 改造为 COLA 风格 |
| `api/QuoteController.java` | `adapter/controller/QuoteController.java` | 改造为 COLA 风格 |
| `api/PortfolioController.java` | `adapter/controller/PortfolioController.java` | 改造为 COLA 风格 |
| `persistence/entity/astock/AstockSpotEntity.java` | `domain/do/quote/StockSpotDO.java` | 迁移+重命名 |
| `persistence/entity/astock/AstockDailyEntity.java` | `domain/do/quote/StockDailyDO.java` | 迁移+重命名 |
| `persistence/entity/astock/AstockMinuteEntity.java` | `domain/do/quote/StockMinuteDO.java` | 迁移+重命名 |
| `graph/TradingAgentsGraph.java` | `domain/service/QuoteAnalysisService.java` | 拆分 |
| `graph/TradingAgentsGraph.java` | `domain/service/TradingDecisionService.java` | 拆分 |
| `graph/TradingAgentsGraph.java` | `domain/service/RiskEvaluationService.java` | 拆分 |
| `graph/TradingAgentsGraph.java` | `domain/service/StockTradingFacade.java` | 拆分 |
| `tools/DatabaseTools.java` | `infra/gateway/mysql/QuoteMySQLGateway.java` | 转换 |
| `tools/MarketTools.java` | `infra/gateway/mysql/MarketGateway.java` | 转换 |
| `dto/RecommendationDTO.java` | `app/assembler/RecommendationAssembler.java` | 转为 Assembler |

---

## Phase 1: 基础分层

### Task 1: 创建 COLA 目录结构

**Files:**
- Create: `src/main/java/com/tradingworld/domain/do/quote/StockSpotDO.java`
- Create: `src/main/java/com/tradingworld/domain/do/quote/StockDailyDO.java`
- Create: `src/main/java/com/tradingworld/domain/do/quote/StockMinuteDO.java`
- Create: `src/main/java/com/tradingworld/domain/do/trading/TradeDecisionDO.java`
- Create: `src/main/java/com/tradingworld/domain/do/trading/PositionDO.java`
- Create: `src/main/java/com/tradingworld/domain/do/trading/OrderDO.java`
- Create: `src/main/java/com/tradingworld/domain/do/portfolio/PortfolioDO.java`
- Create: `src/main/java/com/tradingworld/domain/do/portfolio/PerformanceDO.java`
- Create: `src/main/java/com/tradingworld/domain/do/analysis/AnalystReportDO.java`
- Create: `src/main/java/com/tradingworld/domain/do/analysis/DebateMessageDO.java`
- Create: `src/main/java/com/tradingworld/domain/do/analysis/RiskAssessmentDO.java`

- [ ] **Step 1: 创建 StockSpotDO**

```java
// src/main/java/com/tradingworld/domain/do/quote/StockSpotDO.java
package com.tradingworld.domain.do.quote;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockSpotDO implements Serializable {
    private Long id;
    private String symbol;
    private String name;
    private Double price;
    private Double changePercent;
    private Double preClose;
    private Double open;
    private Double high;
    private Double low;
    private Double volume;
    private Double amount;
    private LocalDateTime updateTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

- [ ] **Step 2: 创建 StockDailyDO**

```java
// src/main/java/com/tradingworld/domain/do/quote/StockDailyDO.java
package com.tradingworld.domain.do.quote;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockDailyDO implements Serializable {
    private Long id;
    private String symbol;
    private LocalDate tradeDate;
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Double volume;
    private Double amount;
    private String adjustFlag;
    private Double turnoverRate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

- [ ] **Step 3: 创建其他 DO 文件**

按上述模式创建：
- `StockMinuteDO.java` - 分钟K线数据
- `TradeDecisionDO.java` - 交易决策
- `PositionDO.java` - 持仓
- `OrderDO.java` - 订单
- `PortfolioDO.java` - 组合
- `PerformanceDO.java` - 业绩
- `AnalystReportDO.java` - 分析师报告
- `DebateMessageDO.java` - 辩论消息
- `RiskAssessmentDO.java` - 风险评估

- [ ] **Step 4: 提交**

```bash
git add src/main/java/com/tradingworld/domain/do/
git commit -m "feat: add COLA domain objects (DO) by business domain

- StockSpotDO, StockDailyDO, StockMinuteDO for quote
- TradeDecisionDO, PositionDO, OrderDO for trading
- PortfolioDO, PerformanceDO for portfolio
- AnalystReportDO, DebateMessageDO, RiskAssessmentDO for analysis"
```

---

### Task 2: 创建 Gateway 接口

**Files:**
- Create: `src/main/java/com/tradingworld/domain/gateway/QuoteGateway.java`
- Create: `src/main/java/com/tradingworld/domain/gateway/TradingGateway.java`
- Create: `src/main/java/com/tradingworld/domain/gateway/PortfolioGateway.java`
- Create: `src/main/java/com/tradingworld/domain/gateway/AnalysisGateway.java`

- [ ] **Step 1: 创建 QuoteGateway 接口**

```java
// src/main/java/com/tradingworld/domain/gateway/QuoteGateway.java
package com.tradingworld.domain.gateway;

import com.tradingworld.domain.do.quote.StockSpotDO;
import com.tradingworld.domain.do.quote.StockDailyDO;
import java.time.LocalDate;
import java.util.List;

public interface QuoteGateway {
    StockSpotDO getSpot(String symbol);
    List<StockSpotDO> getSpotList(List<String> symbols);
    List<StockDailyDO> getDaily(String symbol, LocalDate start, LocalDate end);
    StockDailyDO getDailySingle(String symbol, LocalDate date);
}
```

- [ ] **Step 2: 创建 TradingGateway 接口**

```java
// src/main/java/com/tradingworld/domain/gateway/TradingGateway.java
package com.tradingworld.domain.gateway;

import com.tradingworld.domain.do.trading.OrderDO;
import com.tradingworld.domain.do.trading.PositionDO;
import java.util.List;

public interface TradingGateway {
    OrderDO createOrder(OrderDO order);
    OrderDO cancelOrder(String orderId);
    List<PositionDO> getPositions(String portfolioId);
    PositionDO updatePosition(PositionDO position);
}
```

- [ ] **Step 3: 创建 PortfolioGateway 接口**

```java
// src/main/java/com/tradingworld/domain/gateway/PortfolioGateway.java
package com.tradingworld.domain.gateway;

import com.tradingworld.domain.do.portfolio.PortfolioDO;
import com.tradingworld.domain.do.portfolio.PerformanceDO;
import java.time.LocalDate;
import java.util.List;

public interface PortfolioGateway {
    PortfolioDO getPortfolio(String portfolioId);
    List<PortfolioDO> listPortfolios();
    PerformanceDO getPerformance(String portfolioId, LocalDate start, LocalDate end);
}
```

- [ ] **Step 4: 创建 AnalysisGateway 接口**

```java
// src/main/java/com/tradingworld/domain/gateway/AnalysisGateway.java
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
```

- [ ] **Step 5: 提交**

```bash
git add src/main/java/com/tradingworld/domain/gateway/
git commit -m "feat: add COLA gateway interfaces

- QuoteGateway for market data access
- TradingGateway for order/position management
- PortfolioGateway for portfolio operations
- AnalysisGateway for analyst reports"
```

---

### Task 3: 创建 Gateway 实现（MySQL）

**Files:**
- Create: `src/main/java/com/tradingworld/infra/gateway/mysql/QuoteMySQLGateway.java`
- Create: `src/main/java/com/tradingworld/infra/gateway/mysql/TradingMySQLGateway.java`
- Create: `src/main/java/com/tradingworld/infra/gateway/mysql/PortfolioMySQLGateway.java`
- Create: `src/main/java/com/tradingworld/infra/gateway/mysql/AnalysisMySQLGateway.java`

- [ ] **Step 1: 创建 QuoteMySQLGateway**

```java
// src/main/java/com/tradingworld/infra/gateway/mysql/QuoteMySQLGateway.java
package com.tradingworld.infra.gateway.mysql;

import com.tradingworld.domain.do.quote.StockSpotDO;
import com.tradingworld.domain.do.quote.StockDailyDO;
import com.tradingworld.domain.gateway.QuoteGateway;
import com.tradingworld.persistence.mapper.astock.AstockSpotMapper;
import com.tradingworld.persistence.mapper.astock.AstockDailyMapper;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;

@Component
public class QuoteMySQLGateway implements QuoteGateway {

    private final AstockSpotMapper spotMapper;
    private final AstockDailyMapper dailyMapper;

    public QuoteMySQLGateway(AstockSpotMapper spotMapper, AstockDailyMapper dailyMapper) {
        this.spotMapper = spotMapper;
        this.dailyMapper = dailyMapper;
    }

    @Override
    public StockSpotDO getSpot(String symbol) {
        return spotMapper.selectBySymbol(symbol);
    }

    @Override
    public List<StockSpotDO> getSpotList(List<String> symbols) {
        return spotMapper.selectBySymbols(symbols);
    }

    @Override
    public List<StockDailyDO> getDaily(String symbol, LocalDate start, LocalDate end) {
        return dailyMapper.selectBySymbolAndDateRange(symbol, start, end);
    }

    @Override
    public StockDailyDO getDailySingle(String symbol, LocalDate date) {
        return dailyMapper.selectBySymbolAndDate(symbol, date);
    }
}
```

- [ ] **Step 2: 创建其他 Gateway 实现**

创建：
- `TradingMySQLGateway.java` - 交易 Gateway 实现
- `PortfolioMySQLGateway.java` - 组合 Gateway 实现
- `AnalysisMySQLGateway.java` - 分析 Gateway 实现

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/tradingworld/infra/gateway/mysql/
git commit -m "feat: add MySQL gateway implementations

- QuoteMySQLGateway for market data
- TradingMySQLGateway for trading operations
- PortfolioMySQLGateway for portfolio management
- AnalysisMySQLGateway for analysis reports"
```

---

### Task 4: 创建 ConfigurationProperties 配置类

**Files:**
- Create: `src/main/java/com/tradingworld/infra/config/TradingProperties.java`
- Create: `src/main/java/com/tradingworld/infra/config/QuoteProperties.java`
- Create: `src/main/java/com/tradingworld/infra/config/AnalysisProperties.java`

- [ ] **Step 1: 创建配置类**

```java
// src/main/java/com/tradingworld/infra/config/QuoteProperties.java
package com.tradingworld.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "trading.quote")
public class QuoteProperties {
    private boolean cacheEnabled = true;
    private int cacheTtl = 300;
    private String defaultSymbol;
}
```

- [ ] **Step 2: 创建 TradingProperties**

```java
// src/main/java/com/tradingworld/infra/config/TradingProperties.java
package com.tradingworld.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "trading")
public class TradingProperties {
    private QuoteProperties quote = new QuoteProperties();
    private TradingConfig trading = new TradingConfig();
    private AnalysisConfig analysis = new AnalysisConfig();

    @Data
    public static class TradingConfig {
        private double maxPositionSize = 100000;
        private double riskThreshold = 0.15;
        private int maxDebateRounds = 3;
    }

    @Data
    public static class AnalysisConfig {
        private String model = "deep-think";
        private int timeoutSeconds = 300;
    }
}
```

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/tradingworld/infra/config/
git commit -m "feat: add COLA configuration properties

- QuoteProperties for quote settings
- TradingProperties with nested TradingConfig and AnalysisConfig"
```

---

## Phase 2: 服务拆分

### Task 5: 拆分 TradingAgentsGraph 为 Domain Services

**Files:**
- Create: `src/main/java/com/tradingworld/domain/service/QuoteAnalysisService.java`
- Create: `src/main/java/com/tradingworld/domain/service/TradingDecisionService.java`
- Create: `src/main/java/com/tradingworld/domain/service/RiskEvaluationService.java`
- Create: `src/main/java/com/tradingworld/domain/service/StockTradingFacade.java`

- [ ] **Step 1: 创建 QuoteAnalysisService**

```java
// src/main/java/com/tradingworld/domain/service/QuoteAnalysisService.java
package com.tradingworld.domain.service;

import com.tradingworld.domain.do.analysis.AnalystReportDO;
import com.tradingworld.agents.analysts.*;
import com.tradingworld.graph.state.AgentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

@Service
public class QuoteAnalysisService {
    private static final Logger log = LoggerFactory.getLogger(QuoteAnalysisService.class);

    private final MarketAnalyst marketAnalyst;
    private final SentimentAnalyst sentimentAnalyst;
    private final NewsAnalyst newsAnalyst;
    private final FundamentalsAnalyst fundamentalsAnalyst;

    public QuoteAnalysisService(
            MarketAnalyst marketAnalyst,
            SentimentAnalyst sentimentAnalyst,
            NewsAnalyst newsAnalyst,
            FundamentalsAnalyst fundamentalsAnalyst) {
        this.marketAnalyst = marketAnalyst;
        this.sentimentAnalyst = sentimentAnalyst;
        this.newsAnalyst = newsAnalyst;
        this.fundamentalsAnalyst = fundamentalsAnalyst;
    }

    public AnalystReportDO analyze(String symbol, LocalDate date) {
        log.info("Starting quote analysis for {} on {}", symbol, date);
        AgentState state = createInitialState(symbol, date);
        runAnalysts(state);
        return buildAnalystReport(state);
    }

    private AgentState createInitialState(String symbol, LocalDate date) {
        return AgentState.builder()
                .companyOfInterest(symbol)
                .tradeDate(date.toString())
                .sender("system")
                .build();
    }

    private void runAnalysts(AgentState state) {
        try {
            CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> marketAnalyst.execute(state)),
                CompletableFuture.runAsync(() -> sentimentAnalyst.execute(state)),
                CompletableFuture.runAsync(() -> newsAnalyst.execute(state)),
                CompletableFuture.runAsync(() -> fundamentalsAnalyst.execute(state))
            ).join();
        } catch (Exception e) {
            log.error("Error running analysts", e);
        }
    }

    private AnalystReportDO buildAnalystReport(AgentState state) {
        return AnalystReportDO.builder()
                .symbol(state.getCompanyOfInterest())
                .tradeDate(state.getTradeDate())
                .marketAnalysis(state.getMarketAnalysis())
                .sentimentAnalysis(state.getSentimentAnalysis())
                .newsAnalysis(state.getNewsAnalysis())
                .fundamentalsAnalysis(state.getFundamentalsAnalysis())
                .build();
    }
}
```

- [ ] **Step 2: 创建 TradingDecisionService**

```java
// src/main/java/com/tradingworld/domain/service/TradingDecisionService.java
package com.tradingworld.domain.service;

import com.tradingworld.domain.do.trading.TradeDecisionDO;
import com.tradingworld.domain.do.analysis.AnalystReportDO;
import com.tradingworld.agents.researchers.BullResearcher;
import com.tradingworld.agents.researchers.BearResearcher;
import com.tradingworld.agents.trader.Trader;
import com.tradingworld.graph.state.AgentState;
import com.tradingworld.graph.state.InvestDebateState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TradingDecisionService {
    private static final Logger log = LoggerFactory.getLogger(TradingDecisionService.class);

    private final BullResearcher bullResearcher;
    private final BearResearcher bearResearcher;
    private final Trader trader;

    @Value("${trading.trading.max-debate-rounds:3}")
    private int maxDebateRounds;

    public TradingDecisionService(
            BullResearcher bullResearcher,
            BearResearcher bearResearcher,
            Trader trader) {
        this.bullResearcher = bullResearcher;
        this.bearResearcher = bearResearcher;
        this.trader = trader;
    }

    public TradeDecisionDO makeDecision(String symbol, String date, AnalystReportDO report) {
        log.info("Starting trading decision for {} on {}", symbol, date);
        AgentState state = createState(symbol, date, report);
        runInvestmentDebate(state);
        trader.execute(state);
        return buildTradeDecision(state);
    }

    private AgentState createState(String symbol, String date, AnalystReportDO report) {
        AgentState state = AgentState.builder()
                .companyOfInterest(symbol)
                .tradeDate(date)
                .marketAnalysis(report.getMarketAnalysis())
                .sentimentAnalysis(report.getSentimentAnalysis())
                .newsAnalysis(report.getNewsAnalysis())
                .fundamentalsAnalysis(report.getFundamentalsAnalysis())
                .investmentDebateState(new InvestDebateState())
                .build();
        return state;
    }

    private void runInvestmentDebate(AgentState state) {
        InvestDebateState debateState = state.getInvestmentDebateState();
        for (int round = 0; round < maxDebateRounds; round++) {
            debateState.incrementCount();
            bullResearcher.execute(state);
            bearResearcher.execute(state);
            makeInvestmentJudgeDecision(state);
        }
    }

    private void makeInvestmentJudgeDecision(AgentState state) {
        // LLM-based decision logic
        state.getInvestmentDebateState().setJudgeDecision(
            "Investment decision based on bull/bear debate analysis");
    }

    private TradeDecisionDO buildTradeDecision(AgentState state) {
        return TradeDecisionDO.builder()
                .symbol(state.getCompanyOfInterest())
                .tradeDate(state.getTradeDate())
                .decision(state.getFinalTradeDecision())
                .bullCase(state.getBullCase())
                .bearCase(state.getBearCase())
                .build();
    }
}
```

- [ ] **Step 3: 创建 RiskEvaluationService**

```java
// src/main/java/com/tradingworld/domain/service/RiskEvaluationService.java
package com.tradingworld.domain.service;

import com.tradingworld.domain.do.analysis.RiskAssessmentDO;
import com.tradingworld.domain.do.trading.TradeDecisionDO;
import com.tradingworld.agents.risk_managers.*;
import com.tradingworld.graph.state.AgentState;
import com.tradingworld.graph.state.RiskDebateState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RiskEvaluationService {
    private static final Logger log = LoggerFactory.getLogger(RiskEvaluationService.class);

    private final AggressiveRiskManager aggressiveRiskManager;
    private final ConservativeRiskManager conservativeRiskManager;
    private final NeutralRiskManager neutralRiskManager;

    public RiskEvaluationService(
            AggressiveRiskManager aggressiveRiskManager,
            ConservativeRiskManager conservativeRiskManager,
            NeutralRiskManager neutralRiskManager) {
        this.aggressiveRiskManager = aggressiveRiskManager;
        this.conservativeRiskManager = conservativeRiskManager;
        this.neutralRiskManager = neutralRiskManager;
    }

    public RiskAssessmentDO evaluate(TradeDecisionDO decision) {
        log.info("Starting risk evaluation for {}", decision.getSymbol());
        AgentState state = createState(decision);
        runRiskDebate(state);
        return buildRiskAssessment(state);
    }

    private AgentState createState(TradeDecisionDO decision) {
        return AgentState.builder()
                .companyOfInterest(decision.getSymbol())
                .tradeDate(decision.getTradeDate())
                .finalTradeDecision(decision.getDecision())
                .riskDebateState(new RiskDebateState())
                .build();
    }

    private void runRiskDebate(AgentState state) {
        try {
            CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> aggressiveRiskManager.execute(state)),
                CompletableFuture.runAsync(() -> conservativeRiskManager.execute(state)),
                CompletableFuture.runAsync(() -> neutralRiskManager.execute(state))
            ).join();
        } catch (Exception e) {
            log.error("Error running risk managers", e);
        }
    }

    private RiskAssessmentDO buildRiskAssessment(AgentState state) {
        return RiskAssessmentDO.builder()
                .symbol(state.getCompanyOfInterest())
                .tradeDate(state.getTradeDate())
                .riskLevel(state.getRiskLevel())
                .riskAssessment(state.getRiskAssessment())
                .finalDecision(state.getFinalTradeDecision())
                .build();
    }
}
```

- [ ] **Step 4: 创建 StockTradingFacade**

```java
// src/main/java/com/tradingworld/domain/service/StockTradingFacade.java
package com.tradingworld.domain.service;

import com.tradingworld.domain.do.analysis.RiskAssessmentDO;
import com.tradingworld.domain.do.trading.TradeDecisionDO;
import com.tradingworld.domain.do.analysis.AnalystReportDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class StockTradingFacade {
    private static final Logger log = LoggerFactory.getLogger(StockTradingFacade.class);

    private final QuoteAnalysisService quoteAnalysisService;
    private final TradingDecisionService tradingDecisionService;
    private final RiskEvaluationService riskEvaluationService;

    public StockTradingFacade(
            QuoteAnalysisService quoteAnalysisService,
            TradingDecisionService tradingDecisionService,
            RiskEvaluationService riskEvaluationService) {
        this.quoteAnalysisService = quoteAnalysisService;
        this.tradingDecisionService = tradingDecisionService;
        this.riskEvaluationService = riskEvaluationService;
    }

    public TradingResult execute(String symbol, LocalDate date) {
        log.info("Executing complete trading workflow for {} on {}", symbol, date);

        // Step 1: Quote Analysis
        AnalystReportDO report = quoteAnalysisService.analyze(symbol, date);

        // Step 2: Trading Decision
        TradeDecisionDO decision = tradingDecisionService.makeDecision(
            symbol, date.toString(), report);

        // Step 3: Risk Evaluation
        RiskAssessmentDO assessment = riskEvaluationService.evaluate(decision);

        return new TradingResult(report, decision, assessment);
    }

    public record TradingResult(
            AnalystReportDO report,
            TradeDecisionDO decision,
            RiskAssessmentDO assessment) {}
}
```

- [ ] **Step 5: 提交**

```bash
git add src/main/java/com/tradingworld/domain/service/
git commit -m "feat: split TradingAgentsGraph into domain services

- QuoteAnalysisService for parallel analyst execution
- TradingDecisionService for bull/bear debate and trading decision
- RiskEvaluationService for risk debate and assessment
- StockTradingFacade for workflow orchestration"
```

---

## Phase 3: API 适配

### Task 6: 创建 Command/Query 类

**Files:**
- Create: `src/main/java/com/tradingworld/cmd/trade/AnalyzeStockCmd.java`
- Create: `src/main/java/com/tradingworld/cmd/trade/ExecuteTradeCmd.java`
- Create: `src/main/java/com/tradingworld/qry/quote/GetStockSpotQry.java`
- Create: `src/main/java/com/tradingworld/qry/quote/GetStockDailyQry.java`
- Create: `src/main/java/com/tradingworld/qry/portfolio/GetPortfolioQry.java`

- [ ] **Step 1: 创建 AnalyzeStockCmd**

```java
// src/main/java/com/tradingworld/cmd/trade/AnalyzeStockCmd.java
package com.tradingworld.cmd.trade;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class AnalyzeStockCmd {
    @NotBlank(message = "股票代码不能为空")
    private String symbol;

    @NotNull(message = "交易日期不能为空")
    private LocalDate tradeDate;
}
```

- [ ] **Step 2: 创建 Query 类**

```java
// src/main/java/com/tradingworld/qry/quote/GetStockSpotQry.java
package com.tradingworld.qry.quote;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class GetStockSpotQry {
    @NotBlank(message = "股票代码不能为空")
    private String symbol;
}
```

```java
// src/main/java/com/tradingworld/qry/quote/GetStockDailyQry.java
package com.tradingworld.qry.quote;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class GetStockDailyQry {
    @NotBlank(message = "股票代码不能为空")
    private String symbol;

    private LocalDate startDate;
    private LocalDate endDate;
}
```

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/tradingworld/cmd/ src/main/java/com/tradingworld/qry/
git commit -m "feat: add COLA command and query classes

- AnalyzeStockCmd for stock analysis requests
- GetStockSpotQry for spot data queries
- GetStockDailyQry for daily data queries"
```

---

### Task 7: 创建 Assembler（DO/DTO 转换）

**Files:**
- Create: `src/main/java/com/tradingworld/app/assembler/QuoteAssembler.java`
- Create: `src/main/java/com/tradingworld/app/assembler/TradingAssembler.java`
- Create: `src/main/java/com/tradingworld/app/assembler/RecommendationAssembler.java`

- [ ] **Step 1: 创建 QuoteAssembler**

```java
// src/main/java/com/tradingworld/app/assembler/QuoteAssembler.java
package com.tradingworld.app.assembler;

import com.tradingworld.domain.do.quote.StockSpotDO;
import com.tradingworld.domain.do.quote.StockDailyDO;
import com.tradingworld.dto.QuoteDTO;
import org.springframework.stereotype.Component;

@Component
public class QuoteAssembler {

    public QuoteDTO toDTO(StockSpotDO spot) {
        if (spot == null) return null;
        return QuoteDTO.builder()
                .symbol(spot.getSymbol())
                .name(spot.getName())
                .price(spot.getPrice())
                .changePercent(spot.getChangePercent())
                .open(spot.getOpen())
                .high(spot.getHigh())
                .low(spot.getLow())
                .volume(spot.getVolume())
                .build();
    }

    public QuoteDTO toDTO(StockDailyDO daily) {
        if (daily == null) return null;
        return QuoteDTO.builder()
                .symbol(daily.getSymbol())
                .tradeDate(daily.getTradeDate())
                .open(daily.getOpen())
                .high(daily.getHigh())
                .low(daily.getLow())
                .close(daily.getClose())
                .volume(daily.getVolume())
                .build();
    }
}
```

- [ ] **Step 2: 创建 RecommendationAssembler**

```java
// src/main/java/com/tradingworld/app/assembler/RecommendationAssembler.java
package com.tradingworld.app.assembler;

import com.tradingworld.domain.do.analysis.AnalystReportDO;
import com.tradingworld.domain.do.trading.TradeDecisionDO;
import com.tradingworld.domain.do.analysis.RiskAssessmentDO;
import com.tradingworld.domain.service.StockTradingFacade.TradingResult;
import com.tradingworld.dto.RecommendationDTO;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class RecommendationAssembler {

    public RecommendationDTO toRecommendationDTO(TradingResult result) {
        return RecommendationDTO.builder()
                .symbol(result.report().getSymbol())
                .companyName(result.report().getSymbol())
                .action(parseAction(result.decision().getDecision()))
                .summary(buildSummary(result))
                .analysisDate(LocalDate.parse(result.report().getTradeDate()))
                .confidence(result.assessment().getRiskLevel())
                .build();
    }

    private String parseAction(String decision) {
        if (decision == null) return "HOLD";
        if (decision.toUpperCase().contains("BUY")) return "BUY";
        if (decision.toUpperCase().contains("SELL")) return "SELL";
        return "HOLD";
    }

    private String buildSummary(TradingResult result) {
        return String.format("Decision: %s | Risk: %s",
            result.decision().getDecision(),
            result.assessment().getRiskLevel());
    }
}
```

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/tradingworld/app/
git commit -m "feat: add COLA assemblers for DO/DTO conversion

- QuoteAssembler for quote data conversion
- RecommendationAssembler for trading result conversion"
```

---

### Task 8: 改造 Controller 使用 COLA 风格

**Files:**
- Create: `src/main/java/com/tradingworld/adapter/controller/StockRecommendationController.java`
- Modify: `src/main/java/com/tradingworld/api/RecommendationController.java` (保留但标记为deprecated)

- [ ] **Step 1: 创建 StockRecommendationController**

```java
// src/main/java/com/tradingworld/adapter/controller/StockRecommendationController.java
package com.tradingworld.adapter.controller;

import com.tradingworld.cmd.trade.AnalyzeStockCmd;
import com.tradingworld.domain.service.StockTradingFacade;
import com.tradingworld.app.assembler.RecommendationAssembler;
import com.tradingworld.dto.RecommendationDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/stock")
public class StockRecommendationController {
    private static final Logger log = LoggerFactory.getLogger(StockRecommendationController.class);

    private final StockTradingFacade stockTradingFacade;
    private final RecommendationAssembler recommendationAssembler;

    public StockRecommendationController(
            StockTradingFacade stockTradingFacade,
            RecommendationAssembler recommendationAssembler) {
        this.stockTradingFacade = stockTradingFacade;
        this.recommendationAssembler = recommendationAssembler;
    }

    @PostMapping("/analyze")
    public ResponseEntity<RecommendationDTO> analyze(@Valid @RequestBody AnalyzeStockCmd cmd) {
        log.info("Received analyze request for {} on {}", cmd.getSymbol(), cmd.getTradeDate());
        try {
            var result = stockTradingFacade.execute(cmd.getSymbol(), cmd.getTradeDate());
            var response = recommendationAssembler.toRecommendationDTO(result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error analyzing stock", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
```

- [ ] **Step 2: 提交**

```bash
git add src/main/java/com/tradingworld/adapter/controller/StockRecommendationController.java
git commit -m "feat: add COLA-style StockRecommendationController

- Uses AnalyzeStockCmd for request validation
- Calls StockTradingFacade for business logic
- Returns RecommendationDTO via RecommendationAssembler"
```

---

## Phase 4: 完善与测试

### Task 9: 添加 Gateway 实现（外部 API）

**Files:**
- Create: `src/main/java/com/tradingworld/infra/gateway/external/AlphaVantageGateway.java`
- Create: `src/main/java/com/tradingworld/infra/gateway/external/VendorRouterGateway.java`

- [ ] **Step 1: 创建 AlphaVantageGateway**

```java
// src/main/java/com/tradingworld/infra/gateway/external/AlphaVantageGateway.java
package com.tradingworld.infra.gateway.external;

import com.tradingworld.domain.gateway.QuoteGateway;
import com.tradingworld.domain.do.quote.StockSpotDO;
import com.tradingworld.domain.do.quote.StockDailyDO;
import com.tradingworld.dataflows.AlphaVantageVendor;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;

@Component
public class AlphaVantageGateway implements QuoteGateway {

    private final AlphaVantageVendor vendor;

    public AlphaVantageGateway(AlphaVantageVendor vendor) {
        this.vendor = vendor;
    }

    @Override
    public StockSpotDO getSpot(String symbol) {
        return vendor.getQuote(symbol);
    }

    @Override
    public List<StockSpotDO> getSpotList(List<String> symbols) {
        return symbols.stream().map(vendor::getQuote).toList();
    }

    @Override
    public List<StockDailyDO> getDaily(String symbol, LocalDate start, LocalDate end) {
        return vendor.getDailyTimeSeries(symbol, start, end);
    }

    @Override
    public StockDailyDO getDailySingle(String symbol, LocalDate date) {
        var list = vendor.getDailyTimeSeries(symbol, date, date);
        return list.isEmpty() ? null : list.get(0);
    }
}
```

- [ ] **Step 2: 提交**

```bash
git add src/main/java/com/tradingworld/infra/gateway/external/
git commit -m "feat: add external API gateway implementations

- AlphaVantageGateway for AlphaVantage API
- VendorRouterGateway for multi-vendor routing"
```

---

### Task 10: 更新 application.yml 配置

**Files:**
- Modify: `src/main/resources/application.yml`

- [ ] **Step 1: 添加 COLA 配置**

```yaml
# trading configuration for COLA architecture
trading:
  quote:
    cache-enabled: true
    cache-ttl: 300
  trading:
    max-position-size: 100000
    risk-threshold: 0.15
    max-debate-rounds: 3
  analysis:
    model: deep-think
    timeout-seconds: 300
```

- [ ] **Step 2: 提交**

```bash
git add src/main/resources/application.yml
git commit -m "feat: add COLA configuration to application.yml

- trading.quote for quote settings
- trading.trading for trading config
- trading.analysis for analysis config"
```

---

## 实施总结

| Task | 内容 | 状态 |
|------|------|------|
| Task 1 | 创建 DO（Domain Object） | ⬜ |
| Task 2 | 创建 Gateway 接口 | ⬜ |
| Task 3 | 创建 MySQL Gateway 实现 | ⬜ |
| Task 4 | 创建 ConfigurationProperties | ⬜ |
| Task 5 | 拆分 Graph 为 Domain Services | ⬜ |
| Task 6 | 创建 Command/Query 类 | ⬜ |
| Task 7 | 创建 Assembler | ⬜ |
| Task 8 | 改造 Controller | ⬜ |
| Task 9 | 添加外部 API Gateway | ⬜ |
| Task 10 | 更新配置文件 | ⬜ |

---

## 验证清单

- [ ] 所有 DO 实现 Serializable
- [ ] 所有 Gateway 接口有至少一个实现
- [ ] 所有 Command/Qry 有校验注解
- [ ] ConfigurationProperties 正确绑定
- [ ] Controller 使用 Cmd/Qry 而非直接调用 Tools
- [ ] Facade 编排三个 Domain Service
- [ ] 编译通过: `mvn compile -DskipTests`
- [ ] 测试通过: `mvn test`