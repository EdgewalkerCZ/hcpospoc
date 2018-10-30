package com.example.vaibhavchahal93788.myapplication.billdesk.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InvoiceList {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("invoiceId")
    @Expose
    private String invoiceId;
    @SerializedName("invoiceDate")
    @Expose
    private String invoiceDate;
    @SerializedName("dueDate")
    @Expose
    private String dueDate;
    @SerializedName("companyId")
    @Expose
    private Integer companyId;
    @SerializedName("paymentConditionId")
    @Expose
    private Integer paymentConditionId;
    @SerializedName("customerId")
    @Expose
    private Integer customerId;
    @SerializedName("paymentModeId")
    @Expose
    private Integer paymentModeId;
    @SerializedName("invoiceLineIdList")
    @Expose
    private List<InvoiceLineIdList> invoiceLineIdList = null;
    @SerializedName("currencyId")
    @Expose
    private Integer currencyId;
    @SerializedName("companyExTaxTotal")
    @Expose
    private Integer companyExTaxTotal;
    @SerializedName("companyTaxTotal")
    @Expose
    private Integer companyTaxTotal;
    @SerializedName("amountRemaining")
    @Expose
    private Integer amountRemaining;
    @SerializedName("amountPaid")
    @Expose
    private Integer amountPaid;
    @SerializedName("companyInTaxTotalRemaining")
    @Expose
    private Integer companyInTaxTotalRemaining;
    @SerializedName("amountRejected")
    @Expose
    private Integer amountRejected;
    @SerializedName("exTaxTotal")
    @Expose
    private Double exTaxTotal;
    @SerializedName("directDebitAmount")
    @Expose
    private Integer directDebitAmount;
    @SerializedName("note")
    @Expose
    private String note;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public List<InvoiceLineIdList> getInvoiceLineIdList() {
        return invoiceLineIdList;
    }

    public void setInvoiceLineIdList(List<InvoiceLineIdList> invoiceLineIdList) {
        this.invoiceLineIdList = invoiceLineIdList;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public Integer getCompanyExTaxTotal() {
        return companyExTaxTotal;
    }

    public void setCompanyExTaxTotal(Integer companyExTaxTotal) {
        this.companyExTaxTotal = companyExTaxTotal;
    }

    public Integer getCompanyTaxTotal() {
        return companyTaxTotal;
    }

    public void setCompanyTaxTotal(Integer companyTaxTotal) {
        this.companyTaxTotal = companyTaxTotal;
    }

    public Integer getAmountRemaining() {
        return amountRemaining;
    }

    public void setAmountRemaining(Integer amountRemaining) {
        this.amountRemaining = amountRemaining;
    }

    public Integer getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Integer amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Integer getCompanyInTaxTotalRemaining() {
        return companyInTaxTotalRemaining;
    }

    public void setCompanyInTaxTotalRemaining(Integer companyInTaxTotalRemaining) {
        this.companyInTaxTotalRemaining = companyInTaxTotalRemaining;
    }

    public Integer getAmountRejected() {
        return amountRejected;
    }

    public void setAmountRejected(Integer amountRejected) {
        this.amountRejected = amountRejected;
    }

    public Double getExTaxTotal() {
        return exTaxTotal;
    }

    public void setExTaxTotal(Double exTaxTotal) {
        this.exTaxTotal = exTaxTotal;
    }

    public Integer getDirectDebitAmount() {
        return directDebitAmount;
    }

    public void setDirectDebitAmount(Integer directDebitAmount) {
        this.directDebitAmount = directDebitAmount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}