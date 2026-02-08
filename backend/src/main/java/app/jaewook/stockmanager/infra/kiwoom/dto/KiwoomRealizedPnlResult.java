package app.jaewook.stockmanager.infra.kiwoom.dto;

/**
 * 키움 API 실현손익 응답 + 페이지네이션 헤더 wrapper
 */
public record KiwoomRealizedPnlResult(
        KiwoomRealizedPnlResponse response,
        KiwoomResponseHeader header
) {
}
