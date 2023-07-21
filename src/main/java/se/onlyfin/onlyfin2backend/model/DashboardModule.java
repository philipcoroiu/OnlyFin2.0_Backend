package se.onlyfin.onlyfin2backend.model;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

/**
 * This class represents the dashboard_module table in the database.
 * A dashboard module contains content in the form of a JSON object.
 * It also contains other metadata required, such as post-date, update-date, and size for the dashboard grid.
 * It also contains a link back to the user category that contains it.
 *
 * <br/>
 * (See also: {@link UserCategory})
 */
@Entity
@Table
public class DashboardModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private UserCategory userCategory;

    @Column(columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode content;

    @Column
    private String moduleType;

    @Column(insertable = false)
    private Instant postDate;

    @Column(insertable = false)
    private Instant updateDate;

    @Column
    private Integer height;

    @Column
    private Integer width;

    @Column
    private Integer x;

    @Column
    private Integer y;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserCategory getUserCategory() {
        return userCategory;
    }

    public void setUserCategory(UserCategory userCategory) {
        this.userCategory = userCategory;
    }

    public JsonNode getContent() {
        return content;
    }

    public void setContent(JsonNode content) {
        this.content = content;
    }

    public String getModuleType() {
        return moduleType;
    }

    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }

    public Instant getPostDate() {
        return postDate;
    }

    public void setPostDate(Instant postDate) {
        this.postDate = postDate;
    }

    public Instant getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Instant updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }
}
