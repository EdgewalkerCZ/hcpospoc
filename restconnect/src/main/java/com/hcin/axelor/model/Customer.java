package com.hcin.axelor.model;

public class Customer {
    private Integer id;
    private String firstName;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String description;
    private Integer partnerCategoryId;
    private Integer emailAddressId;
    private Integer partnerAddressId;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getFullName() {
		String fullName = name;
		
		if(firstName != null)
			fullName += " " + firstName;
		
		return fullName;
	}
	public Integer getEmailAddressId() {
		return emailAddressId;
	}
	public void setEmailAddressId(Integer emailAddressId) {
		this.emailAddressId = emailAddressId;
	}
	public Integer getPartnerAddressId() {
		return partnerAddressId;
	}
	public void setPartnerAddressId(Integer partnerAddressId) {
		this.partnerAddressId = partnerAddressId;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getPartnerCategoryId() {
		return partnerCategoryId;
	}
	public void setPartnerCategoryId(Integer partnerCategoryId) {
		this.partnerCategoryId = partnerCategoryId;
	}
	
}
