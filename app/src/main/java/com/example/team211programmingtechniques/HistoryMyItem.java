package com.example.team211programmingtechniques;

import android.graphics.Bitmap;

public class HistoryMyItem {
    private int item_id;
    private String item_name;
    private String description;
    private int price;
    private String date_listed;
    private String category;
    private Boolean status;
    private Bitmap photo;

    public HistoryMyItem(int item_id, String item_name, String description, int price, String date_listed, String category, Boolean status, Bitmap photo) {
        this.item_id = item_id;
        this.item_name = item_name;
        this.description = description;
        this.price = price;
        this.date_listed = date_listed;
        this.category = category;
        this.status = status;
        this.photo = photo;

    }

    // Getters
    public int getItem_id(){return item_id;}
    public String getItemName() { return item_name; }
    public String getDescription(){return description;}
    public int getPrice() { return price; }
    public String getDateListed() {return date_listed;};
    public String getCategory(){return category;}
    public Boolean getStatus() {return status;};
    public Bitmap getPhoto(){return photo;};

    // Setter
    public void setStatus(Boolean newStatus){status = newStatus;}
}
