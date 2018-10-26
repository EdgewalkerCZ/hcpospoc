package com.example.vaibhavchahal93788.myapplication.billdesk.model.userlogin;

import com.google.gson.annotations.SerializedName;

public class UserLoginModel {
    public String getJsessionid() {
        return jsessionid;
    }

    public void setJsessionid(String jsessionid) {
        this.jsessionid = jsessionid;
    }

    @SerializedName("JSESSIONID")
    private String jsessionid;

}
