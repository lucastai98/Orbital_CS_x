package com.example.myapplication;

public class RestaurantByLocation {
    long id;
    String name;

    public String getId() {
        return Long.toString(id);
    }

    public void setName(String name) {
        this.name = name;
    }

    public RestaurantByLocation(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public RestaurantByLocation() {

    }

}
