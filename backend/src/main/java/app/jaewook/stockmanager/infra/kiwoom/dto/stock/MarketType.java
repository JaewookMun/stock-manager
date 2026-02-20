package app.jaewook.stockmanager.infra.kiwoom.dto.stock;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 시장구분 (0:코스피, 10:코스닥, 3:ELW, 8:ETF, 30:K-OTC, 50:코넥스 등)
 */
@Getter
@RequiredArgsConstructor
public enum MarketType {
    KOSPI("0", "코스피"),
    KOSDAQ("10", "코스닥"),
    ELW("3", "ELW"),
    ETF("8", "ETF"),
    K_OTC("30", "K-OTC"),
    KONEX("50", "코넥스");

    @JsonValue
    private final String code;
    private final String description;
}
