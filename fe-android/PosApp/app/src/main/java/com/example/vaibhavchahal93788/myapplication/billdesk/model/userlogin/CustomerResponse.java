package com.example.vaibhavchahal93788.myapplication.billdesk.model.userlogin;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class CustomerResponse{

	@SerializedName("firstName")
	private String firstName;

	@SerializedName("isCustomer")
	private boolean isCustomer;

	@SerializedName("address")
	private String address;

	@SerializedName("phone")
	private String phone;

	@SerializedName("emailAddressId")
	private int emailAddressId;

	@SerializedName("name")
	private String name;

	@SerializedName("description")
	private String description;

	@SerializedName("fullName")
	private String fullName;

	@SerializedName("partnerAddressId")
	private int partnerAddressId;

	@SerializedName("id")
	private int id;

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

	public void setEmailAddressId(int emailAddressId){
		this.emailAddressId = emailAddressId;
	}

	public int getEmailAddressId(){
		return emailAddressId;
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

	public void setFullName(String fullName){
		this.fullName = fullName;
	}

	public String getFullName(){
		return fullName;
	}

	public void setPartnerAddressId(int partnerAddressId){
		this.partnerAddressId = partnerAddressId;
	}

	public int getPartnerAddressId(){
		return partnerAddressId;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
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