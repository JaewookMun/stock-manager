package app.jaewook.stockmanager.infra.kiwoom.dto;

/**
 * 키움 API 캐시플로우 응답 + 페이지네이션 헤더 wrapper
 */
public record KiwoomCashFlowResult(
        KiwoomCashFlowResponse response,
        KiwoomResponseHeader header
) {
}
