package app.jaewook.stockmanager.infra.db;

import app.jaewook.stockmanager.service.dto.StockCommand;
import app.jaewook.stockmanager.service.dto.StockResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockQueryRepositoryTest {

    @Autowired
    StockQueryRepository stockQueryRepository;

    @Test
    void screenStocks_noFilter() {
        StockCommand.Screen command = StockCommand.Screen.builder().build();

        List<StockResult.Screen.StockInfo> result = stockQueryRepository.screenStocks(command);

        assertNotNull(result);
        result.forEach(System.out::println);
        System.out.println("size: " + result.size());
    }

    @Test
    void screenStocks_filterByExchangeType() {
        StockCommand.Screen command = StockCommand.Screen.builder()
                .exchangeType("KOSPI")
                .build();

        List<StockResult.Screen.StockInfo> result = stockQueryRepository.screenStocks(command);

        assertNotNull(result);
        result.forEach(info -> assertEquals("KOSPI", info.exchangeType()));
        System.out.println("KOSPI 종목 수: " + result.size());
    }

    @Test
    void screenStocks_filterByRoeRange() {
        StockCommand.Screen command = StockCommand.Screen.builder()
                .minRoe(BigDecimal.valueOf(10))
                .maxRoe(BigDecimal.valueOf(30))
                .build();

        List<StockResult.Screen.StockInfo> result = stockQueryRepository.screenStocks(command);

        assertNotNull(result);
        result.forEach(info -> {
            if (info.roe() != null) {
                assertTrue(info.roe().compareTo(BigDecimal.valueOf(10)) >= 0,
                        "ROE가 minRoe보다 작음: " + info.roe());
                assertTrue(info.roe().compareTo(BigDecimal.valueOf(30)) <= 0,
                        "ROE가 maxRoe보다 큼: " + info.roe());
            }
        });
        System.out.println("ROE 10~30 종목 수: " + result.size());
    }

    @Test
    void screenStocks_filterByPerRange() {
        StockCommand.Screen command = StockCommand.Screen.builder()
                .minPer(BigDecimal.valueOf(5))
                .maxPer(BigDecimal.valueOf(20))
                .build();

        List<StockResult.Screen.StockInfo> result = stockQueryRepository.screenStocks(command);

        assertNotNull(result);
        result.forEach(info -> {
            if (info.per() != null) {
                assertTrue(info.per().compareTo(BigDecimal.valueOf(5)) >= 0,
                        "PER가 minPer보다 작음: " + info.per());
                assertTrue(info.per().compareTo(BigDecimal.valueOf(20)) <= 0,
                        "PER가 maxPer보다 큼: " + info.per());
            }
        });
        System.out.println("PER 5~20 종목 수: " + result.size());
    }

    @Test
    void screenStocks_filterByAllConditions() {
        StockCommand.Screen command = StockCommand.Screen.builder()
                .exchangeType("KOSPI")
                .minRoe(BigDecimal.valueOf(10))
                .maxRoe(BigDecimal.valueOf(50))
                .minPer(BigDecimal.valueOf(5))
                .maxPer(BigDecimal.valueOf(20))
                .build();

        List<StockResult.Screen.StockInfo> result = stockQueryRepository.screenStocks(command);

        assertNotNull(result);
        result.forEach(info -> {
            assertEquals("KOSPI", info.exchangeType());
            if (info.roe() != null) {
                assertTrue(info.roe().compareTo(BigDecimal.valueOf(10)) >= 0);
                assertTrue(info.roe().compareTo(BigDecimal.valueOf(50)) <= 0);
            }
            if (info.per() != null) {
                assertTrue(info.per().compareTo(BigDecimal.valueOf(5)) >= 0);
                assertTrue(info.per().compareTo(BigDecimal.valueOf(20)) <= 0);
            }
        });
        System.out.println("복합 필터 종목 수: " + result.size());
        result.forEach(System.out::println);
    }

    @Test
    void screenStocks_orderedByMarketCapDesc() {
        StockCommand.Screen command = StockCommand.Screen.builder().build();

        List<StockResult.Screen.StockInfo> result = stockQueryRepository.screenStocks(command);

        assertNotNull(result);
        for (int i = 1; i < result.size(); i++) {
            BigDecimal prev = result.get(i - 1).marketCap();
            BigDecimal curr = result.get(i).marketCap();
            if (prev != null && curr != null) {
                assertTrue(prev.compareTo(curr) >= 0,
                        "시가총액 내림차순 정렬 위반: index=" + (i - 1) + " marketCap=" + prev + ", index=" + i + " marketCap=" + curr);
            }
        }
    }
}
