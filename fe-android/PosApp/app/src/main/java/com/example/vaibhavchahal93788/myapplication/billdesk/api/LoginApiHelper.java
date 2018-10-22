package com.example.vaibhavchahal93788.myapplication.billdesk.api;

import com.example.vaibhavchahal93788.myapplication.billdesk.model.LoginBodyModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.LoginModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.IApiRequestComplete;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.ResponseHandler;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;

public class LoginApiHelper
{
    private loginApi loginApi;

    public LoginApiHelper()
    {
        loginApi = RetrofitClient.getInstance().create(loginApi.class);
    }

    public void validateLogin(LoginBodyModel loginBodyModel, IApiRequestComplete successInterface)
    {
        Call<LoginModel> loginModelCall = loginApi.validateLogin(loginBodyModel);
        loginModelCall.enqueue(new ResponseHandler<LoginModel>(successInterface));
    }
}
