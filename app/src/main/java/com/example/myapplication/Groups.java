package com.example.myapplication;

import com.google.firebase.database.GenericTypeIndicator;

import java.util.HashMap;
import java.util.Map;

public class Groups {

    public String groupName,groupImage;


    public Groups() {
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }


    public Groups(String groupName, String groupImage) {
        this.groupName = groupName;
        this.groupImage = groupImage;
    }
}
