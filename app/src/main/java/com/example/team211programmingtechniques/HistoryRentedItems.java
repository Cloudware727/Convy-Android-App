package com.example.team211programmingtechniques;

public class HistoryRentedItems {
    private String itemName;
    private String category;
    private String lenderUsername;
    private String dateOfReturn;
    private int price;

    public HistoryRentedItems(String itemName, String category, String lenderUsername, String dateOfReturn, int price) {
        this.itemName = itemName;
        this.category = category;
        this.lenderUsername = lenderUsername;
        this.dateOfReturn = dateOfReturn;
        this.price = price;
    }

    // Getters
    public String getItemName() { return itemName; }
    public String getCategory() { return category; }
    public String getLenderUsername() { return lenderUsername; }
    public String getDateOfReturn() { return dateOfReturn; }
    public int getPrice() { return price; }

    // Setters
    public void setItemName(String itemName) { this.itemName = itemName; }
    public void setCategory(String category) { this.category = category; }
    public void setLenderUsername(String lenderUsername) { this.lenderUsername = lenderUsername; }
    public void setDateOfReturn(String dateOfReturn) { this.dateOfReturn = dateOfReturn; }
    public void setPrice(int price) { this.price = price; }
}

