package com.tradingworld.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 分析报告实体类。
 * 对应数据库中的analysis_reports表。
 */
@Data
@TableName("analysis_reports")
public class AnalysisReportEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 股票代码
     */
    private String symbol;

    /**
     * 报告类型：MARKET, SENTIMENT, NEWS, FUNDAMENTALS
     */
    private String reportType;

    /**
     * 报告内容（JSON格式）
     */
    private String content;

    /**
     * 分析日期
     */
    private LocalDate analysisDate;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 执行时间（毫秒）
     */
    private Long executionTimeMs;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 错误信息（如果失败）
     */
    private String errorMessage;
}
