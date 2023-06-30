package se.onlyfin.onlyfin2backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.onlyfin.onlyfin2backend.model.UserCategory;

import java.util.List;

public interface UserCategoryRepository extends JpaRepository<UserCategory, Integer> {
    List<UserCategory> findByUserStockId(Integer userStockId);
}
