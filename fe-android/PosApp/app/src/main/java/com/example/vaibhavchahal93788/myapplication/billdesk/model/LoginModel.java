package com.example.vaibhavchahal93788.myapplication.billdesk.model;

import java.io.Serializable;

public class LoginModel implements Serializable
{
    private String JSESSIONID;

    public String getJSESSIONID() {
        return JSESSIONID;
    }

    public void setJSESSIONID(String JSESSIONID) {
        this.JSESSIONID = JSESSIONID;
    }
}
