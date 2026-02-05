package app.jaewook.stockmanager.infra.kiwoom.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KiwoomOAuthTokenRequest(
        @JsonProperty("grant_type")
        String grantType,

        @JsonProperty("appkey")
        String appKey,

        @JsonProperty("secretkey")
        String secretKey
) {
    public static KiwoomOAuthTokenRequest of(String appKey, String secretKey) {
        return new KiwoomOAuthTokenRequest("client_credentials", appKey, secretKey);
    }
}
