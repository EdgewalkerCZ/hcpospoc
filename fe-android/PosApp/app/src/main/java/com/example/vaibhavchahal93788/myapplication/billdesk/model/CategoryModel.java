package com.example.vaibhavchahal93788.myapplication.billdesk.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CategoryModel implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("label")
    private String label;

    @SerializedName("type")
    private String type;

    @SerializedName("description")
    private String taxCode;

    @SerializedName("fk_parent")
    private String parent;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getParentType() {
        return parent;
    }
}
