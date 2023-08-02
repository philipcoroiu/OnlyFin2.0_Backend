package se.onlyfin.onlyfin2backend.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import se.onlyfin.onlyfin2backend.model.UserCategory;

import java.util.List;
import java.util.Optional;

public interface UserCategoryRepository extends JpaRepository<UserCategory, Integer> {
    List<UserCategory> findByUserStockId(Integer userStockId);

    @EntityGraph(attributePaths = "modules")
    @Query("""
            FROM UserCategory category
            WHERE category.id = :userCategoryId
            ORDER BY category.id
            """)
    Optional<UserCategory> findByIdHydrateModules(Integer userCategoryId);

    @EntityGraph(attributePaths = "modules")
    @Query("""
            FROM UserCategory category
            WHERE category.userStock.id = :userStockId
            ORDER BY category.id
            """)
    List<UserCategory> findByUserStockIdHydrateModules(Integer userStockId);
}
