package com.github.skoryupina.homebank.model;

/**
 * Created by Ekaterina on 08.01.2016.
 */
public class Category {
    private int id;
    public String name;
    public String type;
    public static final String INCOME = "INCOME";
    public static final String OUTLAY = "OUTLAY";
    public static final String ORDER = "ORDER";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
