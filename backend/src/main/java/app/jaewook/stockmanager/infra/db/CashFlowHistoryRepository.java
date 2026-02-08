package app.jaewook.stockmanager.infra.db;

import app.jaewook.stockmanager.domain.CashFlowFetchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CashFlowHistoryRepository extends JpaRepository<CashFlowFetchHistory, Long> {

    List<CashFlowFetchHistory> findByAccountNumberAndTargetDateBetween(
            String accountNumber, LocalDate startDate, LocalDate endDate);

    boolean existsByAccountNumberAndTargetDate(String accountNumber, LocalDate targetDate);
}
