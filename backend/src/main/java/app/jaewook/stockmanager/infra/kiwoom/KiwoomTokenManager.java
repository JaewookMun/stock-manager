package app.jaewook.stockmanager.infra.kiwoom;

import app.jaewook.stockmanager.domain.Account;
import app.jaewook.stockmanager.infra.db.AccountRepository;
import app.jaewook.stockmanager.infra.kiwoom.dto.KiwoomOAuthTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class KiwoomTokenManager {

    private final KiwoomApiClient kiwoomApiClient;
    private final AccountRepository accountRepository;
    private final Map<String, TokenInfo> tokenCache = new ConcurrentHashMap<>();

    /**
     * 유효한 토큰 반환 (만료시 자동 재발급)
     */
    public String getValidToken(String accountNumber) {
        TokenInfo tokenInfo = tokenCache.get(accountNumber);

        if (tokenInfo == null || tokenInfo.isExpired()) {
            log.info("Token expired or not found for account: {}. Issuing new token.", accountNumber);
            return refreshToken(accountNumber);
        }

        return tokenInfo.accessToken();
    }

    private String refreshToken(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));

        KiwoomOAuthTokenResponse response = kiwoomApiClient.issueAccessToken(
                account.getAppkey(),
                account.getSecretkey()
        );

        if (response == null || response.token() == null) {
            throw new IllegalStateException("Failed to issue access token");
        }
        log.debug("Issued new token for account: {}", accountNumber);

        TokenInfo tokenInfo = new TokenInfo(
                response.token(),
                LocalDateTime.parse(
                        response.expiresAt(),
                        DateTimeFormatter.ofPattern("yyyyMMddHHmmss")).minusMinutes(1) // 1분 여유
        );

        tokenCache.put(accountNumber, tokenInfo);
        return tokenInfo.accessToken();
    }

    private record TokenInfo(String accessToken, LocalDateTime expiresAt) {
        boolean isExpired() {
            return LocalDateTime.now().isAfter(expiresAt);
        }
    }
}
