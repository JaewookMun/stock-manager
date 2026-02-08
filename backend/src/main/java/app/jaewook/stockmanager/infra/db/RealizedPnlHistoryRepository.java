package app.jaewook.stockmanager.infra.db;

import app.jaewook.stockmanager.domain.RealizedPnlFetchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RealizedPnlHistoryRepository extends JpaRepository<RealizedPnlFetchHistory, Long> {

    List<RealizedPnlFetchHistory> findByAccountNumberAndTargetDateBetween(
            String accountNumber, LocalDate startDate, LocalDate endDate);

    boolean existsByAccountNumberAndTargetDate(String accountNumber, LocalDate targetDate);
}
