package com.example.myapplication;

public class FindRestaurants {
    public String asianorwestern, cuisineone,cuisinetwo,cuisinethree, imagelink , name, mall1, unit1,mall2,unit2,mall3,unit3;
    long id;

    public FindRestaurants(String asianorwestern, String cuisineone, String cuisinetwo, String cuisinethree,
                           String imagelink, String name, String mall1,String mall2,String mall3, String unit1, String unit2, String unit3, long id) {
        this.asianorwestern = asianorwestern;
        this.cuisineone = cuisineone;
        this.cuisinetwo = cuisinetwo;
        this.cuisinethree = cuisinethree;
        this.imagelink = imagelink;
        this.name = name;
        this.mall1 = mall1;
        this.unit1 = unit1;
        this.mall2 = mall2;
        this.unit2 = unit2;
        this.mall3 = mall3;
        this.unit3 = unit3;
        this.id = id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
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

    public void setMall1(String mall1) {
        this.mall1 = mall1;
    }

    public void setUnit1(String unit1) {
        this.unit1 = unit1;
    }

    public void setMall2(String mall2) {
        this.mall2 = mall2;
    }

    public void setUnit2(String unit2) {
        this.unit2 = unit2;
    }

    public void setMall3(String mall3) {
        this.mall3 = mall3;
    }

    public void setUnit3(String unit3) {
        this.unit3 = unit3;
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

    public String getMall1() {
        return mall1;
    }

    public String getUnit1() {
        return unit1;
    }

    public String getMall2() {
        return mall2;
    }

    public String getUnit2() {
        return unit2;
    }

    public String getMall3() {
        return mall3;
    }

    public String getUnit3() {
        return unit3;
    }
}
