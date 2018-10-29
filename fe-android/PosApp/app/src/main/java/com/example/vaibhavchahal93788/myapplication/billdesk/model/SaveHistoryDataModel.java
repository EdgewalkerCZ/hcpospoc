package com.example.vaibhavchahal93788.myapplication.billdesk.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveHistoryDataModel {

    private Integer id;
    private String invoiceId;
    private String invoiceDate;
    private String dueDate;
    private Integer companyId;
    private Integer paymentConditionId;
    private Integer customerId;
    private Integer paymentModeId;
    private List<Integer> invoiceLineIdList = null;
    private Integer currencyId;
    private Integer companyExTaxTotal;
    private Integer companyTaxTotal;
    private Integer amountRemaining;
    private Integer amountPaid;
    private Integer companyInTaxTotalRemaining;
    private Integer amountRejected;
    private Integer exTaxTotal;
    private Integer directDebitAmount;
    private String note;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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

    public List<Integer> getInvoiceLineIdList() {
        return invoiceLineIdList;
    }

    public void setInvoiceLineIdList(List<Integer> invoiceLineIdList) {
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

    public Integer getExTaxTotal() {
        return exTaxTotal;
    }

    public void setExTaxTotal(Integer exTaxTotal) {
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

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}