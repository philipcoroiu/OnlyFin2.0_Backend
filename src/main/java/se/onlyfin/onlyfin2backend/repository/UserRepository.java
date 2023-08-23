package se.onlyfin.onlyfin2backend.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import se.onlyfin.onlyfin2backend.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Repository mapping for the user table.
 */
public interface UserRepository extends CrudRepository<User, Integer> {

    Optional<User> findByEmail(String username);

    Optional<User> findByUsername(String username);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    List<User> findTop20ByisAnalystIsTrueOrderByIdDesc();

    List<User> findTop20ByisAnalystIsTrueAndUsernameContainsIgnoreCase(String searchQuery);

    Optional<User> findByisAnalystIsTrueAndUsernameEquals(String analystUsername);

    @Query("""
            SELECT user.profilePictureId
            FROM User user
            WHERE user.username = :username
            """)
    Integer getProfilePictureIdByUsername(String username);
}
