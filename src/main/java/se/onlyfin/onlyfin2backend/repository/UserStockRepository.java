package se.onlyfin.onlyfin2backend.repository;

import org.hibernate.mapping.Property;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
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
            """)
    List<UserStock> findByUserIdHydrateStocks(Integer userId);

    @EntityGraph(attributePaths = {"stock", "categories"})
    @Query("""
            FROM UserStock userStock
            WHERE userStock.user.id = :userId
            """)
    List<UserStock> findByUserIdHydrateStocksAndCategories(Integer userId);

    @EntityGraph(attributePaths = {"stock", "categories", "categories.modules"})
    @Query("""
            FROM UserStock userStock
            WHERE userStock.user.id = :userId
            """)
    List<UserStock> findByUserIdHydrateStocksAndCategoriesAndModules(Integer userId);

    @Query("""
            SELECT DISTINCT userStock.user
            FROM UserStock userStock
            WHERE userStock.stock.id = :stockId
            """)
    Set<User> findUsersByStockId(Integer stockId);
}
