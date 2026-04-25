package com.tradingworld.domain.dom.analysis;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 辩论消息数据对象。
 * 记录多空双方在投资辩论过程中的发言。
 *
 * <p>辩论流程：
 * <ul>
 *   <li>多头研究员（BULL）提出买入论点</li>
 *   <li>空头研究员（BEAR）提出卖出论点</li>
 *   <li>经过多轮辩论后形成最终决策</li>
 * </ul>
 *
 * @see AnalystReportDO 分析师报告
 * @see TradeDecisionDO 交易决策
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebateMessageDO implements Serializable {

    /** 主键 ID */
    private Long id;

    /** 股票代码 */
    private String symbol;

    /** 交易日期 */
    private String tradeDate;

    /** 辩论轮次，如 "Round 1" */
    private String debateRound;

    /** 发言者：BULL-多头研究员, BEAR-空头研究员 */
    private String speaker;

    /** 立场：BULLISH-看多, BEARISH-看空 */
    private String perspective;

    /** 发言内容 */
    private String content;

    /** 发言时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}