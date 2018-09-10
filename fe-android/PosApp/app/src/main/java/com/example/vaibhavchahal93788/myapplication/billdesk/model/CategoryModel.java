package com.example.vaibhavchahal93788.myapplication.billdesk.model;

import java.io.Serializable;

public class CategoryModel implements Serializable {

    private String label, id, type;

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
}
