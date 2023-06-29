package se.onlyfin.onlyfin2backend.model;

import jakarta.persistence.*;

@Entity
@Table
public class UserCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    private UserStock userStock;

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

    public UserStock getUserStock() {
        return userStock;
    }

    public void setUserStock(UserStock userStock) {
        this.userStock = userStock;
    }
}
