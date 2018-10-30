package com.example.vaibhavchahal93788.myapplication.billdesk.model;

public class TranHistoryNew {
    private String pName, date;
    private int price;

    public TranHistoryNew(String pName, String date, int price){
        this.pName = pName;
        this.date = date;
        this.price = price;
    }

    public TranHistoryNew(){}

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
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
