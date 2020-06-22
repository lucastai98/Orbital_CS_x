package com.example.myapplication;

public class FindRestaurants {
    public String asianorwestern, cuisineone,cuisinetwo,cuisinethree, imagelink , name, mall, unit;

    public FindRestaurants(String asianorwestern, String cuisineone, String cuisinetwo, String cuisinethree, String imagelink, String name, String mall, String unit) {
        this.asianorwestern = asianorwestern;
        this.cuisineone = cuisineone;
        this.cuisinetwo = cuisinetwo;
        this.cuisinethree = cuisinethree;
        this.imagelink = imagelink;
        this.name = name;
        this.mall = mall;
        this.unit = unit;
    }
    public FindRestaurants(){

    }

    public void setAsianorwestern(String asianorwestern) {
        this.asianorwestern = asianorwestern;
    }

    public void setCuisineone(String cuisineone) {
        this.cuisineone = cuisineone;
    }

    public void setCuisinetwo(String cuisinetwo) {
        this.cuisinetwo = cuisinetwo;
    }

    public void setCuisinethree(String cuisinethree) {
        this.cuisinethree = cuisinethree;
    }

    public void setImagelink(String imagelink) {
        this.imagelink = imagelink;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMall(String mall) {
        this.mall = mall;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getAsianorwestern() {
        return asianorwestern;
    }

    public String getCuisineone() {
        return cuisineone;
    }

    public String getCuisinetwo() {
        return cuisinetwo;
    }

    public String getCuisinethree() {
        return cuisinethree;
    }

    public String getImagelink() {
        return imagelink;
    }

    public String getName() {
        return name;
    }

    public String getMall() {
        return mall;
    }

    public String getUnit() {
        return unit;
    }
}
