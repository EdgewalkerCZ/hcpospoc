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

    @SerializedName("ref")
    private String ref;


    public AddProductModel(String label, String description, String price, String ref) {
        this.label = label;
        this.description = description;
        this.price = price;
        this.ref = ref;
    }
}
