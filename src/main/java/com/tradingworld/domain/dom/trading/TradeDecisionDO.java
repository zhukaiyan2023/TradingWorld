package com.tradingworld.domain.dom.trading;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 交易决策数据对象。
 * 记录 AI 智能体分析后做出的交易建议。
 *
 * <p>包含：
 * <ul>
 *   <li>决策结论（买入/卖出/持有）</li>
 *   <li>多头论点（支持买入的理由）</li>
 *   <li>空头论点（支持卖出的理由）</li>
 *   <li>目标价和止损价</li>
 * </ul>
 *
 * @see PositionDO 持仓信息
 * @see OrderDO 订单信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeDecisionDO implements Serializable {

    /** 主键 ID */
    private Long id;

    /** 股票代码 */
    private String symbol;

    /** 交易日期，格式：yyyy-MM-dd */
    private String tradeDate;

    /** 交易决策：BUY/SELL/HOLD */
    private String decision;

    /** 多头论点（支持买入的理由分析） */
    private String bullCase;

    /** 空头论点（支持卖出的理由分析） */
    private String bearCase;

    /** 目标价格（元） */
    private Double targetPrice;

    /** 止损价格（元） */
    private Double stopLoss;

    /** 信心度：HIGH/MEDIUM/LOW */
    private String confidence;

    /** 记录创建时间 */
    private LocalDateTime createdAt;

    /** 记录更新时间 */
    private LocalDateTime updatedAt;
}