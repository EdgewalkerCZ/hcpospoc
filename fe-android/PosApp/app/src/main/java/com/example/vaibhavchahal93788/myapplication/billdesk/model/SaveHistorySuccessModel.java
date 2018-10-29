package com.example.vaibhavchahal93788.myapplication.billdesk.model;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveHistorySuccessModel {
    private Integer status;
    private List<SaveHistoryDataModel> data = null;
    private Integer total;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<SaveHistoryDataModel> getData() {
        return data;
    }

    public void setData(List<SaveHistoryDataModel> data) {
        this.data = data;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }



}



