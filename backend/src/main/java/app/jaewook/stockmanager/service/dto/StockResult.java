package app.jaewook.stockmanager.service.dto;

import lombok.Builder;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@ToString
public class StockResult {
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
            long totalQuantity,
            BigDecimal roe,
            BigDecimal per,
            BigDecimal pbr,
            BigDecimal debtRatio,
            // 영업이익률
            BigDecimal operatingMargin,
            BigDecimal dividend
        ) {}
    }
}
