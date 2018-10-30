package com.example.vaibhavchahal93788.myapplication.billdesk.api;

import com.example.vaibhavchahal93788.myapplication.billdesk.model.LoginBodyModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.LoginModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.profile.ProfileResponse;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.IApiRequestComplete;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.ResponseHandler;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.RetrofitClient;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.RetrofitClientProfile;

import java.util.List;

import retrofit2.Call;

public class LoginApiHelper
{
    private loginApi loginApi;

    public LoginApiHelper()
    {
        loginApi = RetrofitClientProfile.getInstance().create(loginApi.class);
    }

    public void userProfile (IApiRequestComplete successInterface){

        Call<ProfileResponse> profileResponseCall = loginApi.getProfileDetails();
        profileResponseCall.enqueue(new ResponseHandler<ProfileResponse>(successInterface));

    }
}
