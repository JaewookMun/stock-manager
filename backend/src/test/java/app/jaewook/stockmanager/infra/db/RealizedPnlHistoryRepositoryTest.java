package app.jaewook.stockmanager.infra.db;

import app.jaewook.stockmanager.domain.RealizedPnlFetchHistory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RealizedPnlHistoryRepositoryTest {
    @Autowired
    RealizedPnlHistoryRepository realizedPnlHistoryRepository;

    @Test
    void t() {
        List<RealizedPnlFetchHistory> pnl = realizedPnlHistoryRepository.findByAccountAndTargetDateBetween(
                null,
                LocalDate.of(2021, 1, 1),
                LocalDate.of(2026, 1, 31));

        pnl.forEach(System.out::println);
    }
}