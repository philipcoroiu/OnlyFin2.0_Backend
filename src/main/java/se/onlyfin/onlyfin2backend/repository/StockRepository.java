package se.onlyfin.onlyfin2backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.onlyfin.onlyfin2backend.model.Stock;

public interface StockRepository extends JpaRepository<Stock, Integer> {
}
