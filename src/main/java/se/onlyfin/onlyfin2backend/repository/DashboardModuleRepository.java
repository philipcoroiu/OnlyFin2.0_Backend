package se.onlyfin.onlyfin2backend.repository;

import org.springframework.data.repository.CrudRepository;
import se.onlyfin.onlyfin2backend.model.DashboardModule;

import java.util.List;

public interface DashboardModuleRepository extends CrudRepository<DashboardModule, Integer> {
    List<DashboardModule> findByUserCategoryId(Integer userCategoryId);
}
