package com.example.vaibhavchahal93788.myapplication.billdesk.model;

import java.io.Serializable;

public class  CategoryList implements Serializable {
    private String categoryId;
    private String category;

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
