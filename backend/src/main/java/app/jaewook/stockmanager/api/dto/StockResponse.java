package app.jaewook.stockmanager.api.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

public class StockResponse {
    @Builder
    public record Screen(
        long totalCount,
        List<StockDto> items,
        boolean hasNext
    ) {
        @Builder
        public record StockDto(
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

    @Builder
    public record Conditions(
        int resultCode,
        String resultMessage,
        List<ConditionItem> items
    ) {
        @Builder
        public record ConditionItem(
            String conditionSeq,
            String conditionName
        ) {}
    }
}
