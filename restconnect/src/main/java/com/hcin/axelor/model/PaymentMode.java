package com.hcin.axelor.model;

public class PaymentMode extends BaseEntity {
    private String name;
    private String code;
    private Integer inOutSelect;

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
	public Integer getInOutSelect() {
		return inOutSelect;
	}
	public void setInOutSelect(Integer inOutSelect) {
		this.inOutSelect = inOutSelect;
	}

}
