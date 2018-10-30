package com.example.vaibhavchahal93788.myapplication.billdesk.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class CustomerDetails {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("firstName")
    @Expose
    private Object firstName;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("description")
    @Expose
    private Object description;
    @SerializedName("partnerCategoryId")
    @Expose
    private Integer partnerCategoryId;
    @SerializedName("emailAddressId")
    @Expose
    private Integer emailAddressId;
    @SerializedName("partnerAddressId")
    @Expose
    private Integer partnerAddressId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("isCustomer")
    @Expose
    private Boolean isCustomer;
    @SerializedName("fullName")
    @Expose
    private String fullName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Object getFirstName() {
        return firstName;
    }

    public void setFirstName(Object firstName) {
        this.firstName = firstName;
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

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }

    public Integer getPartnerCategoryId() {
        return partnerCategoryId;
    }

    public void setPartnerCategoryId(Integer partnerCategoryId) {
        this.partnerCategoryId = partnerCategoryId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsCustomer() {
        return isCustomer;
    }

    public void setIsCustomer(Boolean isCustomer) {
        this.isCustomer = isCustomer;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
