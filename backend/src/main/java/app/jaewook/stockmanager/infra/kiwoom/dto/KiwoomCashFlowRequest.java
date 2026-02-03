package app.jaewook.stockmanager.infra.kiwoom.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

/**
 * 키움 REST API - 위탁종합거래내역요청 (kt00015) 요청 DTO
 */
@Builder
public record KiwoomCashFlowRequest(
    @JsonProperty("strt_dt")
    String startDate,               // 시작일자 (YYYYMMDD)

    @JsonProperty("end_dt")
    String endDate,                 // 종료일자 (YYYYMMDD)

    @JsonProperty("tp")
    String category,                // 구분 (1: 입출금)

    @JsonProperty("stk_cd")
    String stockCode,               // 종목코드

    @JsonProperty("crnc_cd")
    String currencyCode,            // 통화코드

    @JsonProperty("gds_tp")
    String productType,             // 상품구분 (0: 전체)

    @JsonProperty("frgn_stex_code")
    String overseasExchangeCode,    // 해외거래소코드

    @JsonProperty("dmst_stex_tp")
    String domesticExchangeCode     // 국내거래소구분 (%: 전체)
) {}
