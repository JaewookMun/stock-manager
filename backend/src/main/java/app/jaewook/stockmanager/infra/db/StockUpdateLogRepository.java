package app.jaewook.stockmanager.infra.db;

import app.jaewook.stockmanager.domain.StockUpdateLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockUpdateLogRepository extends JpaRepository<StockUpdateLog, Long> {
}
