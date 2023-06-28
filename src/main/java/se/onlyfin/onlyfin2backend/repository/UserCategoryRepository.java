package se.onlyfin.onlyfin2backend.repository;

import org.springframework.data.repository.CrudRepository;
import se.onlyfin.onlyfin2backend.model.UserCategory;

public interface UserCategoryRepository extends CrudRepository<UserCategory, Integer> {
}
