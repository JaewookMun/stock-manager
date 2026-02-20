package app.jaewook.stockmanager.infra.kiwoom.dto.stock;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

/**
 * 키움 REST API - 종목정보 리스트 (ka10099) 요청 DTO
 */
@Builder
public record KiwoomStockInfoRequest(
    @JsonProperty("mrkt_tp")
    MarketType marketType           // 시장구분 (0:코스피, 10:코스닥, 3:ELW, 8:ETF, 30:K-OTC, 50:코넥스 등)
) {}
