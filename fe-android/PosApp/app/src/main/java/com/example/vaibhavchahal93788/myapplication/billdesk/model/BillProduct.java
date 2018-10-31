package com.example.vaibhavchahal93788.myapplication.billdesk.model;

import android.os.Parcel;
import android.os.Parcelable;

public class BillProduct implements Parcelable {

    private String name;
    private int id, quantity, price, originalPrice, gstTax, finalPrice;

    public BillProduct(int id, String name, int quantity, int price, int gstTax, int finalPrice) {
        this.name = name;
        this.id = id;
        this.quantity = quantity;
        this.price = price;
        this.gstTax = gstTax;
        this.finalPrice = finalPrice;
    }

    protected BillProduct(Parcel in) {
        id = in.readInt();
        name = in.readString();
        quantity = in.readInt();
        price = in.readInt();
        originalPrice = in.readInt();
        gstTax = in.readInt();
        finalPrice = in.readInt();
    }

    public static final Creator<BillProduct> CREATOR = new Creator<BillProduct>() {
        @Override
        public BillProduct createFromParcel(Parcel in) {
            return new BillProduct(in);
        }

        @Override
        public BillProduct[] newArray(int size) {
            return new BillProduct[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getGstTax() {
        return gstTax;
    }

    public int getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(int finalPrice) {
        this.finalPrice = finalPrice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeInt(quantity);
        parcel.writeInt(price);
        parcel.writeInt(originalPrice);
        parcel.writeInt(gstTax);
        parcel.writeInt(finalPrice);
    }
}
