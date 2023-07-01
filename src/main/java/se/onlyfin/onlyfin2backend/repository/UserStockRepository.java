package se.onlyfin.onlyfin2backend.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import se.onlyfin.onlyfin2backend.model.User;
import se.onlyfin.onlyfin2backend.model.UserStock;

import java.util.List;
import java.util.Set;

public interface UserStockRepository extends CrudRepository<UserStock, Integer> {
    List<UserStock> findByUserId(Integer userId);

    @Query("SELECT DISTINCT user_stock.user " +
            "FROM UserStock user_stock " +
            "WHERE user_stock.stock.id = :stockId")
    Set<User> findUsersByStockId(Integer stockId);
}
