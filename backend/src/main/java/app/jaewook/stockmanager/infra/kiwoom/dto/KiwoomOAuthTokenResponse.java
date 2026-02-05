package app.jaewook.stockmanager.infra.kiwoom.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KiwoomOAuthTokenResponse(
        @JsonProperty("expires_dt")
        String expiresAt,

        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("token")
        String token,

        @JsonProperty("return_code")
        Integer returnCode,

        @JsonProperty("return_msg")
        String returnMessage
) {
    public boolean isSuccess() {
        return returnCode != null && returnCode == 0;
    }
}
