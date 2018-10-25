package com.hcin.axelor.model;

public class PartnerAddress extends BaseEntity {
    private Integer addressId;
    private Integer partnerId;

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
