package com.example.myapplication;

public class FindRestaurants {
    public String picture, name, location, type;

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPicture() {
        return picture;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public FindRestaurants(String picture, String name, String location, String type) {
        this.picture = picture;
        this.name = name;
        this.location = location;
        this.type = type;
    }
}
