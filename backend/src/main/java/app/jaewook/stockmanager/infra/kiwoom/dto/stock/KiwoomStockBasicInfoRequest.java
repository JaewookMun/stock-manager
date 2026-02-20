package app.jaewook.stockmanager.infra.kiwoom.dto.stock;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

/**
 * 키움 REST API - 주식기본정보요청 (ka10001) 요청 DTO
 */
@Builder
public record KiwoomStockBasicInfoRequest(
    @JsonProperty("stk_cd")
    String stockCode            // 종목코드 (KRX:039490, NXT:039490_NX, SOR:039490_AL)
) {}
