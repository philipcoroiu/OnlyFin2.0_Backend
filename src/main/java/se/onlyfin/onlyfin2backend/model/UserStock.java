package se.onlyfin.onlyfin2backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

/**
 * This class represents the user_stock table in the database.
 * A user stock is used for modeling users' dashboard stock tabs.
 * It contains a link to a stock (name & ticker) as well as who owns the user stock.
 * <p>
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

    //"categories" contain all UserCategories that have a foreign key pointing to this UserStock.
    // it is only fetched when requested as fetch-type is set to lazy (reduces load time if categories aren't needed)
    @OneToMany(mappedBy = "userStock", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<UserCategory> categories;

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

    public List<UserCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<UserCategory> categories) {
        this.categories = categories;
    }
}
