package com.tradingworld.domain.dom.portfolio;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 组合业绩表现数据对象。
 * 记录投资组合在特定时间段的业绩指标。
 *
 * <p>业绩指标包括：
 * <ul>
 *   <li>收益率（日/周/月/年）</li>
 *   <li>夏普比率（风险调整收益）</li>
 *   <li>最大回撤</li>
 * </ul>
 *
 * @see PortfolioDO 投资组合
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceDO implements Serializable {

    /** 主键 ID */
    private Long id;

    /** 投资组合 ID */
    private String portfolioId;

    /** 报告日期 */
    private LocalDate reportDate;

    /** 总收益率（%） */
    private Double totalReturn;

    /** 日收益率（%） */
    private Double dailyReturn;

    /** 周收益率（%） */
    private Double weeklyReturn;

    /** 月收益率（%） */
    private Double monthlyReturn;

    /** 年化收益率（%） */
    private Double annualReturn;

    /** 夏普比率（风险调整收益指标） */
    private Double sharpeRatio;

    /** 最大回撤（%） */
    private Double maxDrawdown;

    /** 记录创建时间 */
    private LocalDateTime createdAt;

    /** 记录更新时间 */
    private LocalDateTime updatedAt;
}