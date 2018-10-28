package com.example.vaibhavchahal93788.myapplication.billdesk.model.customer;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class DataItem{

	@SerializedName("firstName")
	private Object firstName;

	@SerializedName("address")
	private String address;

	@SerializedName("phone")
	private String phone;

	@SerializedName("emailAddressId")
	private int emailAddressId;

	@SerializedName("name")
	private String name;

	@SerializedName("description")
	private Object description;

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

	public void setFirstName(Object firstName){
		this.firstName = firstName;
	}

	public Object getFirstName(){
		return firstName;
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

	public void setDescription(Object description){
		this.description = description;
	}

	public Object getDescription(){
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

	@Override
 	public String toString(){
		return 
			"DataItem{" + 
			"firstName = '" + firstName + '\'' + 
			",address = '" + address + '\'' + 
			",phone = '" + phone + '\'' + 
			",emailAddressId = '" + emailAddressId + '\'' + 
			",name = '" + name + '\'' + 
			",description = '" + description + '\'' + 
			",fullName = '" + fullName + '\'' + 
			",partnerAddressId = '" + partnerAddressId + '\'' + 
			",id = '" + id + '\'' + 
			",email = '" + email + '\'' + 
			",partnerCategoryId = '" + partnerCategoryId + '\'' + 
			"}";
		}
}