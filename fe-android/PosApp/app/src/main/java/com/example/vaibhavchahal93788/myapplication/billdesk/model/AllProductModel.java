package com.example.vaibhavchahal93788.myapplication.billdesk.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AllProductModel implements Parcelable {

    private long id;
    private String name;
    private String desc;
    private float price;
    private int quantity;
    private String img;
    private int gst;
    private String color;
    private String ram;
    private String rom;
    private int ramExpandable;
    private String category;
    private String currency;

    public String getCategoryID() {
        return categoryId;
    }

    public void setCategoryID(String categoryID) {
        this.categoryId = categoryID;
    }

    private String categoryId;

    private boolean selected;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getGst() {
        return gst;
    }

    public void setGst(int gst) {
        this.gst = gst;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getRom() {
        return rom;
    }

    public void setRom(String rom) {
        this.rom = rom;
    }

    public int getRamExpandable() {
        return ramExpandable;
    }

    public void setRamExpandable(int ramExpandable) {
        this.ramExpandable = ramExpandable;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AllProductModel)) return false;

        AllProductModel product = (AllProductModel) o;

        if (getId() != product.getId()) return false;
        return getName() != null ? getName().equals(product.getName()) : product.getName() == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }



    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.desc);
        dest.writeFloat(this.price);
        dest.writeInt(this.quantity);
        dest.writeString(this.img);
        dest.writeInt(this.gst);
        dest.writeString(this.color);
        dest.writeString(this.ram);
        dest.writeString(this.rom);
        dest.writeInt(this.ramExpandable);
        dest.writeString(this.category);
        dest.writeString(this.currency);

        dest.writeString(this.categoryId);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
    }

    public AllProductModel() {
    }

    protected AllProductModel(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.desc = in.readString();
        this.price = in.readFloat();
        this.quantity = in.readInt();
        this.img = in.readString();
        this.gst = in.readInt();
        this.color = in.readString();
        this.ram = in.readString();
        this.rom = in.readString();
        this.ramExpandable = in.readInt();
        this.category = in.readString();
        this.currency = in.readString();
        this.categoryId = in.readString();
        this.selected = in.readByte() != 0;
    }

    public static final Parcelable.Creator<AllProductModel> CREATOR = new Parcelable.Creator<AllProductModel>() {
        @Override
        public AllProductModel createFromParcel(Parcel source) {
            return new AllProductModel(source);
        }

        @Override
        public AllProductModel[] newArray(int size) {
            return new AllProductModel[size];
        }
    };
}
