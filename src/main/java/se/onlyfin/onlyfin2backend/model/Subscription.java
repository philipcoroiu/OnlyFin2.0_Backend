package se.onlyfin.onlyfin2backend.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * This class represents the subscription table in the database.
 * A subscription is made using the subscribing user & target user as the key.
 * As the subscription table uses composite keys, it is required by JPA
 * to create a separate class which models the composite key: {@link SubscriptionId}
 */
@Entity
@Table
public class Subscription {
    @EmbeddedId
    private SubscriptionId id;

    public SubscriptionId getId() {
        return id;
    }

    public void setId(SubscriptionId id) {
        this.id = id;
    }
}
