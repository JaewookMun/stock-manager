package app.jaewook.stockmanager.infra.db;

import app.jaewook.stockmanager.domain.Account;
import app.jaewook.stockmanager.domain.RealizedPnl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RealizedPnlRepository extends JpaRepository<RealizedPnl, Long> {

    List<RealizedPnl> findByAccountAndDateBetweenOrderByDateDescStockCodeAsc(Account account, LocalDate startDate, LocalDate endDate);
}
