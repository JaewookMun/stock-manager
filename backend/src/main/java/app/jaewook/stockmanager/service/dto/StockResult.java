package app.jaewook.stockmanager.service.dto;

import lombok.Builder;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@ToString
public class StockResult {
    @Builder
    public record Conditions(
        int resultCode,
        String resultMessage,
        List<ConditionInfo> items
    ) {
        @Builder
        public record ConditionInfo(
            String conditionSeq,
            String conditionName
        ) {}
    }
    @Builder
    public record Screen(
        long totalCount,
        List<StockInfo> items,
        boolean hasNext
    ) {
        @Builder
        public record StockInfo(
            String exchangeType,
            String code,
            String name,
            BigDecimal price,
            BigDecimal marketCap,
            long totalQuantity,
            BigDecimal roe,
            BigDecimal per,
            BigDecimal pbr,
            BigDecimal eps,
            BigDecimal bps,
            BigDecimal operatingProfit,
            BigDecimal salesAmount
        ) {}
    }
}
