package app.jaewook.stockmanager.infra.kiwoom.dto.stock;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 키움 REST API - 주식기본정보요청 (ka10001) 응답 DTO
 */
public record KiwoomStockBasicInfoResponse(
    @JsonProperty("return_code")
    int resultCode,                     // 결과코드 (0: 정상)

    @JsonProperty("return_msg")
    String message,                     // 메시지

    // 종목 기본정보
    @JsonProperty("stk_cd")
    String stockCode,                   // 종목코드

    @JsonProperty("stk_nm")
    String stockName,                   // 종목명

    @JsonProperty("setl_mm")
    String settlementMonth,             // 결산월

    @JsonProperty("fav")
    String faceValue,                   // 액면가

    @JsonProperty("fav_unit")
    String faceValueUnit,               // 액면가단위

    @JsonProperty("cap")
    String capital,                     // 자본금

    @JsonProperty("flo_stk")
    String listedShares,                // 상장주식

    @JsonProperty("dstr_stk")
    String floatingShares,              // 유통주식

    @JsonProperty("dstr_rt")
    String floatingRate,                // 유통비율

    @JsonProperty("crd_rt")
    String creditRate,                  // 신용비율

    // 시세 정보
    @JsonProperty("cur_prc")
    String currentPrice,                // 현재가

    @JsonProperty("pre_sig")
    String changeSign,                  // 대비기호

    @JsonProperty("pred_pre")
    String changeFromPrevious,          // 전일대비

    @JsonProperty("flu_rt")
    String fluctuationRate,             // 등락율

    @JsonProperty("open_pric")
    String openPrice,                   // 시가

    @JsonProperty("high_pric")
    String highPrice,                   // 고가

    @JsonProperty("low_pric")
    String lowPrice,                    // 저가

    @JsonProperty("upl_pric")
    String upperLimitPrice,             // 상한가

    @JsonProperty("lst_pric")
    String lowerLimitPrice,             // 하한가

    @JsonProperty("base_pric")
    String basePrice,                   // 기준가

    @JsonProperty("trde_qty")
    String tradingVolume,               // 거래량

    @JsonProperty("trde_pre")
    String tradingChange,               // 거래대비

    // 예상 체결
    @JsonProperty("exp_cntr_pric")
    String expectedContractPrice,       // 예상체결가

    @JsonProperty("exp_cntr_qty")
    String expectedContractQuantity,    // 예상체결수량

    // 시가총액/투자지표
    @JsonProperty("mac")
    String marketCap,                   // 시가총액

    @JsonProperty("mac_wght")
    String marketCapWeight,             // 시가총액비중

    @JsonProperty("for_exh_rt")
    String foreignExhaustionRate,       // 외인소진률

    @JsonProperty("repl_pric")
    String substitutionPrice,           // 대용가

    @JsonProperty("per")
    String per,                         // PER

    @JsonProperty("eps")
    String eps,                         // EPS

    @JsonProperty("roe")
    String roe,                         // ROE

    @JsonProperty("pbr")
    String pbr,                         // PBR

    @JsonProperty("ev")
    String ev,                          // EV

    @JsonProperty("bps")
    String bps,                         // BPS

    @JsonProperty("sale_amt")
    String salesAmount,                 // 매출액

    @JsonProperty("bus_pro")
    String operatingProfit,             // 영업이익

    @JsonProperty("cup_nga")
    String netIncome,                   // 당기순이익

    // 연중/250일 고저
    @JsonProperty("oyr_hgst")
    String yearHigh,                    // 연중최고

    @JsonProperty("oyr_lwst")
    String yearLow,                     // 연중최저

    @JsonProperty("250hgst")
    String highest250,                  // 250최고

    @JsonProperty("250lwst")
    String lowest250,                   // 250최저

    @JsonProperty("250hgst_pric_dt")
    String highest250Date,              // 250최고가일

    @JsonProperty("250hgst_pric_pre_rt")
    String highest250Rate,              // 250최고가대비율

    @JsonProperty("250lwst_pric_dt")
    String lowest250Date,               // 250최저가일

    @JsonProperty("250lwst_pric_pre_rt")
    String lowest250Rate                // 250최저가대비율
) {}
