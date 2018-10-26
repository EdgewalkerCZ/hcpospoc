package com.hcin.axelor.model;

public class ProductCategory extends BaseEntity {
    private String code;
	private String name;
	private Integer productFamilyId;

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
	public Integer getProductFamilyId() {
		return productFamilyId;
	}
	public void setProductFamilyId(Integer productFamilyId) {
		this.productFamilyId = productFamilyId;
	}
	
	
}
