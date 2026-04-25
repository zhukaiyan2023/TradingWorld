# COLA 架构改造设计文档

## 概述

将 TradingWorld 项目从当前 LangChain4j 多智能体架构改造为 COLA（Clean Object-Oriented Architecture）框架风格。

## 当前架构

```
Controller → Service → Tools/Database
                  ↓
           TradingAgentsGraph（多智能体编排）
                  ↓
           Dataflows（外部数据源）
```

## 目标架构

```
Controller → Cmd/Qry → Domain Service → DO
                                    ↓
                              Gateway（外部服务）
                                    ↓
                              Infrastructure（Tools/Adapter）
```

---

## 1. 整体分层

### 1.1 分层结构

```
src/main/java/com/tradingworld/
├── adapter/                    # 适配器层（Controller/CLI）
├── cmd/                        # Command（写操作）
├── qry/                        # Query（读操作）
├── domain/                     # 领域层
│   ├── do/                     # Domain Object（按业务域划分）
│   ├── service/                # 领域服务
│   └── gateway/                # Gateway 接口
├── infra/                      # 基础设施层
│   ├── gateway/                # Gateway 实现
│   ├── tools/                  # 工具类
│   └── config/                 # 配置类
└── app/                        # 应用层（Assembler/DTO转换）
```

### 1.2 模块划分

| 业务域 | 包路径 | 说明 |
|--------|--------|------|
| Quote（报价） | `domain/do/quote/` | 股票行情、K线数据 |
| Trading（交易） | `domain/do/trading/` | 交易决策、持仓 |
| Portfolio（组合） | `domain/do/portfolio/` | 投资组合、收益 |
| Analysis（分析） | `domain/do/analysis/` | 分析报告、辩论 |

---

## 2. DO 设计（Domain Object）

### 2.1 按业务域划分

```
domain/do/
├── quote/
│   ├── StockDailyDO.java       # 日K数据
│   ├── StockSpotDO.java        # 实时行情
│   └── StockMinuteDO.java      # 分钟数据
├── trading/
│   ├── TradeDecisionDO.java    # 交易决策
│   ├── PositionDO.java         # 持仓
│   └── OrderDO.java            # 订单
├── portfolio/
│   ├── PortfolioDO.java        # 组合
│   └── PerformanceDO.java      # 业绩
└── analysis/
    ├── AnalystReportDO.java    # 分析师报告
    ├── DebateMessageDO.java    # 辩论消息
    └── RiskAssessmentDO.java   # 风险评估
```

### 2.2 DO 规范

- DO 必须与数据库表一一对应
- 使用 Lombok 的 `@Data`、`@Builder`
- 实现 `Serializable`
- 包含 `id`、`createdAt`、`updatedAt` 通用字段

---

## 3. Gateway 设计

### 3.1 Gateway 接口（domain/gateway/）

```java
public interface QuoteGateway {
    StockSpotDO getSpot(String symbol);
    List<StockDailyDO> getDaily(String symbol, Date start, Date end);
}

public interface AnalysisGateway {
    AnalystReportDO analyze(String symbol, Date date);
}
```

### 3.2 Gateway 实现（infra/gateway/）

```
infra/gateway/
├── mysql/                      # MySQL 实现
│   ├── QuoteMySQLGateway.java
│   └── PortfolioMySQLGateway.java
├── external/                   # 外部 API 实现
│   ├── AlphaVantageGateway.java
│   └── VendorRouterGateway.java
└── cache/                      # Redis 实现
    └── StockCacheGateway.java
```

---

## 4. Command/Query 设计

### 4.1 场景化分离原则

- **交易下单** → `TradeCmd`（写）
- **股票查询** → `StockQry`（读）
- **组合查询** → `PortfolioQry`（读）
- **简单查询** → 保持现有方式，不强制拆分

### 4.2 Cmd 包结构

```
cmd/
├── trade/
│   ├── ExecuteTradeCmd.java    # 执行交易
│   └── CancelOrderCmd.java     # 取消订单
└── analysis/
    └── AnalyzeStockCmd.java    # 分析股票
```

### 4.3 Qry 包结构

```
qry/
├── quote/
│   ├── GetStockSpotQry.java    # 查询实时行情
│   ├── GetStockDailyQry.java   # 查询日K线
│   └── GetStockListQry.java     # 查询股票列表
├── portfolio/
│   ├── GetPortfolioQry.java    # 查询组合
│   └── GetPositionsQry.java    # 查询持仓
└── analysis/
    └── GetAnalysisReportQry.java
```

---

## 5. Domain Service 设计

### 5.1 Graph 拆分方案

