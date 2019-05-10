package com.example.cykablyat.Model;

public class User {

    private String id;
    private String username;
    private String name;
    private String imageUrl;
    private String bio;
    private String phone;

    public User(String id, String username, String name, String imageUrl, String bio, String phone) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.imageUrl = imageUrl;
        this.bio = bio;
        this.phone = phone;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
