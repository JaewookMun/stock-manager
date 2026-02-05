package app.jaewook.stockmanager.infra.kiwoom.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

/**
 * 키움 REST API - 일자별종목별실현손익요청_기간 (ka10073) 요청 DTO
 */
@Builder
public record KiwoomRealizedPnlRequest(
    @JsonProperty("strt_dt")
    String startDate,           // 시작일자 (YYYYMMDD)

    @JsonProperty("end_dt")
    String endDate,             // 종료일자 (YYYYMMDD)

    @JsonProperty("stk_cd")
    String stockCode            // 종목코드 (선택)
) {}
