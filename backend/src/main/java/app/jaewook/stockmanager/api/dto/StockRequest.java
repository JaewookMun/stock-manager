package app.jaewook.stockmanager.api.dto;

public class StockRequest {
    public record Screen(
        // 거래소 유형
        String exchangeType
    ) {}
}