将 `TradingAgentsGraph` 拆分为三个领域服务：

```java
// Quote Analysis Service - 负责分析师并行运行
@Service
public class QuoteAnalysisService {
    AnalystReportDO analyze(String symbol, Date date);
}

// Trading Decision Service - 负责多空辩论与交易决策
@Service
public class TradingDecisionService {
    TradeDecisionDO makeDecision(String symbol, Date date);
}

// Risk Evaluation Service - 负责风险辩论与评估
@Service
public class RiskEvaluationService {
    RiskAssessmentDO evaluate(TradeDecisionDO decision);
}
```

### 5.2 服务编排

```
QuoteAnalysisService
       ↓
TradingDecisionService（依赖 QuoteAnalysisService）
       ↓
RiskEvaluationService（依赖 TradingDecisionService）
```

### 5.3 Facade 编排

```java
// StockTradingFacade - 编排完整工作流程
@Service
public class StockTradingFacade {
    public TradingResult execute(String symbol, Date date) {
        // 调用三个服务完成完整流程
    }
}
```

---

## 6. Tools → Gateway 转换

### 6.1 转换映射

| 现有 Tool | Gateway 实现 |
|-----------|--------------|
| MarketTools | `MarketGateway` |
| TechnicalTools | `TechnicalGateway` |
| FundamentalTools | `FundamentalGateway` |
| DatabaseTools | `QuoteMySQLGateway` |
| NewsTools | `NewsGateway` |

### 6.2 实现方式

```java
// infra/gateway/mysql/MarketGateway.java
@ола
public class MarketGateway implements domain.gateway.MarketGateway {
    @Autowired
    private StockSpotMapper stockSpotMapper;

    @Override
    public StockSpotDO getSpot(String symbol) {
        return stockSpotMapper.selectBySymbol(symbol);
    }
}
```

---

## 7. 配置管理

### 7.1 配置结构

```yaml
# application.yml
trading:
  quote:
    cache-enabled: true
    cache-ttl: 300
  trading:
    max-position-size: 100000
    risk-threshold: 0.15
  analysis:
    model: deep-think
```

### 7.2 ConfigurationProperties 绑定

```java
@ConfigurationProperties(prefix = "trading")
public class TradingProperties {
    private QuoteProperties quote = new QuoteProperties();
    private TradingProperties trading = new TradingProperties();
    private AnalysisProperties analysis = new AnalysisProperties();
}
```

---

## 8. API 适配层

### 8.1 Controller 改造

```java
// adapter/controller/StockController.java
@RestController
public class StockController {

    @PostMapping("/api/stock/analyze")
    public AnalyzeStockResponse analyze(@RequestBody AnalyzeStockCmd cmd) {
        // 1. Cmd 校验
        // 2. 调用 StockTradingFacade
        // 3. 返回 Response
    }
}
```

### 8.2 Response 规范

```java
public class AnalyzeStockResponse {
    private String symbol;
    private String decision;
    private BigDecimal targetPrice;
    private String riskLevel;
}
```

---

## 9. 实施步骤

### Phase 1: 基础分层（第1-2周）
1. 创建 `domain/`、`cmd/`、`qry/`、`infra/` 包结构
2. 定义第一批 Gateway 接口
3. 迁移 Quote 相关 DO 和 Gateway

### Phase 2: 服务拆分（第3-4周）
1. 拆分 `TradingAgentsGraph` 为三个 Domain Service
2. 实现 Gateway 实现类（MySQL/外部API）
3. 创建 StockTradingFacade 编排服务

### Phase 3: API 适配（第5周）
1. 改造 Controller 使用 Cmd/Qry
2. 实现 Assembler 进行 DO/DTO 转换
3. 配置Spring Boot ConfigurationProperties

### Phase 4: 完善与测试（第6周）
1. 完成所有 DO、Gateway 定义
2. 单元测试与集成测试
3. 文档更新

---

## 10. 风险与注意事项

1. **Graph 拆分风险**：多智能体工作流程有状态依赖，需小心设计接口
2. **测试覆盖**：全量改造需要完整的测试覆盖
3. **过渡期**：新旧代码并行时保持接口兼容
4. **性能考虑**：Gateway 层可能引入额外调用开销，需做性能测试

---

## 设计决策总结

| 决策项 | 选择 |
|--------|------|
| DO 划分 | 按业务域（quote/trading/portfolio/analysis） |
| Cmd/Qry | 场景化分离（按需拆分） |
| Graph 处理 | 拆分为三个 Domain Service + Facade |
| Tools 处理 | 转换为 Gateway 实现 |
| 配置管理 | YAML + @ConfigurationProperties |