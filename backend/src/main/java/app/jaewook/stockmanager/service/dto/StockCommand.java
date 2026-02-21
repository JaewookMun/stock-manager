package app.jaewook.stockmanager.service.dto;

import lombok.Builder;

import java.math.BigDecimal;

public class StockCommand {
    @Builder
    public record Screen(
        String exchangeType,
        BigDecimal minRoe,
        BigDecimal maxRoe,
        BigDecimal minPer,
        BigDecimal maxPer,
        int page
    ) {}
}
