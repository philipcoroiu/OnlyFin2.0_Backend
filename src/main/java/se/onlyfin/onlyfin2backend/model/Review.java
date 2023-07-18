package se.onlyfin.onlyfin2backend.model;

import jakarta.persistence.*;

/**
 * This class represents the review table in the database.
 * A review contains the review contents in the form of a string.
 * It also contains information about the author and the target user.
 * There is a constraint on the table that only allows one user to have 1 review per target user at a time.
 */
@Entity
@Table
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String reviewText;

    @ManyToOne
    @JoinColumn
    private User target;

    @ManyToOne
    @JoinColumn
    private User author;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public User getTarget() {
        return target;
    }

    public void setTarget(User target) {
        this.target = target;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
