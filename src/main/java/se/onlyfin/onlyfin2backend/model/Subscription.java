package se.onlyfin.onlyfin2backend.model;

import jakarta.persistence.*;

/**
 * This class represents the subscription table in the database.
 */
@Entity
@Table
public class Subscription {
    @Id
    @ManyToOne
    @JoinColumn
    private User subscriber;

    @Id
    @ManyToOne
    @JoinColumn
    private User subscribedTo;

    public User getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(User subscriber) {
        this.subscriber = subscriber;
    }

    public User getSubscribedTo() {
        return subscribedTo;
    }

    public void setSubscribedTo(User subscribedTo) {
        this.subscribedTo = subscribedTo;
    }
}
