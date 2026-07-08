package app.jaewook.stockmanager.infra.db;

import app.jaewook.stockmanager.domain.Account;
import app.jaewook.stockmanager.domain.CashFlow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CashFlowRepository extends JpaRepository<CashFlow, Long> {

    List<CashFlow> findByAccountAndTradeDateBetweenOrderByTradeDateDescTradeNumberAsc(Account account, LocalDate startDate, LocalDate endDate);
}
