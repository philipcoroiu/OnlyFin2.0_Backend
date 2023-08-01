package se.onlyfin.onlyfin2backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.onlyfin.onlyfin2backend.model.Stock;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Integer> {
    List<Stock> findByNameContainingIgnoreCaseAndOwnerIsNull(String name);

    List<Stock> findAllByOwnerIsNull();
}
