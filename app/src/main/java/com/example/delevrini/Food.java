package com.example.delevrini;


import java.util.ArrayList;

public class Food {
    private String IdFood,Idowner,Title,Location,Description,Price,Longitude,Latitude;
    private ArrayList<String> ImageLink;



    public Food(String idowner, String idFood, String title, String location, String longitude, String latitude, String description, String price, ArrayList<String> imageLink) {
        Idowner = idowner;
        Title = title;
        Location = location;
        Description = description;
        Price = price;
        IdFood = idFood;
        ImageLink = imageLink;
        Longitude = longitude;
        Latitude = latitude;
    }

    public Food() {
    }

    public Food(String idowner, String idFood, String title, String location, String description, String price) {
        Idowner = idowner;
        Title = title;
        Location = location;
        Description = description;
        Price = price;
        IdFood = idFood;
    }


    public String getIdFood() {
        return IdFood;
    }

    public void setIdFood(String IdFood) {
        this.IdFood = IdFood;
    }

    public String getIdowner() {
        return Idowner;
    }

    public void setIdowner(String Idowner) {
        this.Idowner = Idowner;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }


    public String getPrice() {
        return Price;
    }

    public void setPrice(String Price) {
        this.Price = Price;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }


    public ArrayList<String> getImageLink() {
        return ImageLink;
    }

    public void setImageLink(ArrayList<String> imageLink) {
        ImageLink = imageLink;
    }


}
