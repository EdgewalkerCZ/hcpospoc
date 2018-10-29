package com.hcin.axelor.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
//@XmlRootElement       //only needed if we also want to generate XML
public class Product extends BaseEntity {

    private String code;
    private Integer productCategoryId;
    private Integer productFamilyId;
    private BigDecimal quantity;
    private String description;
    private String salePrice;
    private Boolean isGst;
    private Boolean isSellable;
	private String name;
    private Integer warrantyNbrOfMonths;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getSalePrice() {
		return salePrice;
	}
	
	public void setSalePrice(String price) {
		this.salePrice = price;
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
	public Integer getWarrantyNbrOfMonths() {
		return warrantyNbrOfMonths;
	}
	public void setWarrantyNbrOfMonths(Integer warrantyNbrOfMonths) {
		this.warrantyNbrOfMonths = warrantyNbrOfMonths;
	}

}