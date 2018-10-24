package com.example.vaibhavchahal93788.myapplication.billdesk.utility;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {

    @GET("allitems")
    Call<JsonObject> getAllProductsList();

    @GET("category")
    Call<JsonObject> getAllCategoriesList();

  //  @POST("mobiles")
  //  Call<JsonObject> getProductListByCategory(@Body SearchProductRequestModel searchProductRequestModel);
}
