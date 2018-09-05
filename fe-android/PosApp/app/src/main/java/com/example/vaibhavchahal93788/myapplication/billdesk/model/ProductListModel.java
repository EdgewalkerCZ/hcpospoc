package com.example.vaibhavchahal93788.myapplication.billdesk.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProductListModel implements Serializable {

    private int quantity;
    private boolean isSelected = false;

    @SerializedName("label")
    private String label;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private String price;

    @SerializedName("tva_tx")
    private String taxPercentage;


    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getLabel() {
        return label;
    }


    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getTaxPercentage() {
        return taxPercentage;
    }

}
