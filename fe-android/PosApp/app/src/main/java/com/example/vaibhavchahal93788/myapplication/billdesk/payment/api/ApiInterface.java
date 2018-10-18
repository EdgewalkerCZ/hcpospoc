package com.example.vaibhavchahal93788.myapplication.billdesk.payment.api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("/allitems")
    Call<JsonObject> getAllProductsList();

}
