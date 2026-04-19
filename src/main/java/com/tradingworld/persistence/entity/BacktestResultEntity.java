package com.tradingworld.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 回测结果实体类。
 * 对应数据库中的backtest_results表。
 */
@Data
@TableName("backtest_results")
public class BacktestResultEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 股票代码
     */
    private String symbol;

    /**
     * 回测开始日期
     */
    private LocalDate startDate;

    /**
     * 回测结束日期
     */
    private LocalDate endDate;

    /**
     * 初始资金
     */
    private Double initialCapital;

    /**
     * 最终价值
     */
    private Double finalValue;

    /**
     * 总收益率（百分比）
     */
    private Double totalReturn;

    /**
     * 年化收益率（百分比）
     */
    private Double annualizedReturn;

    /**
     * 夏普比率
     */
    private Double sharpeRatio;

    /**
     * 最大回撤（百分比）
     */
    private Double maxDrawdown;

    /**
     * 胜率（百分比）
     */
    private Double winRate;

    /**
     * 盈亏比
     */
    private Double profitLossRatio;

    /**
     * 总交易次数
     */
    private Integer totalTrades;

    /**
     * 盈利交易次数
     */
    private Integer winningTrades;

    /**
     * 回测执行时间（毫秒）
     */
    private Long executionTimeMs;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 回测状态：SUCCESS, FAILED, RUNNING
     */
    private String status;

    /**
     * 错误信息（如果失败）
     */
    private String errorMessage;
}
