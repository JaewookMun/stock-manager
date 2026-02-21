package app.jaewook.stockmanager.api.dto;

import java.math.BigDecimal;

public class StockRequest {
    public record Screen(
        String exchangeType,
        BigDecimal minRoe,
        BigDecimal maxRoe,
        BigDecimal minPer,
        BigDecimal maxPer,
        int page
    ) {}
}
