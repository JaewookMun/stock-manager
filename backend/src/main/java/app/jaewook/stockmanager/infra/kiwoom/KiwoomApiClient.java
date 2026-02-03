package app.jaewook.stockmanager.infra.kiwoom;

import app.jaewook.stockmanager.infra.kiwoom.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 키움 REST API 클라이언트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KiwoomApiClient {

    private final RestClient restClient;

    private static final int MAX_PERIOD_MONTHS = 3;

    private static final String CONTINUE_CHECK = "cont-yn";
    private static final String NEXT_KEY = "next-key";

    private static final String BASE_URL = "https://api.kiwoom.com";
    private static final String TR_ID_REALIZED_PNL_PERIOD = "ka10073";
    private static final String TR_ID_CASH_FLOW = "kt00015";
    private static final String OAUTH_TOKEN_PATH = "/oauth2/token";

    /**
     * API 호출을 위한 토큰 발급
     */
    protected KiwoomOAuthTokenResponse issueAccessToken(String appKey, String appSecret) {
        log.info("Calling Kiwoom API - issueAccessToken");

        KiwoomOAuthTokenRequest request = KiwoomOAuthTokenRequest.of(appKey, appSecret);

        KiwoomOAuthTokenResponse response = restClient.post()
                .uri(BASE_URL + OAUTH_TOKEN_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(KiwoomOAuthTokenResponse.class);

        log.info("Kiwoom API response - issueAccessToken: returnCode={}, message={}",
                response != null ? response.returnCode() : null,
                response != null ? response.returnMessage() : null);

        return response;
    }

    /**
     * 일자별종목별실현손익요청_기간 (ka10073)
     */
    public KiwoomRealizedPnlResponse getRealizedPnlByPeriod(KiwoomRealizedPnlRequest request, String accessToken) {
        log.info("Calling Kiwoom API - getRealizedPnlByPeriod: {}", request);

        KiwoomRealizedPnlResponse response = restClient.post()
                .uri(BASE_URL + "/api/dostk/acnt")
                .headers(headers -> setCommonHeaders(headers, accessToken))
                .header("api-id", TR_ID_REALIZED_PNL_PERIOD)
                .body(request)
                .retrieve()
                .body(KiwoomRealizedPnlResponse.class);

        log.info("Kiwoom API response - getRealizedPnlByPeriod: resultCode={}, message={}",
                response != null ? response.resultCode() : null,
                response != null ? response.message() : null);

        return response;
    }

    /**
     * 위탁종합거래내역요청 (kt00015)
     */
    public KiwoomCashFlowResponse getCashFlow(KiwoomCashFlowRequest request, String accessToken) {
        log.info("Calling Kiwoom API - getCashFlow: {}", request);

        ResponseEntity<KiwoomCashFlowResponse> responseEntity = restClient.post()
                .uri(BASE_URL + "/api/dostk/acnt")
                .headers(headers -> setCommonHeaders(headers, accessToken))
                .header("api-id", TR_ID_CASH_FLOW)
                .body(request)
                .retrieve()
                .toEntity(KiwoomCashFlowResponse.class);

        HttpHeaders headers = responseEntity.getHeaders();
        String continueYn = headers.getFirst(CONTINUE_CHECK);
        String nextKey = headers.getFirst(NEXT_KEY);
        log.info("Kiwoom API response - getCashFlow: continueYn={}, nextKey={}", continueYn, nextKey);

        KiwoomCashFlowResponse response = responseEntity.getBody();

        log.info("Kiwoom API response - getCashFlow: resultCode={}, message={}",
                response != null ? response.resultCode() : null,
                response != null ? response.message() : null);

        return response;
    }

    private void setCommonHeaders(HttpHeaders headers, String accessToken) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
    }
}
