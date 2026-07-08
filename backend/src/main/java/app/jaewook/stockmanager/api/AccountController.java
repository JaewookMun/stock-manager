package app.jaewook.stockmanager.api;

import app.jaewook.stockmanager.api.dto.AccountResponse;
import app.jaewook.stockmanager.api.dto.ApiResponse;
import app.jaewook.stockmanager.api.mapper.AccountControllerMapper;
import app.jaewook.stockmanager.service.AccountService;
import app.jaewook.stockmanager.service.dto.AccountResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AccountControllerMapper mapper;

    /**
     * GET /api/accounts
     * 계좌번호 목록 조회
     */
    @GetMapping
    public ApiResponse<AccountResponse.Accounts> getAccounts() {
        log.info("GET /api/accounts");

        AccountResult.Accounts result = accountService.getAccounts();
        return ApiResponse.success(mapper.toAccountsResponse(result));
    }
}
