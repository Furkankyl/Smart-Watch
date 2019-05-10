package com.reeder.smartwatch.Model;

import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

public class User {

    private int age;
    private int weight;
    private int height;
    private String gender;
    private String phoneNumber;
    private String bio;
    private String displayName;

    public User() {
    }

    public User(int age, int weight, int height, String gender, String phoneNumber, String bio) {
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.bio = bio;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }



    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
    /*public HashMap<String,Object> getHashMap(){
        HashMap<String,Object> map = new HashMap<>();
        map.put("age",this.age);
        map.put("weight",this.weight);
        map.put("height",this.height);
        map.put("gender",this.gender);
        map.put("bio",this.bio);
        map.put("phoneNumber",this.phoneNumber);
        return map;
    }
*/
}
