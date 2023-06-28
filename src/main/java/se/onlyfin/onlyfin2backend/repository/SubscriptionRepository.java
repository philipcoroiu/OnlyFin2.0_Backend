package se.onlyfin.onlyfin2backend.repository;

import org.springframework.data.repository.CrudRepository;
import se.onlyfin.onlyfin2backend.model.Subscription;
import se.onlyfin.onlyfin2backend.model.SubscriptionId;

public interface SubscriptionRepository extends CrudRepository<Subscription, SubscriptionId> {
}
