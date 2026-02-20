package app.jaewook.stockmanager.infra.kiwoom.dto.stock;

import app.jaewook.stockmanager.infra.kiwoom.dto.KiwoomResponseHeader;

/**
 * 키움 API 종목정보 응답 + 페이지네이션 헤더 wrapper
 */
public record KiwoomStockInfoResult(
        KiwoomStockInfoResponse response,
        KiwoomResponseHeader header
) {
}
