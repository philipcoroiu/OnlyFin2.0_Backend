package se.onlyfin.onlyfin2backend.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * This class represents the subscription table in the database.
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
