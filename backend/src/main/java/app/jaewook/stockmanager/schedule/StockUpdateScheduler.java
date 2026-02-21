package app.jaewook.stockmanager.schedule;

import app.jaewook.stockmanager.domain.Account;
import app.jaewook.stockmanager.domain.Stock;
import app.jaewook.stockmanager.domain.StockDetail;
import app.jaewook.stockmanager.domain.StockUpdateLog;
import app.jaewook.stockmanager.infra.db.AccountRepository;
import app.jaewook.stockmanager.infra.db.StockDetailRepository;
import app.jaewook.stockmanager.infra.db.StockRepository;
import app.jaewook.stockmanager.infra.db.StockUpdateLogRepository;
import app.jaewook.stockmanager.infra.kiwoom.KiwoomApiClient;
import app.jaewook.stockmanager.infra.kiwoom.KiwoomTokenManager;
import app.jaewook.stockmanager.infra.kiwoom.dto.KiwoomResponseHeader;
import app.jaewook.stockmanager.infra.kiwoom.dto.stock.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockUpdateScheduler {

    private final AccountRepository accountRepository;
    private final StockRepository stockRepository;
    private final StockDetailRepository stockDetailRepository;
    private final StockUpdateLogRepository stockUpdateLogRepository;
    private final KiwoomApiClient kiwoomApiClient;
    private final KiwoomTokenManager tokenManager;

    private static final int API_CALLS_PER_SECOND = 5;
    private static final long RATE_LIMIT_PAUSE_MS = 1000L;

    private record StockData(Stock stock, StockDetail detail) {}

    /**
     * 평일 오후 3:30에 종목 데이터 업데이트
     * KOSPI + KOSDAQ 종목리스트(ka10099) 조회 후 각 종목 기본정보(ka10001) 호출
     */
    @Scheduled(cron = "0 30 15 * * MON-FRI")
    public void updateStocks() {
        log.info("StockUpdateScheduler - 종목 데이터 업데이트 시작");

        StockUpdateLog updateLog = StockUpdateLog.start();
        stockUpdateLogRepository.save(updateLog);

        try {
            Account account = accountRepository.findAll().stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No account found"));

            String accessToken = tokenManager.getValidToken(account.getAccountNumber());

            List<StockData> allStockData = new ArrayList<>();

            // KOSPI 종목 조회
            List<KiwoomStockInfoResponse.StockInfoItem> kospiItems = fetchAllStockInfoPages(MarketType.KOSPI, accessToken);
            log.info("StockUpdateScheduler - KOSPI 종목 수: {}", kospiItems.size());

            fetchStockDetails(kospiItems, MarketType.KOSPI.name(), accessToken, allStockData);

            // KOSDAQ 종목 조회
            List<KiwoomStockInfoResponse.StockInfoItem> kosdaqItems = fetchAllStockInfoPages(MarketType.KOSDAQ, accessToken);
            log.info("StockUpdateScheduler - KOSDAQ 종목 수: {}", kosdaqItems.size());

            fetchStockDetails(kosdaqItems, MarketType.KOSDAQ.name(), accessToken, allStockData);

            // DB 저장
            saveOrUpdateStocks(allStockData);

            updateLog.success(kospiItems.size(), kosdaqItems.size(), allStockData.size());
            stockUpdateLogRepository.save(updateLog);

            log.info("StockUpdateScheduler - 종목 데이터 업데이트 완료. 총 {}건 저장", allStockData.size());
        } catch (Exception e) {
            updateLog.fail(e.getMessage());
            stockUpdateLogRepository.save(updateLog);

            log.error("StockUpdateScheduler - 종목 데이터 업데이트 실패", e);
        }
    }

    /**
     * ka10099 연속 조회로 전체 종목리스트 조회
     */
    private List<KiwoomStockInfoResponse.StockInfoItem> fetchAllStockInfoPages(MarketType marketType, String accessToken) {
        List<KiwoomStockInfoResponse.StockInfoItem> allItems = new ArrayList<>();
        String nextKey = null;
        boolean hasNext = true;

        KiwoomStockInfoRequest request = KiwoomStockInfoRequest.builder()
                .marketType(marketType)
                .build();

        while (hasNext) {
            KiwoomStockInfoResult result = kiwoomApiClient.getStockInfoList(request, accessToken, nextKey);
            KiwoomStockInfoResponse response = result.response();
            KiwoomResponseHeader header = result.header();

            if (response != null && response.list() != null) {
                allItems.addAll(response.list());
            }

            hasNext = header.hasNext();
            nextKey = header.nextKey();
        }

        return allItems;
    }

    /**
     * 종목 리스트에 대해 ka10001을 초당 5회 제한으로 호출
     */
    private void fetchStockDetails(List<KiwoomStockInfoResponse.StockInfoItem> items,
                                   String marketType, String accessToken, List<StockData> result) {
        for (int i = 0; i < items.size(); i++) {
            if (i > 0 && i % API_CALLS_PER_SECOND == 0) {
                try {
                    // TODO: RateLimiter를 활용한 방식으로 교체
                    Thread.sleep(RATE_LIMIT_PAUSE_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("StockUpdateScheduler - 스레드 인터럽트 발생, 업데이트 중단");
                    return;
                }
            }

            StockData stockData = fetchAndBuildStockData(items.get(i), marketType, accessToken);
            if (stockData != null) {
                result.add(stockData);
            }

            if ((i + 1) % 100 == 0) {
                log.info("StockUpdateScheduler - {} 진행: {}/{}", marketType, i + 1, items.size());
            }
        }
    }

    /**
     * ka10001 호출하여 Stock + StockDetail 데이터 생성
     */
    private StockData fetchAndBuildStockData(KiwoomStockInfoResponse.StockInfoItem item, String marketType, String accessToken) {
        try {
            KiwoomStockBasicInfoRequest basicInfoRequest = KiwoomStockBasicInfoRequest.builder()
                    .stockCode(item.code())
                    .build();

            KiwoomStockBasicInfoResponse basicInfo = kiwoomApiClient.getStockBasicInfo(basicInfoRequest, accessToken);

            if (basicInfo == null || basicInfo.resultCode() != 0) {
                log.warn("StockUpdateScheduler - ka10001 실패: code={}", item.code());
                return null;
            }

            Stock stock = Stock.builder()
                    .code(item.code())
                    .name(item.name())
                    .marketType(marketType)
                    .sectorName(item.upName())
                    .build();

            StockDetail detail = StockDetail.builder()
                    .currentPrice(parseBigDecimal(basicInfo.currentPrice()))
                    .marketCap(parseBigDecimal(basicInfo.marketCap()))
                    .per(parseBigDecimal(basicInfo.per()))
                    .roe(parseBigDecimal(basicInfo.roe()))
                    .pbr(parseBigDecimal(basicInfo.pbr()))
                    .eps(parseBigDecimal(basicInfo.eps()))
                    .bps(parseBigDecimal(basicInfo.bps()))
                    .operatingProfit(parseBigDecimal(basicInfo.operatingProfit()))
                    .salesAmount(parseBigDecimal(basicInfo.salesAmount()))
                    .tradingVolume(parseLong(basicInfo.tradingVolume()))
                    .updatedAt(LocalDateTime.now())
                    .build();

            return new StockData(stock, detail);
        } catch (Exception e) {
            log.error("StockUpdateScheduler - 종목 정보 조회 실패: code={}, error={}", item.code(), e.getMessage());
            return null;
        }
    }

    /**
     * Stock: 기존 매핑 후 신규만 batch insert
     * StockDetail: 전체 삭제 후 batch insert
     */
    private void saveOrUpdateStocks(List<StockData> stockDataList) {
        // 1. 기존 Stock 전체 조회 → Map<code, Stock>
        Map<String, Stock> existingStockMap = stockRepository.findAll().stream()
                .collect(Collectors.toMap(Stock::getCode, Function.identity()));

        // 2. 신규 Stock batch insert
        List<Stock> newStocks = stockDataList.stream()
                .filter(data -> !existingStockMap.containsKey(data.stock().getCode()))
                .map(StockData::stock)
                .toList();

        stockRepository.saveAll(newStocks)
                .forEach(s -> existingStockMap.put(s.getCode(), s));

        // 3. StockDetail batch insert
        List<StockDetail> allDetails = stockDataList.stream()
                .map(data -> StockDetail.builder()
                        .stock(existingStockMap.get(data.stock().getCode()))
                        .currentPrice(data.detail().getCurrentPrice())
                        .marketCap(data.detail().getMarketCap())
                        .per(data.detail().getPer())
                        .roe(data.detail().getRoe())
                        .pbr(data.detail().getPbr())
                        .eps(data.detail().getEps())
                        .bps(data.detail().getBps())
                        .operatingProfit(data.detail().getOperatingProfit())
                        .salesAmount(data.detail().getSalesAmount())
                        .tradingVolume(data.detail().getTradingVolume())
                        .updatedAt(data.detail().getUpdatedAt())
                        .build())
                .toList();

        stockDetailRepository.saveAll(allDetails);
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return 0L;
        }
        try {
            return Long.parseLong(value.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
