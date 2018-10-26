package com.hcin.axelor.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Invoice extends BaseEntity {
	private String invoiceId;
    private String invoiceDate;
    private String dueDate;
    private Integer companyId;
    private Integer paymentConditionId;
    private Integer customerId;
    private Integer paymentModeId;
    private Integer operationTypeSelect;
    private Integer statusSelect;
    private List<Integer> invoiceLineIdList;
    private Integer currencyId;
    private BigDecimal companyExTaxTotal;
    private BigDecimal companyTaxTotal;
    private BigDecimal amountRemaining;
    private BigDecimal amountPaid;
    private BigDecimal companyInTaxTotalRemaining;
    private BigDecimal amountRejected;
    private BigDecimal exTaxTotal;
    private BigDecimal directDebitAmount;

	public Invoice() {
		super();
		invoiceLineIdList = new ArrayList<Integer>();
	}
	public String getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}
	public String getInvoiceDate() {
		return invoiceDate;
	}
	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	public String getDueDate() {
		return dueDate;
	}
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	public Integer getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}
	public Integer getPaymentConditionId() {
		return paymentConditionId;
	}
	public void setPaymentConditionId(Integer paymentConditionId) {
		this.paymentConditionId = paymentConditionId;
	}
	public Integer getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}
	public Integer getPaymentModeId() {
		return paymentModeId;
	}
	public void setPaymentModeId(Integer paymentModeId) {
		this.paymentModeId = paymentModeId;
	}
	public Integer getOperationTypeSelect() {
		return operationTypeSelect;
	}
	public void setOperationTypeSelect(Integer operationTypeSelect) {
		this.operationTypeSelect = operationTypeSelect;
	}
	public Integer getStatusSelect() {
		return statusSelect;
	}
	public void setStatusSelect(Integer statusSelect) {
		this.statusSelect = statusSelect;
	}
	public List<Integer> getInvoiceLineIdList() {
		return invoiceLineIdList;
	}
	public Integer getCurrencyId() {
		return currencyId;
	}
	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
	}
	public BigDecimal getCompanyExTaxTotal() {
		return companyExTaxTotal;
	}
	public void setCompanyExTaxTotal(BigDecimal companyExTaxTotal) {
		this.companyExTaxTotal = companyExTaxTotal;
	}
	public BigDecimal getCompanyTaxTotal() {
		return companyTaxTotal;
	}
	public void setCompanyTaxTotal(BigDecimal companyTaxTotal) {
		this.companyTaxTotal = companyTaxTotal;
	}
	public BigDecimal getAmountRemaining() {
		return amountRemaining;
	}
	public void setAmountRemaining(BigDecimal amountRemaining) {
		this.amountRemaining = amountRemaining;
	}
	public BigDecimal getAmountPaid() {
		return amountPaid;
	}
	public void setAmountPaid(BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}
	public BigDecimal getCompanyInTaxTotalRemaining() {
		return companyInTaxTotalRemaining;
	}
	public void setCompanyInTaxTotalRemaining(BigDecimal companyInTaxTotalRemaining) {
		this.companyInTaxTotalRemaining = companyInTaxTotalRemaining;
	}
	public BigDecimal getAmountRejected() {
		return amountRejected;
	}
	public void setAmountRejected(BigDecimal amountRejected) {
		this.amountRejected = amountRejected;
	}
	public BigDecimal getExTaxTotal() {
		return exTaxTotal;
	}
	public void setExTaxTotal(BigDecimal exTaxTotal) {
		this.exTaxTotal = exTaxTotal;
	}
	public BigDecimal getDirectDebitAmount() {
		return directDebitAmount;
	}
	public void setDirectDebitAmount(BigDecimal directDebitAmount) {
		this.directDebitAmount = directDebitAmount;
	}
}
