package se.onlyfin.onlyfin2backend.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;
import java.util.Objects;

/**
 * This class is the model of the composite key used in the subscription table.
 * It contains a subscriber and a subscribed-to user which together form the composite key.
 */
@Embeddable
public class SubscriptionId implements Serializable {
    @ManyToOne
    @JoinColumn
    private User subscriber;

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
        SubscriptionId that = (SubscriptionId) o;
        return Objects.equals(subscriber, that.subscriber) && Objects.equals(subscribedTo, that.subscribedTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subscriber, subscribedTo);
    }
}
