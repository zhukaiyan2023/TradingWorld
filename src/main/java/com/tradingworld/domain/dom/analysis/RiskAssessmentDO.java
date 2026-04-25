package com.tradingworld.domain.dom.analysis;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 风险评估数据对象。
 * 记录风险评估智能体对交易决策的风险判断。
 *
 * <p>风险等级：
 * <ul>
 *   <li>HIGH - 高风险，建议轻仓或回避</li>
 *   <li>MEDIUM - 中等风险，可适量参与</li>
 *   <li>LOW - 低风险，可正常参与</li>
 * </ul>
 *
 * @see AnalystReportDO 分析师报告
 * @see TradeDecisionDO 交易决策
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskAssessmentDO implements Serializable {

    /** 主键 ID */
    private Long id;

    /** 股票代码 */
    private String symbol;

    /** 交易日期 */
    private String tradeDate;

    /** 风险等级：HIGH/MEDIUM/LOW */
    private String riskLevel;

    /** 风险评估详细说明 */
    private String riskAssessment;

    /** 风险因素分析 */
    private String riskFactors;

    /** 最大亏损比例（%） */
    private Double maxLossPercent;

    /** 建议持仓比例（%） */
    private Double recommendedPositionSize;

    /** 最终决策建议 */
    private String finalDecision;

    /** 评估时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}