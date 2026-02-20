package app.jaewook.stockmanager.infra.kiwoom;

import app.jaewook.stockmanager.domain.Account;
import app.jaewook.stockmanager.infra.db.AccountRepository;
import app.jaewook.stockmanager.infra.kiwoom.dto.*;
import app.jaewook.stockmanager.infra.kiwoom.dto.stock.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class KiwoomApiClientTest {
    @Autowired
    KiwoomApiClient kiwoomApiClient;
    @Autowired
    KiwoomTokenManager tokenManager;
    @Autowired
    AccountRepository accountRepository;

    @Test
    void issueAccessToken() {
        // given - Account must exist in DB with appkey/secretkey
        Account account = accountRepository.findAll().getFirst();

        // when
        KiwoomOAuthTokenResponse response = kiwoomApiClient.issueAccessToken(
                account.getAppkey(),
                account.getSecretkey()
        );

        // then
        System.out.println("response = " + response);
        assertNotNull(response);
        assertNotNull(response.token());
    }

    @Test
    void getRealizedPnlByPeriod() {
        // given
        Account account = accountRepository.findAll().getFirst();
        String accessToken = tokenManager.getValidToken(account.getAccountNumber());
        KiwoomRealizedPnlRequest request = KiwoomRealizedPnlRequest.builder()
                .startDate("20251101")
                .endDate("20260131")
                .stockCode("")
                .build();

        // when
        KiwoomRealizedPnlResult response = kiwoomApiClient.getRealizedPnlByPeriod(request, accessToken, null);

        // then
        System.out.println("response = " + response);
        assertNotNull(response);
    }

    @Test
    void getCashFlow() {
        // given
        Account account = accountRepository.findAll().getFirst();
        String accessToken = tokenManager.getValidToken(account.getAccountNumber());
        KiwoomCashFlowRequest request = KiwoomCashFlowRequest.builder()
                .startDate("20250201")
                .endDate("20260131")
                .category("1")
                .stockCode("")
                .currencyCode("")
                .productType("0")
                .overseasExchangeCode("")
                .domesticExchangeCode("%")
                .build();

        // when
        KiwoomCashFlowResult response = kiwoomApiClient.getCashFlow(request, accessToken, null);

        // then
        System.out.println("response = " + response);
        assertNotNull(response);
    }

    @Test
    void getStockInfoList() {
        // given
        Account account = accountRepository.findAll().getFirst();
        String accessToken = tokenManager.getValidToken(account.getAccountNumber());
        KiwoomStockInfoRequest request = KiwoomStockInfoRequest.builder()
                .marketType(MarketType.KOSPI) // 코스피
                .build();

        // when
        KiwoomStockInfoResult response = kiwoomApiClient.getStockInfoList(request, accessToken, null);

        // then
        System.out.println("response = " + response);
        assertNotNull(response);
    }

    @Test
    void getStockBasicInfo() {
        // given
        Account account = accountRepository.findAll().getFirst();
        String accessToken = tokenManager.getValidToken(account.getAccountNumber());
        KiwoomStockBasicInfoRequest request = KiwoomStockBasicInfoRequest.builder()
                .stockCode("005930") // 삼성전자
                .build();

        // when
        KiwoomStockBasicInfoResponse response = kiwoomApiClient.getStockBasicInfo(request, accessToken);

        // then
        System.out.println("response = " + response);
        assertNotNull(response);
    }

}
