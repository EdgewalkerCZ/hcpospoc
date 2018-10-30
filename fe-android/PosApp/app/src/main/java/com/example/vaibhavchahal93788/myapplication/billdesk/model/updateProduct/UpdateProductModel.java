package com.example.vaibhavchahal93788.myapplication.billdesk.model.updateProduct;

import com.google.gson.annotations.SerializedName;

public class UpdateProductModel {
    @SerializedName("productFamilyId")
    private int productFamilyId;

    @SerializedName("warrantyNbrOfMonths")
    private int warrantyNbrOfMonths;

    @SerializedName("quantity")
    private int quantity;



    @SerializedName("id")
    private int id;

    @SerializedName("code")
    private String code;



    @SerializedName("productCategoryId")
    private int productCategoryId;

    @SerializedName("isGst")
    private boolean isGst;

    @SerializedName("salePrice")
    private String salePrice;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("isSellable")
    private boolean isSellable;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setProductFamilyId(int productFamilyId){
        this.productFamilyId = productFamilyId;
    }

    public int getProductFamilyId(){
        return productFamilyId;
    }

    public void setCode(String code){
        this.code = code;
    }

    public String getCode(){
        return code;
    }

    public void setProductCategoryId(int productCategoryId){
        this.productCategoryId = productCategoryId;
    }

    public int getProductCategoryId(){
        return productCategoryId;
    }

    public void setIsGst(boolean isGst){
        this.isGst = isGst;
    }

    public boolean isIsGst(){
        return isGst;
    }

    public void setSalePrice(String salePrice){
        this.salePrice = salePrice;
    }

    public String getSalePrice(){
        return salePrice;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

    public void setIsSellable(boolean isSellable){
        this.isSellable = isSellable;
    }

    public boolean isIsSellable(){
        return isSellable;
    }

    public int getWarrantyNbrOfMonths() { return warrantyNbrOfMonths; }

    public void setWarrantyNbrOfMonths(int warrantyNbrOfMonths) { this.warrantyNbrOfMonths = warrantyNbrOfMonths; }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
}
