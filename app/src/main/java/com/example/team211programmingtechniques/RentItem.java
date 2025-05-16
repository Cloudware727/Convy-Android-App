package com.example.team211programmingtechniques;

import android.graphics.Bitmap;

public class RentItem {
    private String item_id;
    private String item_title;
    private Bitmap photo;
    private int price;
    private String number;
    private String location;
    private String description;
    private String category;

    public RentItem(String item_title, Bitmap photo, int price, String number, String location, String description, String category) {
        this.item_id = item_id;
        this.item_title = item_title;
        this.photo = photo;
        this.price = price;
        this.number = number;
        this.location = location;
        this.description = description;
        this.category = category;

    }

    // Getters
    public String getItemTitle() { return item_title; }
    public Bitmap getPhoto() { return photo; }
    public int getPrice() { return price; }
    public String getNumber() { return number; }
    public String getLocation() { return location; }
    public String getDescription(){return description;}
    public String getCategory(){return category;}
}
