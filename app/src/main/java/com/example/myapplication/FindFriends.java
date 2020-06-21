package com.example.myapplication;

public class FindFriends {

    public String profileimage, fullname, username;

    public FindFriends(){

    }

    public FindFriends(String profileimage, String fullname, String username) {
        this.profileimage = profileimage;
        this.fullname = fullname;
        this.username = username;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public String getFullname() {
        return fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
