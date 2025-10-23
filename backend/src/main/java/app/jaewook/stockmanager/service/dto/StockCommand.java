package app.jaewook.stockmanager.service.dto;

import lombok.Builder;

public class StockCommand {
    @Builder
    public record Screen(
        String exchangeType
    ) {}
}
