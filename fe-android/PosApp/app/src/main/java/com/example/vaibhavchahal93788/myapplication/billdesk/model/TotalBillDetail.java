package com.example.vaibhavchahal93788.myapplication.billdesk.model;

public class TotalBillDetail {

    private String title;
    private int totalPrice;

    public TotalBillDetail(String name, int totalPrice) {
        this.title = name;
        this.totalPrice = totalPrice;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
