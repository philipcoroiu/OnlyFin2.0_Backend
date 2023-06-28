package se.onlyfin.onlyfin2backend.repository;

import org.springframework.data.repository.CrudRepository;
import se.onlyfin.onlyfin2backend.model.Stock;

public interface StockRepository extends CrudRepository<Stock, Integer> {
}
