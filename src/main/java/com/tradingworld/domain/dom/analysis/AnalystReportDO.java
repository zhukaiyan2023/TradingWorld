package com.tradingworld.domain.dom.analysis;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 分析师报告数据对象。
 * 记录 AI 智能体对股票进行多维度分析后的报告。
 *
 * <p>分析维度包括：
 * <ul>
 *   <li>市场分析（技术面、资金面）</li>
 *   <li>情绪分析（市场情绪、投资者情绪）</li>
 *   <li>新闻分析（利好/利空消息）</li>
 *   <li>基本面分析（财务数据、估值）</li>
 * </ul>
 *
 * @see DebateMessageDO 辩论消息
 * @see RiskAssessmentDO 风险评估
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalystReportDO implements Serializable {

    /** 主键 ID */
    private Long id;

    /** 股票代码 */
    private String symbol;

    /** 交易日期 */
    private String tradeDate;

    /** 市场分析内容 */
    private String marketAnalysis;

    /** 情绪分析内容 */
    private String sentimentAnalysis;

    /** 新闻分析内容 */
    private String newsAnalysis;

    /** 基本面分析内容 */
    private String fundamentalsAnalysis;

    /** 综合建议：BUY/SELL/HOLD */
    private String overallRecommendation;

    /** 信心度：HIGH/MEDIUM/LOW */
    private String confidence;

    /** 报告创建时间 */
    private LocalDateTime createdAt;

    /** 报告更新时间 */
    private LocalDateTime updatedAt;
}