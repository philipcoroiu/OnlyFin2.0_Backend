package se.onlyfin.onlyfin2backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import se.onlyfin.onlyfin2backend.model.Subscription;
import se.onlyfin.onlyfin2backend.model.SubscriptionId;
import se.onlyfin.onlyfin2backend.model.User;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, SubscriptionId> {
    @Query("""
            SELECT subscription.id.subscribedTo
            FROM Subscription subscription
            WHERE subscription.id.subscriber = :subscriber
            """)
    List<User> findByIdSubscriber(User subscriber);

    Long countByIdSubscribedTo(User subscribedToUser);
}
