package app.jaewook.stockmanager.infra.db;

import app.jaewook.stockmanager.domain.StockDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockDetailRepository extends JpaRepository<StockDetail, Long> {
}
