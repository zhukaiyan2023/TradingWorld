package com.tradingworld.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 投资组合实体。
 * 对应数据库中的 portfolios 表。
 *
 * <p>字段说明：
 * <ul>
 *   <li>portfolioId - 组合唯一标识符</li>
 *   <li>name - 组合名称</li>
 *   <li>description - 组合描述</li>
 *   <li>totalValue - 组合总价值 = 现金余额 + 市值</li>
 *   <li>cashBalance - 现金余额</li>
 *   <li>marketValue - 持仓总市值</li>
 *   <li>totalPnl - 总盈亏（元）</li>
 *   <li>totalPnlPercent - 总盈亏比例（%）</li>
 * </ul>
 */
@Data
@TableName("portfolios")
public class PortfolioEntity {

    /** 主键 ID */
    @TableId(type = IdType.AUTO)
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

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
