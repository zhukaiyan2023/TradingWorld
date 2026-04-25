package com.tradingworld.domain.dom.portfolio;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 投资组合数据对象。
 * 记录一个投资组合的整体信息。
 *
 * <p>包含：
 * <ul>
 *   <li>组合价值和现金余额</li>
 *   <li>市场价值和盈亏情况</li>
 *   <li>组合基本描述</li>
 * </ul>
 *
 * @see PerformanceDO 组合业绩表现
 * @see PositionDO 持仓信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioDO implements Serializable {

    /** 主键 ID */
    private Long id;

    /** 投资组合唯一标识符 */
    private String portfolioId;

    /** 投资组合名称 */
    private String name;

    /** 投资组合描述 */
    private String description;

    /** 组合总价值（元）= 现金余额 + 市值 */
    private Double totalValue;

    /** 现金余额（元） */
    private Double cashBalance;

    /** 持仓总市值（元） */
    private Double marketValue;

    /** 总盈亏（元），正值盈利，负值亏损 */
    private Double totalPnl;

    /** 总盈亏比例（%），正值盈利，负值亏损 */
    private Double totalPnlPercent;

    /** 记录创建时间 */
    private LocalDateTime createdAt;

    /** 记录更新时间 */
    private LocalDateTime updatedAt;
}