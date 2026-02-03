package app.jaewook.stockmanager.infra.kiwoom.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 키움 REST API - 위탁종합거래내역요청 (kt00015) 응답 DTO
 */
public record KiwoomCashFlowResponse(
    @JsonProperty("return_code")
    String resultCode,              // 결과코드 (0: 정상)

    @JsonProperty("return_msg")
    String message,                 // 메시지

    @JsonProperty("acnt_no")
    String accountNumber,           // 계좌 번호

    @JsonProperty("trst_ovrl_trde_prps_array")
    List<CashFlowItem> output       // 위탁종합거래내역 목록
) {
    public record CashFlowItem(
        @JsonProperty("trde_dt")
        String tradeDate,               // 거래일자

        @JsonProperty("trde_no")
        String tradeNumber,             // 거래번호

        @JsonProperty("rmrk_nm")
        String summary,                 // 적요명

        @JsonProperty("crd_deal_tp_nm")
        String creditTradeTypeName,     // 신용거래구분명

        @JsonProperty("exct_amt")
        String settlementAmount,        // 정산금액

        @JsonProperty("loan_amt_rpya")
        String loanRepayment,           // 대출금상환

        @JsonProperty("fc_trde_amt")
        String tradingAmountForeign,    // 거래금액(외)

        @JsonProperty("fc_exct_amt")
        String settlementAmountForeign, // 정산금액(외)

        @JsonProperty("entra_remn")
        String depositBalance,          // 예수금잔고

        @JsonProperty("crnc_cd")
        String currencyCode,            // 통화코드

        @JsonProperty("trde_ocr_tp")
        String tradeTypeCode,           // 거래종류구분

        @JsonProperty("trde_kind_nm")
        String tradeTypeName,           // 거래종류명

        @JsonProperty("stk_nm")
        String stockName,               // 종목명

        @JsonProperty("trde_amt")
        String tradingAmount,           // 거래금액

        @JsonProperty("trde_agri_tax")
        String tradingAndAgriculturalTax, // 거래및농특세

        @JsonProperty("rpy_diffa")
        String repaymentDifference,     // 상환차금

        @JsonProperty("fc_trde_tax")
        String transactionTaxForeign,   // 거래세(외)

        @JsonProperty("dly_sum")
        String overdueSum,              // 연체합

        @JsonProperty("fc_entra")
        String foreignDepositBalance,   // 외화예수금잔고

        @JsonProperty("mdia_tp_nm")
        String mediaTypeName,           // 매체구분명

        @JsonProperty("io_tp")
        String inOutType,               // 입출구분

        @JsonProperty("io_tp_nm")
        String inOutTypeName,           // 입출구분명

        @JsonProperty("orig_deal_no")
        String originalTradeNumber,     // 원거래번호

        @JsonProperty("stk_cd")
        String stockCode,               // 종목코드

        @JsonProperty("trde_qty_jwa_cnt")
        String tradeQuantity,           // 거래수량/좌수

        @JsonProperty("cmsn")
        String commission,              // 수수료

        @JsonProperty("int_ls_usfe")
        String interestOrBorrowingUse,  // 이자/대주이용

        @JsonProperty("fc_cmsn")
        String commissionForeign,       // 수수료(외)

        @JsonProperty("fc_dly_sum")
        String overdueSumForeign,       // 연체합(외)

        @JsonProperty("vlbl_nowrm")
        String securitiesBalance,       // 유가금잔

        @JsonProperty("proc_tm")
        String processTime,             // 처리시간

        @JsonProperty("isin_cd")
        String isinCode,                // ISIN코드

        @JsonProperty("stex_cd")
        String exchangeCode,            // 거래소코드

        @JsonProperty("stex_nm")
        String exchangeName,            // 거래소명

        @JsonProperty("trde_unit")
        String tradePriceOrExchangeRate, // 거래단가/환율

        @JsonProperty("incm_resi_tax")
        String incomeTaxOrResidentTax,  // 소득/주민세

        @JsonProperty("loan_dt")
        String loanDate,                // 대출일

        @JsonProperty("uncl_ocr")
        String receivable,              // 미수(원/주)

        @JsonProperty("rpym_sum")
        String repaymentSum,            // 변제합

        @JsonProperty("cntr_dt")
        String executionDate,           // 체결일

        @JsonProperty("rcpy_no")
        String cashierNumber,           // 출납번호

        @JsonProperty("prcsr")
        String processor,               // 처리자

        @JsonProperty("proc_brch")
        String processingBranch,        // 처리점

        @JsonProperty("trde_stle")
        String tradeForm,               // 매매형태

        @JsonProperty("txon_base_pric")
        String taxBasePrice,            // 과세기준가

        @JsonProperty("tax_sum_cmsn")
        String taxCommissionSum,        // 세금수수료합

        @JsonProperty("frgn_pay_txam")
        String foreignPaidTaxForeign,   // 외국납부세액(외)

        @JsonProperty("fc_uncl_ocr")
        String receivableForeign,       // 미수(외)

        @JsonProperty("rpym_sum_fr")
        String repaymentSumForeign,     // 변제합(외)

        @JsonProperty("rcpmnyer")
        String depositor,               // 입금자

        @JsonProperty("trde_prtc_tp")
        String tradeHistoryType         // 거래내역구분
    ) {}
}
