package edu.muenchnermuseen.entities;

import java.io.Serializable;

/**
 * Created by sfrey on 05.05.2017.
 */

public class Museum implements Serializable {

    private Integer id = -1;
    private String name = "undefined";
    private String description = "undefined";
    private Double lat = 0.0;
    private Double lon = 0.0;
    private Category category = new Category();


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
