package com.example.vaibhavchahal93788.myapplication.billdesk.payment.apiary;


import com.example.vaibhavchahal93788.myapplication.billdesk.model.JSONAddCustomer;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.SearchProductRequestModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.customer.JSONCustomerResponse;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.userlogin.JSONAddCustomerResponse;
import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiInterface {

    @GET("product")
    Call<JsonObject> getAllProductsList(@HeaderMap HashMap<String, String> headerMap);


    @GET("category")
    Call<JsonObject> getAllCategoriesList();

    @POST("mobiles")
    Call<JsonObject> getProductListByCategory(@Body SearchProductRequestModel searchProductRequestModel);

    @POST("brand")
    Call<JsonObject> getProductByBrand(@Body SearchProductRequestModel searchProductRequestModel);

    // list all customers

    @GET("customer")
    Call<JSONCustomerResponse> getallCustomers(@HeaderMap HashMap<String, String> header);


    @PUT("customer")
    Call<JSONAddCustomerResponse> addnewcustomer(@HeaderMap HashMap<String, String> header, @Body JSONAddCustomer jsonArray);


    @POST("customer/{Id}")
    Call<JSONAddCustomerResponse> updatecustomer(@Path("Id") String customerId, @HeaderMap HashMap<String, String> header, @Body JSONAddCustomer jsonArray);
}
