package com.example.vaibhavchahal93788.myapplication.billdesk.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AddCategoryModel implements Serializable {

    @SerializedName("options_hcprodcategory")
    private String categoryId;

    @SerializedName("options_testcategory")
    private String categoryName;

    public AddCategoryModel(String categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
}
