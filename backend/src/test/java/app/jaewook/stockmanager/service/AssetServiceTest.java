package app.jaewook.stockmanager.service;

import app.jaewook.stockmanager.domain.Account;
import app.jaewook.stockmanager.infra.db.AccountRepository;
import app.jaewook.stockmanager.service.dto.AssetCommand;
import app.jaewook.stockmanager.service.dto.AssetCommand.RealizedPnl;
import app.jaewook.stockmanager.service.dto.AssetResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AssetServiceTest {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AssetService assetService;

    @Test
    void realizedPnl() {
        // given
        Account account = accountRepository.findAll()
                .stream()
                .filter(a -> a.getAlias().equals("위탁"))
                .findFirst()
                .orElseThrow();

        RealizedPnl command = RealizedPnl.builder()
                .accountNumber(account.getAccountNumber())
                .stockCode("")
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2026, 1, 31))
                .build();

        AssetResult.RealizedPnl realizedPnl = assetService.getRealizedPnl(command);
        realizedPnl.items().forEach(System.out::println);
    }

    @Test
    void cashFlow() {
        Account account = accountRepository.findAll()
                .stream()
                .filter(a -> a.getAlias().equals("위탁"))
                .findFirst()
                .orElseThrow();

        AssetCommand.CashFlow command = AssetCommand.CashFlow.builder()
                .accountNumber(account.getAccountNumber())
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2026, 1, 31))
                .build();

        AssetResult.CashFlow cashFlow = assetService.getCashFlow(command);
        cashFlow.items().forEach(System.out::println);
    }
}