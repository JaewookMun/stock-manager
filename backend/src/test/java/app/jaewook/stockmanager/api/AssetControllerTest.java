package app.jaewook.stockmanager.api;

import app.jaewook.stockmanager.domain.Account;
import app.jaewook.stockmanager.infra.db.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AssetControllerTest {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    MockMvc mockMvc;

    @Test
    void realizedPnl() throws Exception {
        Account account = accountRepository.findAll()
                .stream()
                .filter(a -> a.getAlias().equals("위탁"))
                .findFirst()
                .orElseThrow();

        ResultActions resultActions = mockMvc.perform(get("/api/assets/realized-pnl")
                        .param("accountNumber", account.getAccountNumber())
                        .param("stockCode", "")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2026-01-31"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists());

        System.out.println(resultActions.andReturn().getResponse().getContentAsString());
    }
}