package se.onlyfin.onlyfin2backend.repository;

import org.springframework.data.repository.CrudRepository;
import se.onlyfin.onlyfin2backend.model.Review;

public interface ReviewRepository extends CrudRepository<Review, Integer> {
}
