package com.hcin.axelor.model;

public class PartnerAddress {
    private Integer id;
    private Integer addressId;
    private Integer partnerId;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getAddressId() {
		return addressId;
	}
	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}
	public Integer getPartnerId() {
		return partnerId;
	}
	public void setPartnerId(Integer partnerId) {
		this.partnerId = partnerId;
	}
}
