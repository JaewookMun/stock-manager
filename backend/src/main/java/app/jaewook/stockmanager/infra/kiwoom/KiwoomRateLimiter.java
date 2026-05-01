package app.jaewook.stockmanager.infra.kiwoom;

import app.jaewook.stockmanager.infra.kiwoom.dto.KiwoomResponseHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.IntConsumer;

@Slf4j
@Component
public class KiwoomRateLimiter {

    private static final int API_CALLS_PER_SECOND = 5;
    private static final long RATE_LIMIT_PAUSE_MS = 1000L;
    static final long INTERVAL_MS = RATE_LIMIT_PAUSE_MS / API_CALLS_PER_SECOND; // 200ms

    /**
     * 페이지네이션 루프: 응답 헤더의 hasNext/nextKey를 따라 모든 페이지를 순차 조회.
     * 첫 호출은 즉시, 이후 호출은 INTERVAL_MS(200ms) 지연하여 초당 5회 제한 준수.
     */
    public <Result, Item> List<Item> fetchAllPages(
            Function<String, Result> pageCall,
            Function<Result, List<Item>> itemsExtractor,
            Function<Result, KiwoomResponseHeader> headerExtractor) {

        List<Item> allItems = new ArrayList<>();
        String nextKey = null;
        boolean hasNext = true;
        int callIndex = 0;

        try (ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()) {
            while (hasNext) {
                final String key = nextKey;
                long delay = callIndex == 0 ? 0L : INTERVAL_MS;

                Result result = scheduler.schedule(
                        () -> pageCall.apply(key),
                        delay,
                        TimeUnit.MILLISECONDS
                ).get();

                List<Item> items = itemsExtractor.apply(result);
                if (items != null) {
                    allItems.addAll(items);
                }

                KiwoomResponseHeader header = headerExtractor.apply(result);
                hasNext = header.hasNext();
                nextKey = header.nextKey();
                callIndex++;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Kiwoom API 호출 중 인터럽트 발생", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Kiwoom API 호출 실패", e.getCause());
        }

        log.info("fetchAllPages - total items fetched: {}", allItems.size());
        return allItems;
    }

    /**
     * 팬아웃 스케줄링: N개 작업을 index * INTERVAL_MS 간격으로 예약하여 초당 5회 제한 준수.
     * ExecutionException 발생 시 해당 항목을 건너뛰고 계속 진행.
     */
    public <T, R> List<R> scheduleAll(List<T> items, Function<T, R> task, IntConsumer onItemComplete) {
        int total = items.size();
        List<Future<R>> futures = new ArrayList<>(total);
        List<R> results = new ArrayList<>();

        try (ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()) {
            for (int i = 0; i < total; i++) {
                final T item = items.get(i);
                futures.add(scheduler.schedule(() -> task.apply(item), (long) i * INTERVAL_MS, TimeUnit.MILLISECONDS));
            }

            for (int i = 0; i < total; i++) {
                try {
                    R result = futures.get(i).get();
                    if (result != null) {
                        results.add(result);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("KiwoomRateLimiter - 스레드 인터럽트 발생, 처리 중단 (완료: {}/{})", i, total);
                    return results;
                } catch (ExecutionException e) {
                    log.error("KiwoomRateLimiter - 작업 실패: error={}", e.getMessage());
                }

                if (onItemComplete != null) {
                    onItemComplete.accept(i);
                }
            }
        }

        return results;
    }
}
