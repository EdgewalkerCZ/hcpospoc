package com.example.vaibhavchahal93788.myapplication.billdesk.model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class JSONAddCustomer{

	@SerializedName("firstName")
	private String firstName;

	@SerializedName("isCustomer")
	private boolean isCustomer;

	@SerializedName("address")
	private String address;

	@SerializedName("phone")
	private String phone;

	@SerializedName("name")
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@SerializedName("id")
	private int id;

	@SerializedName("description")
	private Object description;

	@SerializedName("email")
	private String email;

	@SerializedName("partnerCategoryId")
	private int partnerCategoryId;

	public void setFirstName(String firstName){
		this.firstName = firstName;
	}

	public String getFirstName(){
		return firstName;
	}

	public void setIsCustomer(boolean isCustomer){
		this.isCustomer = isCustomer;
	}

	public boolean isIsCustomer(){
		return isCustomer;
	}

	public void setAddress(String address){
		this.address = address;
	}

	public String getAddress(){
		return address;
	}

	public void setPhone(String phone){
		this.phone = phone;
	}

	public String getPhone(){
		return phone;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setDescription(Object description){
		this.description = description;
	}

	public Object getDescription(){
		return description;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return email;
	}

	public void setPartnerCategoryId(int partnerCategoryId){
		this.partnerCategoryId = partnerCategoryId;
	}

	public int getPartnerCategoryId(){
		return partnerCategoryId;
	}
}