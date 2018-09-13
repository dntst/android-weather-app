package com.weather.app.entities;

public class City implements Entity {

    private int id;
    private String name;

    static public String getDatabaseTableName() {
        return "city";
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
