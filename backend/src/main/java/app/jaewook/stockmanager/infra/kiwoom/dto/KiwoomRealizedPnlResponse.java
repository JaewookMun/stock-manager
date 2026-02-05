package app.jaewook.stockmanager.infra.kiwoom.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 키움 REST API - 일자별종목별실현손익요청_기간 (ka10073) 응답 DTO
 */
public record KiwoomRealizedPnlResponse(
    @JsonProperty("return_code")
    String resultCode,          // 결과코드 (0: 정상)

    @JsonProperty("return_msg")
    String message,             // 메시지

    @JsonProperty("dt_stk_rlzt_pl")
    List<RealizedPnlItem> output    // 일자별종목별실현손익 목록
) {
    public record RealizedPnlItem(
        @JsonProperty("dt")
        String date,                    // 일자

        @JsonProperty("tdy_htssel_cmsn")
        String htsSellCommission,       // 당일hts매도수수료

        @JsonProperty("stk_nm")
        String stockName,               // 종목명

        @JsonProperty("cntr_qty")
        String quantity,                // 체결량

        @JsonProperty("buy_uv")
        String buyPrice,                // 매입단가

        @JsonProperty("cntr_pric")
        String executionPrice,          // 체결가

        @JsonProperty("tdy_sel_pl")
        String realizedPnl,             // 당일매도손익

        @JsonProperty("pl_rt")
        String pnlRate,                 // 손익율

        @JsonProperty("stk_cd")
        String stockCode,               // 종목코드

        @JsonProperty("tdy_trde_cmsn")
        String tradingCommission,       // 당일매매수수료

        @JsonProperty("tdy_trde_tax")
        String tradingTax,              // 당일매매세금

        @JsonProperty("wthd_alowa")
        String withdrawalAmount,        // 인출가능금액

        @JsonProperty("loan_dt")
        String loanDate,                // 대출일

        @JsonProperty("crd_tp")
        String creditClassification     // 신용구분
    ) {}
}
