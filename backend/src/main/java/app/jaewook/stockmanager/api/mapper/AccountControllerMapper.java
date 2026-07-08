package app.jaewook.stockmanager.api.mapper;

import app.jaewook.stockmanager.api.dto.AccountResponse;
import app.jaewook.stockmanager.service.dto.AccountResult;
import org.springframework.stereotype.Component;

@Component
public class AccountControllerMapper {

    public AccountResponse.Accounts toAccountsResponse(AccountResult.Accounts result) {
        return AccountResponse.Accounts.builder()
                .accounts(result.accounts().stream()
                        .map(item -> AccountResponse.AccountItem.builder()
                                .id(item.id())
                                .accountNumber(maskAccountNumber(item.accountNumber()))
                                .alias(item.alias())
                                .type(item.type())
                                .build())
                        .toList())
                .build();
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() <= 4) {
            return "****";
        }
        return accountNumber.substring(0, accountNumber.length() - 4) + "****";
    }
}
