package app.jaewook.stockmanager.infra.kiwoom.dto;

import lombok.Builder;

@Builder
public record KiwoomResponseHeader(
        boolean hasNext,
        String nextKey
) {
}
