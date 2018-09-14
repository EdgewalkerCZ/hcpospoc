package com.example.vaibhavchahal93788.myapplication.billdesk.model;

public class BillProduct {

    private String name;
    private int quantity, price, originalPrice, gstTax, finalPrice;

    public BillProduct(String name, int quantity, int price, int gstTax, int finalPrice) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.gstTax = gstTax;
        this.finalPrice = finalPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getGstTax() {
        return gstTax;
    }

    public int getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(int finalPrice) {
        this.finalPrice = finalPrice;
    }
}
