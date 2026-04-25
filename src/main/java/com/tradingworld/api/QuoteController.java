package com.tradingworld.api;

import com.tradingworld.domain.dom.quote.StockSpotDO;
import com.tradingworld.domain.dom.quote.StockDailyDO;
import com.tradingworld.domain.gateway.QuoteGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

/**
 * 行情控制器。
 * 提供股票实时行情和历史 K 线数据的查询接口。
 *
 * @see StockSpotDO 股票实时行情
 * @see StockDailyDO 股票日K线数据
 * @see QuoteGateway 行情网关接口
 */
@RestController
@RequestMapping("/api/v1/quote")
public class QuoteController {

    private final QuoteGateway quoteGateway;

    public QuoteController(QuoteGateway quoteGateway) {
        this.quoteGateway = quoteGateway;
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<StockSpotDO> getQuote(@PathVariable String symbol) {
        var quote = quoteGateway.getSpot(symbol);
        if (quote == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(quote);
    }

    @GetMapping("/batch")
    public ResponseEntity<List<StockSpotDO>> getQuotes(@RequestParam List<String> symbols) {
        var quotes = quoteGateway.getSpotList(symbols);
        return ResponseEntity.ok(quotes);
    }

    @GetMapping("/{symbol}/daily")
    public ResponseEntity<List<StockDailyDO>> getDaily(
            @PathVariable String symbol,
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {
        var daily = quoteGateway.getDaily(symbol, start, end);
        return ResponseEntity.ok(daily);
    }

    @GetMapping("/{symbol}/daily/{date}")
    public ResponseEntity<StockDailyDO> getDailySingle(
            @PathVariable String symbol,
            @PathVariable LocalDate date) {
        var daily = quoteGateway.getDailySingle(symbol, date);
        if (daily == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(daily);
    }
}
