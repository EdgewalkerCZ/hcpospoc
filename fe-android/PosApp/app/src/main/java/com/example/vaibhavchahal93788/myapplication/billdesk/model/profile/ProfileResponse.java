package com.example.vaibhavchahal93788.myapplication.billdesk.model.profile;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class ProfileResponse{

	@SerializedName("address")
	private String address;

	@SerializedName("image_url")
	private String imageUrl;

	@SerializedName("is_email_verified")
	private boolean isEmailVerified;

	@SerializedName("store_name")
	private String storeName;

	@SerializedName("merchant")
	private String merchant;

	@SerializedName("language")
	private List<String> language;

	@SerializedName("mobile_number")
	private String mobileNumber;

	@SerializedName("is_mobile_verified")
	private boolean isMobileVerified;

	@SerializedName("email")
	private String email;

	public void setAddress(String address){
		this.address = address;
	}

	public String getAddress(){
		return address;
	}

	public void setImageUrl(String imageUrl){
		this.imageUrl = imageUrl;
	}

	public String getImageUrl(){
		return imageUrl;
	}

	public void setIsEmailVerified(boolean isEmailVerified){
		this.isEmailVerified = isEmailVerified;
	}

	public boolean isIsEmailVerified(){
		return isEmailVerified;
	}

	public void setStoreName(String storeName){
		this.storeName = storeName;
	}

	public String getStoreName(){
		return storeName;
	}

	public void setMerchant(String merchant){
		this.merchant = merchant;
	}

	public String getMerchant(){
		return merchant;
	}

	public void setLanguage(List<String> language){
		this.language = language;
	}

	public List<String> getLanguage(){
		return language;
	}

	public void setMobileNumber(String mobileNumber){
		this.mobileNumber = mobileNumber;
	}

	public String getMobileNumber(){
		return mobileNumber;
	}

	public void setIsMobileVerified(boolean isMobileVerified){
		this.isMobileVerified = isMobileVerified;
	}

	public boolean isIsMobileVerified(){
		return isMobileVerified;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return email;
	}
}