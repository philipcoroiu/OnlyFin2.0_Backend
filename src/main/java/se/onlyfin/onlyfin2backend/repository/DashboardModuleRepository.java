package se.onlyfin.onlyfin2backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.onlyfin.onlyfin2backend.model.DashboardModule;

import java.util.List;

public interface DashboardModuleRepository extends JpaRepository<DashboardModule, Integer> {
    List<DashboardModule> findByUserCategoryId(Integer userCategoryId);
}
