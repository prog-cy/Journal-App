package com.example.firesbaseauthenticationservice.journalapp.model;

import com.google.firebase.Timestamp;

public class Journal {

    private String title;
    private String thoughts;
    private String imageUrl;

    private String userId;
    private String userName;
    private Timestamp timeAdded;

    //Empty constructor
    public Journal(){

    }

    //Parameterized constructor
    public Journal(String title, String thoughts, String imageUrl, String userId, String userName, Timestamp timeAdded) {
        this.title = title;
        this.thoughts = thoughts;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.userName = userName;
        this.timeAdded = timeAdded;
    }

    //Getters & Setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThoughts() {
        return thoughts;
    }

    public void setThoughts(String thoughts) {
        this.thoughts = thoughts;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }
}
