package app.jaewook.stockmanager.infra.kiwoom;

import app.jaewook.stockmanager.domain.Account;
import app.jaewook.stockmanager.infra.db.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class KiwoomTokenManagerTest {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    KiwoomTokenManager tokenManager;

    @Test
    void getValidToken() {
        // given
        Account found = accountRepository.findAll().getFirst();

        // when & then
        String validToken = tokenManager.getValidToken(found.getAccountNumber());
        assertNotNull(validToken);
    }
}