package app.jaewook.stockmanager.service;

import app.jaewook.stockmanager.domain.Account;
import app.jaewook.stockmanager.infra.db.AccountRepository;
import app.jaewook.stockmanager.service.dto.AccountResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public AccountResult.Accounts getAccounts() {
        List<Account> accounts = accountRepository.findAll();

        List<AccountResult.AccountItem> items = accounts.stream()
                .map(a -> AccountResult.AccountItem.builder()
                        .id(a.getId())
                        .accountNumber(a.getAccountNumber())
                        .alias(a.getAlias())
                        .type(a.getType())
                        .build())
                .toList();

        return AccountResult.Accounts.builder()
                .accounts(items)
                .build();
    }
}
