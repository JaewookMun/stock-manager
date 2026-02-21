package app.jaewook.stockmanager.infra.db;

import app.jaewook.stockmanager.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByCode(String code);
}
