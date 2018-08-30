package com.example.vaibhavchahal93788.myapplication.billdesk.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductListModel implements Parcelable {

    private String title;
    private int quantity,price=50;
    private boolean isSelected = false;

    public ProductListModel(String text) {
        this.title = text;
    }

    protected ProductListModel(Parcel in) {
        title = in.readString();
        quantity = in.readInt();
        price = in.readInt();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<ProductListModel> CREATOR = new Creator<ProductListModel>() {
        @Override
        public ProductListModel createFromParcel(Parcel in) {
            return new ProductListModel(in);
        }

        @Override
        public ProductListModel[] newArray(int size) {
            return new ProductListModel[size];
        }
    };

    public String getText() {
        return title;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public boolean isSelected() {
        return isSelected;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeInt(quantity);
        parcel.writeInt(price);
        parcel.writeByte((byte) (isSelected ? 1 : 0));
    }
}
