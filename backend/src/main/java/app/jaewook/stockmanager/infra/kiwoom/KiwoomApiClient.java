package app.jaewook.stockmanager.infra.kiwoom;

import app.jaewook.stockmanager.infra.kiwoom.dto.*;
import jakarta.annotation.Nullable;
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
     * 단일 요청만 처리. 연속 조회가 필요한 경우 서비스 레이어에서 처리
     *
     * @param request     요청 DTO
     * @param accessToken 접근 토큰
     * @param nextKey     연속 조회 키 (첫 요청 시 null)
     * @return 응답 + 페이지네이션 헤더
     */
    public KiwoomRealizedPnlResult getRealizedPnlByPeriod(KiwoomRealizedPnlRequest request, String accessToken, @Nullable String nextKey) {
        log.info("Calling Kiwoom API - getRealizedPnlByPeriod: {}, nextKey={}", request, nextKey);

        ResponseEntity<KiwoomRealizedPnlResponse> responseEntity = callRealizedPnlApi(request, accessToken, nextKey);
        KiwoomResponseHeader responseHeader = extractResponseHeader(responseEntity.getHeaders());
        KiwoomRealizedPnlResponse response = responseEntity.getBody();

        log.info("Kiwoom API response - getRealizedPnlByPeriod: resultCode={}, message={}, hasNext={}, nextKey={}",
                response != null ? response.resultCode() : null,
                response != null ? response.message() : null,
                responseHeader.hasNext(),
                responseHeader.nextKey());

        return new KiwoomRealizedPnlResult(response, responseHeader);
    }

    private ResponseEntity<KiwoomRealizedPnlResponse> callRealizedPnlApi(
            KiwoomRealizedPnlRequest request, String accessToken, String nextKey) {

        RestClient.RequestBodySpec requestSpec = restClient.post()
                .uri(BASE_URL + "/api/dostk/acnt")
                .headers(headers -> setCommonHeaders(headers, accessToken))
                .header("api-id", TR_ID_REALIZED_PNL_PERIOD);

        if (nextKey != null) {
            requestSpec = requestSpec
                    .header(CONTINUE_CHECK, "Y")
                    .header(NEXT_KEY, nextKey);
        }

        return requestSpec
                .body(request)
                .retrieve()
                .toEntity(KiwoomRealizedPnlResponse.class);
    }

    /**
     * 위탁종합거래내역요청 (kt00015)
     * 단일 요청만 처리. 연속 조회가 필요한 경우 서비스 레이어에서 처리
     *
     * @param request     요청 DTO
     * @param accessToken 접근 토큰
     * @param nextKey     연속 조회 키 (첫 요청 시 null)
     * @return 응답 + 페이지네이션 헤더
     */
    public KiwoomCashFlowResult getCashFlow(KiwoomCashFlowRequest request, String accessToken, @Nullable String nextKey) {
        log.info("Calling Kiwoom API - getCashFlow: {}, nextKey={}", request, nextKey);

        ResponseEntity<KiwoomCashFlowResponse> responseEntity = callCashFlowApi(request, accessToken, nextKey);
        KiwoomResponseHeader responseHeader = extractResponseHeader(responseEntity.getHeaders());
        KiwoomCashFlowResponse response = responseEntity.getBody();

        log.info("Kiwoom API response - getCashFlow: resultCode={}, message={}, hasNext={}, nextKey={}",
                response != null ? response.resultCode() : null,
                response != null ? response.message() : null,
                responseHeader.hasNext(),
                responseHeader.nextKey());

        return new KiwoomCashFlowResult(response, responseHeader);
    }

    private ResponseEntity<KiwoomCashFlowResponse> callCashFlowApi(
            KiwoomCashFlowRequest request, String accessToken, String nextKey) {

        RestClient.RequestBodySpec requestSpec = restClient.post()
                .uri(BASE_URL + "/api/dostk/acnt")
                .headers(headers -> setCommonHeaders(headers, accessToken))
                .header("api-id", TR_ID_CASH_FLOW);

        // 연속 조회인 경우 헤더 추가
        if (nextKey != null) {
            requestSpec = requestSpec
                    .header(CONTINUE_CHECK, "Y")
                    .header(NEXT_KEY, nextKey);
        }

        return requestSpec
                .body(request)
                .retrieve()
                .toEntity(KiwoomCashFlowResponse.class);
    }

    private KiwoomResponseHeader extractResponseHeader(HttpHeaders headers) {
        String continueYn = headers.getFirst(CONTINUE_CHECK);
        String nextKey = headers.getFirst(NEXT_KEY);

        return KiwoomResponseHeader.builder()
                .hasNext(continueYn != null && continueYn.equals("Y"))
                .nextKey(nextKey)
                .build();
    }

    private void setCommonHeaders(HttpHeaders headers, String accessToken) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
    }
}
