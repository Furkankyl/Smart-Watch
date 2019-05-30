package com.reeder.smartwatch.Model;

public class User {
    private String id;
    private int age;
    private int weight;
    private int height;
    private String gender;
    private String phoneNumber;
    private String bio;
    private String displayName;
    private String sosPhoneNumber;

    public User() {
    }

    public User(String id, int age, int weight, int height, String gender, String phoneNumber, String bio, String displayName, String sosPhoneNumber) {
        this.id = id;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.bio = bio;
        this.displayName = displayName;
        this.sosPhoneNumber = sosPhoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getSosPhoneNumber() {
        return sosPhoneNumber;
    }



    public void setSosPhoneNumber(String sosPhoneNumber) {
        this.sosPhoneNumber = sosPhoneNumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", age=" + age +
                ", weight=" + weight +
                ", height=" + height +
                ", gender='" + gender + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", bio='" + bio + '\'' +
                ", displayName='" + displayName + '\'' +
                ", sosPhoneNumber='" + sosPhoneNumber + '\'' +
                '}';
    }
}
