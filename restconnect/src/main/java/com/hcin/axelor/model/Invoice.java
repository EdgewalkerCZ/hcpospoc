package com.hcin.axelor.model;

import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.TreeSet;

public class Invoice extends BaseEntity {
	private String invoiceId;
    private String invoiceDate;
    private String dueDate;
    private Integer companyId;
    private Integer paymentConditionId;
    private Integer customerId;
    private Integer paymentModeId;
    private SortedSet<ProductItem> invoiceLineIdList;
    private Integer currencyId;
    private BigDecimal companyExTaxTotal;
    private BigDecimal companyTaxTotal;
    private BigDecimal amountRemaining;
    private BigDecimal amountPaid;
    private BigDecimal companyInTaxTotalRemaining;
    private BigDecimal amountRejected;
    private BigDecimal exTaxTotal;
    private BigDecimal directDebitAmount;
    private String note;

	public Invoice() {
		super();
		invoiceLineIdList = new TreeSet<ProductItem>();
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
	public SortedSet<ProductItem> getInvoiceLineIdList() {
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
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}

	public static class ProductItem implements Comparable<ProductItem> {
		private int id;
		private Integer quantity;

		public ProductItem() {
			super();
		}

		public ProductItem(String value) {
			super();

			String idStr = value.substring(0, value.indexOf(":"));
			String qntStr = value.substring(value.indexOf(":") + 1);
			
			id = Integer.parseInt(idStr);
			quantity = Integer.parseInt(qntStr);
		}

		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public Integer getQuantity() {
			return quantity;
		}
		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
		
		@Override
		public int compareTo(ProductItem o) {
			return ((Integer)id).compareTo(o.getId());
		}
		
		@Override
		public String toString() {
			return String.valueOf(id) + ":" + String.valueOf(quantity);
		}
	
	}
}
