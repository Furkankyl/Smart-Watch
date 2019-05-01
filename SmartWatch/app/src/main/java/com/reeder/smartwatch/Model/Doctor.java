package com.reeder.smartwatch.Model;

public class Doctor {
    private String name;
    private String bio;
    private String photoUrl;

    public Doctor() {
    }

    public Doctor(String name, String bio, String photoUrl) {
        this.name = name;
        this.bio = bio;
        this.photoUrl = photoUrl;
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
}
