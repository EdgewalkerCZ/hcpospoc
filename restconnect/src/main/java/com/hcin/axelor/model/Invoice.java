package com.hcin.axelor.model;

public class Invoice extends BaseEntity {
    private String fullName;

	public String getFullName() {
		return fullName;
	}
	public void setFullName(String name) {
		this.fullName = name;
	}
}
