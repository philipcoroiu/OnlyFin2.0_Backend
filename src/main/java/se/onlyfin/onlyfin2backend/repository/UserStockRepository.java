package se.onlyfin.onlyfin2backend.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import se.onlyfin.onlyfin2backend.model.Stock;
import se.onlyfin.onlyfin2backend.model.User;
import se.onlyfin.onlyfin2backend.model.UserStock;

import java.util.List;
import java.util.Set;

public interface UserStockRepository extends CrudRepository<UserStock, Integer> {
    List<UserStock> findByUserId(Integer userId);

    @EntityGraph(attributePaths = "stock")
    @Query("""
            FROM UserStock userStock
            WHERE userStock.user.id = :userId
            ORDER BY userStock.stock.name
            """)
    List<UserStock> findByUserIdHydrateStocks(Integer userId);

    @EntityGraph(attributePaths = {"stock", "categories"})
    @Query("""
            FROM UserStock userStock
            WHERE userStock.user.id = :userId
            ORDER BY userStock.stock.name
            """)
    List<UserStock> findByUserIdHydrateStocksAndCategories(Integer userId);

    @Query("""
            SELECT DISTINCT userStock.user
            FROM UserStock userStock
            WHERE (userStock.stock.id = :stockId) AND (userStock.stock.owner IS NULL)
            """)
    Set<User> findUsersByStockId(Integer stockId);

    boolean existsByUserAndStock(User user, Stock stock);
}
