package com.tradingworld.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * 推荐数据传输对象。
 * 用于 API 返回股票交易推荐结果。
 *
 * @author TradingWorld
 */
@Data
public class RecommendationDTO {

    /** 股票代码 */
    private String symbol;

    /** 公司名称 */
    private String name;

    /** 交易动作：BUY/SELL/HOLD */
    private String action;

    /** 推荐摘要 */
    private String summary;

    /** 推荐日期 */
    private LocalDate date;

    /** 风险等级：HIGH/MEDIUM/LOW */
    private String riskLevel;

    public RecommendationDTO(String symbol, String name, String action, String summary, LocalDate date, String riskLevel) {
        this.symbol = symbol;
        this.name = name;
        this.action = action;
        this.summary = summary;
        this.date = date;
        this.riskLevel = riskLevel;
    }
}
