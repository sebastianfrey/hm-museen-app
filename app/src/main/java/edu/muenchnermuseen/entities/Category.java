package edu.muenchnermuseen.entities;

import java.io.Serializable;

/**
 * Created by sfrey on 05.05.2017.
 */

public class Category implements Serializable {

    private Integer id = -1;
    private String name = "undefined";

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
}
