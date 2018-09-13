package com.example.vaibhavchahal93788.myapplication.billdesk.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AddProductModel implements Serializable {


    @SerializedName("label")
    private String label;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private String price;

    @SerializedName("price_ttc")
    private String priceWithTax;

    @SerializedName("ref")
    private String ref;

    @SerializedName("array_options")
    private AddCategoryModel addCategoryModel;

    @SerializedName("status")
    private String status;

    @SerializedName("status_buy")
    private String status_buy;

    public AddProductModel(String label, String description, String price, String priceWithTax, String ref, AddCategoryModel addCategoryModel, String status, String statusBuy) {
        this.label = label;
        this.description = description;
        this.price = price;
        this.ref = ref;
        this.priceWithTax = priceWithTax;
        this.addCategoryModel = addCategoryModel;
        this.status = status;
        this.status_buy = statusBuy;
    }

    public AddProductModel(String label, String description, String price, AddCategoryModel addCategoryModel) {
        this.label = label;
        this.description = description;
        this.price = price;
        this.addCategoryModel = addCategoryModel;
    }
}
