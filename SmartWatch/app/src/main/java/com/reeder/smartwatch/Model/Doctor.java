package com.reeder.smartwatch.Model;

public class Doctor {
    private String id;
    private String name;
    private String bio;
    private String photoUrl;
    private String phoneNumber;
    private String token;
    public Doctor() {
    }

    public Doctor(String id, String name, String bio, String photoUrl, String phoneNumber, String token) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.photoUrl = photoUrl;
        this.phoneNumber = phoneNumber;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
