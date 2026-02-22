package app.jaewook.stockmanager.service.dto;

import app.jaewook.stockmanager.domain.AccountType;
import lombok.Builder;

import java.util.List;

public class AccountResult {

    @Builder
    public record Accounts(
            List<AccountItem> accounts
    ) {}

    @Builder
    public record AccountItem(
            Long id,
            String accountNumber,
            String alias,
            AccountType type
    ) {}
}
