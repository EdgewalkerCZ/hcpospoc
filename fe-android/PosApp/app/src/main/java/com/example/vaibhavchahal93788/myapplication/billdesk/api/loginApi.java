package com.example.vaibhavchahal93788.myapplication.billdesk.api;


import com.example.vaibhavchahal93788.myapplication.billdesk.model.AddProductModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.LoginBodyModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.LoginModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.profile.ProfileResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface loginApi
{
    @POST("login")
    Call<LoginModel> validateLogin(@Body LoginBodyModel loginBodyModel);
    @GET("profiles")
    Call<ProfileResponse> getProfileDetails();
}
