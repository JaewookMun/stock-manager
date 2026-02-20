package app.jaewook.stockmanager.infra.kiwoom.dto.stock;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 키움 REST API - 종목정보 리스트 (ka10099) 응답 DTO
 */
public record KiwoomStockInfoResponse(
    @JsonProperty("return_code")
    int resultCode,             // 결과코드 (0: 정상)

    @JsonProperty("return_msg")
    String message,             // 메시지

    @JsonProperty("list")
    List<StockInfoItem> list    // 종목정보 목록
) {
    public record StockInfoItem(
        @JsonProperty("code")
        String code,                    // 종목코드

        @JsonProperty("name")
        String name,                    // 종목명

        @JsonProperty("listCount")
        String listCount,               // 상장주식수

        @JsonProperty("auditInfo")
        String auditInfo,               // 감리구분

        @JsonProperty("regDay")
        String regDay,                  // 상장일

        @JsonProperty("lastPrice")
        String lastPrice,               // 전일종가

        @JsonProperty("state")
        String state,                   // 종목상태

        @JsonProperty("marketCode")
        String marketCode,              // 시장구분코드

        @JsonProperty("marketName")
        String marketName,              // 시장구분명

        @JsonProperty("upName")
        String upName,                  // 업종명

        @JsonProperty("upSizeName")
        String upSizeName,              // 기업규모

        @JsonProperty("companyClassName")
        String companyClassName,        // 기업구분명 (코스닥)

        @JsonProperty("orderWarning")
        String orderWarning,            // 주문주의구분 (0:없음, 2:관리종목, 3:과열, 4:위험, 5:임시, 1:ETF주의)

        @JsonProperty("nxtEnable")
        String nxtEnable                // NXT가능여부 (Y/N)
    ) {}
}
