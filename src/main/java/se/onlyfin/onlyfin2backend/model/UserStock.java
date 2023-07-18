package se.onlyfin.onlyfin2backend.model;

import jakarta.persistence.*;

/**
 * This class represents the user_stock table in the database.
 * A user stock is used for modeling users' dashboard stock tabs.
 * It contains a link to a stock (name & ticker) as well as who owns the user stock.
 *
 * <br/>
 * (See also: {@link Stock})
 */
@Entity
@Table
public class UserStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Stock stock;

    @ManyToOne
    private User user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
