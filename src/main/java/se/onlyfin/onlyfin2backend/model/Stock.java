package se.onlyfin.onlyfin2backend.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;

/**
 * This class represents the stock table in the database.
 * A stock is a name & ticker - for example, "Apple Inc." and "AAPL"
 * It also contains an owner if it isn't a global stock
 */
@Entity
@Table
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @Column
    @Nullable
    private String ticker;

    @Nullable
    @ManyToOne
    private User owner;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public String getTicker() {
        return ticker;
    }

    public void setTicker(@Nullable String ticker) {
        this.ticker = ticker;
    }

    @Nullable
    public User getOwner() {
        return owner;
    }

    public void setOwner(@Nullable User owner) {
        this.owner = owner;
    }
}
