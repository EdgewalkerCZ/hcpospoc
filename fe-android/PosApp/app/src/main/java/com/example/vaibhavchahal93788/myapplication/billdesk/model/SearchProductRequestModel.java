package com.example.vaibhavchahal93788.myapplication.billdesk.model;

public class SearchProductRequestModel {

    private String category;

    public SearchProductRequestModel() {
    }

    public SearchProductRequestModel(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
