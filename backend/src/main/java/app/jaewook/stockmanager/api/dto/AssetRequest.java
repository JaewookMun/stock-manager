package app.jaewook.stockmanager.api.dto;

import lombok.Builder;

import java.time.LocalDate;

public class AssetRequest {
    @Builder
    public record RealizedPnl(
            String stockCode,       // 종목코드 (Optional, 6자)
            LocalDate startDate,    // 시작일자 (Required, YYYYMMDD)
            LocalDate endDate       // 종료일자 (Required, YYYYMMDD)
    ) {
    }

    @Builder
    public record CashFlow(
        LocalDate startDate,            // 시작일자 (Required, YYYYMMDD)
        LocalDate endDate,              // 종료일자 (Required, YYYYMMDD)
        String category,                // 구분 (Required) 0:전체,1:입출금,2:입출고,3:매매,4:매수,5:매도,6:입금,7:출금 등
        String stockCode,               // 종목코드 (Optional, 12자)
        String currencyCode,            // 통화코드 (Optional, 3자)
        String productType,             // 상품구분 (Required) 0:전체,1:국내주식,2:수익증권,3:해외주식,4:금융상품
        String overseasExchangeCode,    // 해외거래소코드 (Optional, 10자)
        String domesticExchangeCode     // 국내거래소구분 (Required) %:전체,KRX:한국거래소,NXT:넥스트트레이드
    ) {
    }
}
