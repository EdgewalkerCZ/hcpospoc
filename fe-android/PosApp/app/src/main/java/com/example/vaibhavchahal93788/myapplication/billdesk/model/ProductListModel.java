package com.example.vaibhavchahal93788.myapplication.billdesk.model;

public class ProductListModel {

    private String title;
    private int quantity,price=50;
    private boolean isSelected = false;

    public ProductListModel(String text) {
        this.title = text;
    }

    public String getText() {
        return title;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public boolean isSelected() {
        return isSelected;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
