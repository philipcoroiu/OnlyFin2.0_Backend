package se.onlyfin.onlyfin2backend.model;

import jakarta.persistence.*;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscription that = (Subscription) o;
        return Objects.equals(subscriber, that.subscriber) && Objects.equals(subscribedTo, that.subscribedTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subscriber, subscribedTo);
    }
}
