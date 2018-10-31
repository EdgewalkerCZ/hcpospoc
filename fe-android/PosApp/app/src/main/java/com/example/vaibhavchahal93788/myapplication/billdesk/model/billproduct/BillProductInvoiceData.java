package com.example.vaibhavchahal93788.myapplication.billdesk.model.billproduct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BillProductInvoiceData implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("productCategoryId")
    @Expose
    private Integer productCategoryId;
    @SerializedName("productFamilyId")
    @Expose
    private Integer productFamilyId;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("salePrice")
    @Expose
    private Integer salePrice;
    @SerializedName("isGst")
    @Expose
    private Boolean isGst;
    @SerializedName("isSellable")
    @Expose
    private Boolean isSellable;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("warrantyNbrOfMonths")
    @Expose
    private Integer warrantyNbrOfMonths;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(Integer productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public Integer getProductFamilyId() {
        return productFamilyId;
    }

    public void setProductFamilyId(Integer productFamilyId) {
        this.productFamilyId = productFamilyId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Integer salePrice) {
        this.salePrice = salePrice;
    }

    public Boolean getIsGst() {
        return isGst;
    }

    public void setIsGst(Boolean isGst) {
        this.isGst = isGst;
    }

    public Boolean getIsSellable() {
        return isSellable;
    }

    public void setIsSellable(Boolean isSellable) {
        this.isSellable = isSellable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getWarrantyNbrOfMonths() {
        return warrantyNbrOfMonths;
    }

    public void setWarrantyNbrOfMonths(Integer warrantyNbrOfMonths) {
        this.warrantyNbrOfMonths = warrantyNbrOfMonths;
    }

}