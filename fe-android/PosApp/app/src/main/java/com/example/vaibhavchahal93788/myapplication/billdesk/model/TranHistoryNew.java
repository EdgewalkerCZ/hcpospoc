package com.example.vaibhavchahal93788.myapplication.billdesk.model;

public class TranHistoryNew {
    private String pName, date;
    private int price, availItem;

    public TranHistoryNew(String pName, String date, int availItem, int price){
        this.pName = pName;
        this.date = date;
        this.availItem = availItem;
        this.price = price;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public int getAvailItem() {
        return availItem;
    }

    public void setAvailItem(int availItem) {
        this.availItem = availItem;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
