package com.example.vaibhavchahal93788.myapplication.billdesk.payment.api;

import com.example.vaibhavchahal93788.myapplication.billdesk.model.JsonCustomerSet;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("allitems")
    Call<JsonObject> getAllProductsList();

    // list all customers

    @GET("customer")
    Call<JsonCustomerSet> getallCustomers();

    @POST("newcustomer")
    Call<JSONObject> addnewcustomer(String data);

}