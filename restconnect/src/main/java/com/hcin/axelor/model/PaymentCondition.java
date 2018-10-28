package com.hcin.axelor.model;

public class PaymentCondition extends BaseEntity {
    private String code;
	private String name;
	private int daySelect;
	private int typeSelect;
	private int paymentTime;

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
	public int getDaySelect() {
		return daySelect;
	}
	public void setDaySelect(int daySelect) {
		this.daySelect = daySelect;
	}
	public int getPaymentTime() {
		return paymentTime;
	}
	public void setPaymentTime(int paymentTime) {
		this.paymentTime = paymentTime;
	}
	public int getTypeSelect() {
		return typeSelect;
	}
	public void setTypeSelect(int typeSelect) {
		this.typeSelect = typeSelect;
	}

}
