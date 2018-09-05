package com.example.vaibhavchahal93788.myapplication.billdesk.network;


public interface IApiRequestComplete<T> {

    public void onSuccess(T response);

    public void onFailure(String message);
}
