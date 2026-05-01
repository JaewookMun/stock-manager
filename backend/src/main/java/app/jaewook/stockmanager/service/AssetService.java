package app.jaewook.stockmanager.service;

import app.jaewook.stockmanager.domain.Account;
import app.jaewook.stockmanager.domain.CashFlow;
import app.jaewook.stockmanager.domain.CashFlowFetchHistory;
import app.jaewook.stockmanager.domain.RealizedPnl;
import app.jaewook.stockmanager.domain.RealizedPnlFetchHistory;
import app.jaewook.stockmanager.infra.db.AccountRepository;
import app.jaewook.stockmanager.infra.db.CashFlowHistoryRepository;
import app.jaewook.stockmanager.infra.db.CashFlowRepository;
import app.jaewook.stockmanager.infra.kiwoom.KiwoomTokenManager;
import app.jaewook.stockmanager.infra.kiwoom.dto.KiwoomCashFlowRequest;
import app.jaewook.stockmanager.infra.kiwoom.dto.KiwoomCashFlowResponse;
import app.jaewook.stockmanager.infra.kiwoom.dto.KiwoomCashFlowResult;
import app.jaewook.stockmanager.infra.kiwoom.dto.KiwoomRealizedPnlRequest;
import app.jaewook.stockmanager.infra.kiwoom.dto.KiwoomRealizedPnlResponse;
import app.jaewook.stockmanager.infra.kiwoom.dto.KiwoomRealizedPnlResult;
import app.jaewook.stockmanager.service.mapper.CashFlowDbMapper;
import app.jaewook.stockmanager.service.mapper.RealizedPnlDbMapper;
import app.jaewook.stockmanager.infra.db.RealizedPnlHistoryRepository;
import app.jaewook.stockmanager.infra.db.RealizedPnlRepository;
import app.jaewook.stockmanager.infra.kiwoom.KiwoomApiClient;
import app.jaewook.stockmanager.infra.kiwoom.KiwoomApiMapper;
import app.jaewook.stockmanager.infra.kiwoom.KiwoomRateLimiter;
import app.jaewook.stockmanager.service.dto.AssetCommand;
import app.jaewook.stockmanager.service.dto.AssetResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetService {

    private static final int MAX_MONTHS_PER_REALIZED_PNL_REQUEST = 3;
    private static final int MAX_MONTHS_PER_CASH_FLOW_REQUEST = 12;

    private final AccountRepository accountRepository;
    private final KiwoomTokenManager tokenManager;
    private final KiwoomApiClient kiwoomApiClient;
    private final KiwoomApiMapper kiwoomApiMapper;
    private final KiwoomRateLimiter kiwoomRateLimiter;
    private final RealizedPnlHistoryRepository realizedPnlHistoryRepository;
    private final RealizedPnlRepository realizedPnlRepository;
    private final RealizedPnlDbMapper realizedPnlDbMapper;
    private final CashFlowHistoryRepository cashFlowHistoryRepository;
    private final CashFlowRepository cashFlowRepository;
    private final CashFlowDbMapper cashFlowDbMapper;

    /**
     * 일자별종목별실현손익 조회 (기간)
     * DB에서 조회 이력이 있는 날짜 확인 후, 없는 날짜에 대해서만 키움 REST API ka10073 호출
     */
    @Transactional
    public AssetResult.RealizedPnl getRealizedPnl(AssetCommand.RealizedPnl command) {
        log.info("getRealizedPnl - command: {}", command);

        Account account = accountRepository.findById(command.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + command.accountId()));

        // 1. 요청 기간 내 조회 이력이 있는 날짜 확인
        List<RealizedPnlFetchHistory> fetchedHistories = realizedPnlHistoryRepository
                .findByAccountAndTargetDateBetween(account, command.startDate(), command.endDate());

        Set<LocalDate> fetchedDates = fetchedHistories.stream()
                .map(RealizedPnlFetchHistory::getTargetDate)
                .collect(Collectors.toSet());

        // 2. 조회 이력이 없는 날짜가 있으면 Kiwoom API 호출
        List<LocalDate> missingDates = findMissingDates(command.startDate(), command.endDate(), fetchedDates);

        if (!missingDates.isEmpty()) {
            log.info("getRealizedPnl - fetching data for {} missing dates from Kiwoom API", missingDates.size());
            fetchAndSaveFromKiwoom(command, account, missingDates);
        } else {
            log.info("getRealizedPnl - all dates cached for account: {}, period: {} ~ {}",
                    account.getAccountNumber(), command.startDate(), command.endDate());
        }

        // 3. DB에서 해당 기간의 실현손익 데이터 조회
        List<RealizedPnl> realizedPnls = realizedPnlRepository
                .findByAccountAndDateBetweenOrderByDateDescStockCodeAsc(account, command.startDate(), command.endDate());

        return realizedPnlDbMapper.toServiceResult(realizedPnls);
    }

    /**
     * 위탁종합거래내역 조회 (현금흐름)
     * DB에서 조회 이력이 있는 날짜 확인 후, 없는 날짜에 대해서만 키움 REST API kt00015 호출
     * - 구분: 1 (입출금)
     * - 상품구분: 0 (전체)
     * - 국내거래소구분: % (전체)
     */
    @Transactional
    public AssetResult.CashFlow getCashFlow(AssetCommand.CashFlow command) {
        log.info("getCashFlow - command: {}", command);

        Account account = accountRepository.findById(command.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + command.accountId()));

        // 1. 요청 기간 내 조회 이력이 있는 날짜 확인
        List<CashFlowFetchHistory> fetchedHistories = cashFlowHistoryRepository
                .findByAccountAndTargetDateBetween(account, command.startDate(), command.endDate());

        Set<LocalDate> fetchedDates = fetchedHistories.stream()
                .map(CashFlowFetchHistory::getTargetDate)
                .collect(Collectors.toSet());

        // 2. 조회 이력이 없는 날짜가 있으면 Kiwoom API 호출
        List<LocalDate> missingDates = findMissingDates(command.startDate(), command.endDate(), fetchedDates);

        if (!missingDates.isEmpty()) {
            log.info("getCashFlow - fetching data for {} missing dates from Kiwoom API", missingDates.size());
            fetchAndSaveCashFlowFromKiwoom(command, account, missingDates);
        } else {
            log.info("getCashFlow - all dates cached for account: {}, period: {} ~ {}",
                    account.getAccountNumber(), command.startDate(), command.endDate());
        }

        // 3. DB에서 해당 기간의 현금흐름 데이터 조회
        List<CashFlow> cashFlows = cashFlowRepository
                .findByAccountAndTradeDateBetweenOrderByTradeDateDescTradeNumberAsc(account, command.startDate(), command.endDate());

        return cashFlowDbMapper.toServiceResult(cashFlows);
    }

    private void fetchAndSaveCashFlowFromKiwoom(AssetCommand.CashFlow command, Account account, List<LocalDate> missingDates) {
        LocalDate minDate = missingDates.stream().min(LocalDate::compareTo).orElseThrow();
        LocalDate maxDate = missingDates.stream().max(LocalDate::compareTo).orElseThrow();

        // 12개월(1년) 단위로 기간 분할하여 API 호출
        List<CashFlow> allEntities = new ArrayList<>();
        List<DateRange> dateRanges = splitIntoChunks(minDate, maxDate, MAX_MONTHS_PER_CASH_FLOW_REQUEST);

        String accessToken = tokenManager.getValidToken(account.getAccountNumber());

        for (DateRange range : dateRanges) {
            log.info("getCashFlow - fetching from Kiwoom API: {} ~ {}", range.start(), range.end());

            KiwoomCashFlowRequest kiwoomRequest = KiwoomCashFlowRequest.builder()
                    .startDate(range.start().format(DateTimeFormatter.BASIC_ISO_DATE))
                    .endDate(range.end().format(DateTimeFormatter.BASIC_ISO_DATE))
                    .category(command.category() != null ? command.category() : "1")
                    .stockCode(command.stockCode() != null ? command.stockCode() : "")
                    .currencyCode(command.currencyCode() != null ? command.currencyCode() : "")
                    .productType(command.productType() != null ? command.productType() : "0")
                    .overseasExchangeCode(command.overseasExchangeCode() != null ? command.overseasExchangeCode() : "")
                    .domesticExchangeCode(command.domesticExchangeCode() != null ? command.domesticExchangeCode() : "%")
                    .build();

            // 연속 조회 처리
            List<KiwoomCashFlowResponse.CashFlowItem> allItems = kiwoomRateLimiter.fetchAllPages(
                    key -> kiwoomApiClient.getCashFlow(kiwoomRequest, accessToken, key),
                    result -> result.response() != null ? result.response().output() : null,
                    KiwoomCashFlowResult::header);

            for (KiwoomCashFlowResponse.CashFlowItem item : allItems) {
                allEntities.add(cashFlowDbMapper.toEntity(kiwoomApiMapper.toServiceResultItem(item), account));
            }
        }

        // CashFlow 엔티티들 저장
        cashFlowRepository.saveAll(allEntities);

        // 조회 이력 저장 (각 날짜별로)
        List<CashFlowFetchHistory> histories = missingDates.stream()
                .map(date -> new CashFlowFetchHistory(account, date))
                .toList();
        cashFlowHistoryRepository.saveAll(histories);

        log.info("getCashFlow - saved {} items and {} fetch histories to DB",
                allEntities.size(), histories.size());
    }

    private List<LocalDate> findMissingDates(LocalDate startDate, LocalDate endDate, Set<LocalDate> fetchedDates) {
        List<LocalDate> missingDates = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            if (!fetchedDates.contains(current)) {
                missingDates.add(current);
            }
            current = current.plusDays(1);
        }
        return missingDates;
    }

    private void fetchAndSaveFromKiwoom(AssetCommand.RealizedPnl command, Account account, List<LocalDate> missingDates) {
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);

        List<LocalDate> validDates = missingDates.stream()
                .filter(date -> !date.isBefore(oneYearAgo))
                .toList();

        if (validDates.size() < missingDates.size()) {
            log.warn("fetchAndSaveFromKiwoom - 조회할 수 없는 기간입니다. 최근 1년 이내의 데이터만 조회합니다. (제외된 날짜 수: {})",
                    missingDates.size() - validDates.size());
        }

        if (validDates.isEmpty()) {
            return;
        }

        LocalDate minDate = validDates.stream().min(LocalDate::compareTo).orElseThrow();
        LocalDate maxDate = validDates.stream().max(LocalDate::compareTo).orElseThrow();

        // 3개월 단위로 기간 분할하여 API 호출
        List<RealizedPnl> allEntities = new ArrayList<>();
        List<DateRange> dateRanges = splitIntoChunks(minDate, maxDate, MAX_MONTHS_PER_REALIZED_PNL_REQUEST);

        String accessToken = tokenManager.getValidToken(account.getAccountNumber());

        for (DateRange range : dateRanges) {
            log.info("getRealizedPnl - fetching from Kiwoom API: {} ~ {}", range.start(), range.end());

            KiwoomRealizedPnlRequest kiwoomRequest = KiwoomRealizedPnlRequest.builder()
                    .startDate(range.start().format(DateTimeFormatter.BASIC_ISO_DATE))
                    .endDate(range.end().format(DateTimeFormatter.BASIC_ISO_DATE))
                    .stockCode(command.stockCode() != null ? command.stockCode() : "")
                    .build();

            // 연속 조회 처리
            List<KiwoomRealizedPnlResponse.RealizedPnlItem> allItems = kiwoomRateLimiter.fetchAllPages(
                    key -> kiwoomApiClient.getRealizedPnlByPeriod(kiwoomRequest, accessToken, key),
                    result -> result.response() != null ? result.response().output() : null,
                    KiwoomRealizedPnlResult::header);

            for (KiwoomRealizedPnlResponse.RealizedPnlItem item : allItems) {
                allEntities.add(realizedPnlDbMapper.toEntity(kiwoomApiMapper.toServiceResultItem(item), account));
            }
        }

        // RealizedPnl 엔티티들 저장
        realizedPnlRepository.saveAll(allEntities);

        // 조회 이력 저장 (각 날짜별로)
        List<RealizedPnlFetchHistory> histories = validDates.stream()
                .map(date -> new RealizedPnlFetchHistory(account, date))
                .toList();
        realizedPnlHistoryRepository.saveAll(histories);

        log.info("getRealizedPnl - saved {} items and {} fetch histories to DB",
                allEntities.size(), histories.size());
    }

    private List<DateRange> splitIntoChunks(LocalDate start, LocalDate end, int maxMonths) {
        List<DateRange> ranges = new ArrayList<>();
        LocalDate current = start;

        while (!current.isAfter(end)) {
            LocalDate chunkEnd = current.plusMonths(maxMonths).minusDays(1);
            if (chunkEnd.isAfter(end)) {
                chunkEnd = end;
            }
            ranges.add(new DateRange(current, chunkEnd));
            current = chunkEnd.plusDays(1);
        }

        return ranges;
    }

    private record DateRange(LocalDate start, LocalDate end) {}
}
