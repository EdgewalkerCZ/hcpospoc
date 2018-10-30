package com.example.vaibhavchahal93788.myapplication.billdesk.model.allproduct;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Generated("com.robohorse.robopojogenerator")
public class DataItem implements Serializable {

	@SerializedName("productFamilyId")
	private int productFamilyId;

	@SerializedName("warrantyNbrOfMonths")
	private int warrantyNbrOfMonths;

	@SerializedName("code")
	private String code;

	@SerializedName("productCategoryId")
	private int productCategoryId;

	@SerializedName("quantity")
	private int quantity;

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

	@SerializedName("id")
	private int id;

	private boolean selected;

	public void setProductFamilyId(int productFamilyId){
		this.productFamilyId = productFamilyId;
	}

	public int getProductFamilyId(){
		return productFamilyId;
	}

	public void setWarrantyNbrOfMonths(int warrantyNbrOfMonths){
		this.warrantyNbrOfMonths = warrantyNbrOfMonths;
	}

	public int getWarrantyNbrOfMonths(){
		return warrantyNbrOfMonths;
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

	public void setQuantity(int quantity){
		this.quantity = quantity;
	}

	public int getQuantity(){
		return quantity;
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

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}