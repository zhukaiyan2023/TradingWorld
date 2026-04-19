package com.tradingworld.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 交易记录实体类。
 * 对应数据库中的trade_records表。
 */
@Data
@TableName("trade_records")
public class TradeRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 股票代码
     */
    private String symbol;

    /**
     * 交易动作：BUY 或 SELL
     */
    private String action;

    /**
     * 交易数量
     */
    private Integer quantity;

    /**
     * 交易价格
     */
    private Double price;

    /**
     * 交易总金额
     */
    private Double totalAmount;

    /**
     * 交易日期
     */
    private LocalDate tradeDate;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 关联的回测结果ID（如果有）
     */
    private Long backtestResultId;

    /**
     * 备注
     */
    private String remark;
}
