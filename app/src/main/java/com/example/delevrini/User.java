package com.example.delevrini;

import java.util.ArrayList;

public class User {
    private String id, username, email, userType, phone, profileImage;
    private ArrayList<String> favorites;
    private ArrayList<String[]> rating;

    public User() {
    }

    public User(String id, String username, String email, String userType, String phone, String profileImage) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.userType = userType;
        this.phone = phone;
        this.profileImage = profileImage;
    }

    public User(String id, String username, String email, String userType, String phone, String profileImage, ArrayList<String> favorites) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.userType = userType;
        this.phone = phone;
        this.profileImage = profileImage;
        this.favorites = favorites;
    }

    public User(String id, String username, String email, String userType, String phone, ArrayList<String[]> rating, String profileImage) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.userType = userType;
        this.phone = phone;
        this.profileImage = profileImage;
        this.rating = rating;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public ArrayList<String> getFavorites() {
        return favorites;
    }

    public void setFavorites(ArrayList<String> favorites) {
        this.favorites = favorites;
    }


    public ArrayList<String[]> getRating() {
        return rating;
    }

    public void setRating(ArrayList<String[]> rating) {
        this.rating = rating;
    }
}
