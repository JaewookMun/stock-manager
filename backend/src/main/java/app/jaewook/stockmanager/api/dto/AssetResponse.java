package app.jaewook.stockmanager.api.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class AssetResponse {
    @Builder
    public record RealizedPnl(
        List<RealizedPnlItem> items  // 일자별종목별실현손익
    ) {
        @Builder
        public record RealizedPnlItem(
            LocalDate date,             // 일자
            String htsSellCommission,   // 당일hts매도수수료
            String stockName,           // 종목명
            Integer quantity,           // 체결량
            BigDecimal buyPrice,        // 매입단가
            BigDecimal executionPrice,  // 체결가
            BigDecimal realizedPnl,     // 당일매도손익
            BigDecimal pnlRate,         // 손익율
            String stockCode,           // 종목코드
            BigDecimal tradingCommission,   // 당일매매수수료
            BigDecimal tradingTax       // 당일매매세금
        ) {}
    }

    @Builder
    public record CashFlow(
        List<CashFlowItem> items        // 위탁종합거래내역배열
    ) {
        @Builder
        public record CashFlowItem(
            String tradeDate,               // 거래일자
            String tradeNumber,             // 거래번호
            String summary,                 // 적요명
            String creditTradeTypeName,     // 신용거래구분명
            BigDecimal settlementAmount,    // 정산금액
            BigDecimal loanRepayment,       // 대출금상환
            BigDecimal tradingAmountForeign,// 거래금액(외)
            BigDecimal settlementAmountForeign, // 정산금액(외)
            BigDecimal depositBalance,      // 예수금잔고
            String currencyCode,            // 통화코드
            String tradeTypeCode,           // 거래종류구분
            String tradeTypeName,           // 거래종류명
            String stockName,               // 종목명
            BigDecimal tradingAmount,       // 거래금액
            BigDecimal tradingAndAgriculturalTax, // 거래및농특세
            BigDecimal repaymentDifference, // 상환차금
            BigDecimal transactionTaxForeign,   // 거래세(외)
            BigDecimal overdueSum,          // 연체합
            BigDecimal foreignDepositBalance,   // 외화예수금잔고
            String mediaTypeName,           // 매체구분명
            String inOutType,               // 입출구분
            String inOutTypeName,           // 입출구분명
            String originalTradeNumber,     // 원거래번호
            String stockCode,               // 종목코드
            String tradeQuantity,           // 거래수량/좌수
            BigDecimal commission,          // 수수료
            BigDecimal interestOrBorrowingUse,  // 이자/대주이용
            BigDecimal commissionForeign,   // 수수료(외)
            BigDecimal overdueSumForeign,   // 연체합(외)
            String securitiesBalance,       // 유가금잔
            String processTime,             // 처리시간
            String isinCode,                // ISIN코드
            String exchangeCode,            // 거래소코드
            String exchangeName,            // 거래소명
            String tradePriceOrExchangeRate,// 거래단가/환율
            BigDecimal incomeTaxOrResidentTax,  // 소득/주민세
            String loanDate,                // 대출일
            String receivable,              // 미수(원/주)
            String repaymentSum,            // 변제합
            String executionDate,           // 체결일
            String cashierNumber,           // 출납번호
            String processor,               // 처리자
            String processingBranch,        // 처리점
            String tradeForm,               // 매매형태
            BigDecimal taxBasePrice,        // 과세기준가
            BigDecimal taxCommissionSum,    // 세금수수료합
            BigDecimal foreignPaidTaxForeign,   // 외국납부세액(외)
            BigDecimal receivableForeign,   // 미수(외)
            String repaymentSumForeign,     // 변제합(외)
            String depositor,               // 입금자
            String tradeHistoryType         // 거래내역구분
        ) {}
    }
}
