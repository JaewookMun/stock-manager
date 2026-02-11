package app.jaewook.stockmanager.service.dto;

import lombok.Builder;

import java.time.LocalDate;

public class AssetCommand {
    @Builder
    public record RealizedPnl(
        String accountNumber,   // 계좌번호
        String stockCode,       // 종목코드
        LocalDate startDate,    // 시작일자 (YYYYMMDD)
        LocalDate endDate       // 종료일자 (YYYYMMDD)
    ) {
    }

    @Builder
    public record CashFlow(
        String accountNumber,          // 계좌번호
        LocalDate startDate,           // 시작일자 (YYYYMMDD)
        LocalDate endDate,             // 종료일자 (YYYYMMDD)
        String category,               // 구분
        String stockCode,              // 종목코드
        String currencyCode,           // 통화코드
        String productType,            // 상품구분
        String overseasExchangeCode,   // 해외거래소코드
        String domesticExchangeCode    // 국내거래소구분
    ) {
    }
}
