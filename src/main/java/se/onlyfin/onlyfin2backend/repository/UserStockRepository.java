package se.onlyfin.onlyfin2backend.repository;

import org.springframework.data.repository.CrudRepository;
import se.onlyfin.onlyfin2backend.model.UserStock;

import java.util.List;

public interface UserStockRepository extends CrudRepository<UserStock, Integer> {
    List<UserStock> findByUserId(Integer userId);
}
