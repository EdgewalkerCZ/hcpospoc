package com.hcin.axelor.model;

public class PaymentCondition extends BaseEntity {
    private String fullName;
	private String name;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getFullName() {
		return fullName;
	}
	public void setFullName(String name) {
		this.fullName = name;
	}
}
