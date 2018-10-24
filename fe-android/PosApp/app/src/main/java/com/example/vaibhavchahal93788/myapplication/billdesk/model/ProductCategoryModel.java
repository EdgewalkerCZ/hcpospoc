package com.example.vaibhavchahal93788.myapplication.billdesk.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ProductCategoryModel implements Serializable {

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<CategoryList> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<CategoryList> categories) {
        this.categories = categories;
    }

    private String status;
    private ArrayList<CategoryList> categories;
    }


