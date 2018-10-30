package com.example.vaibhavchahal93788.myapplication.billdesk.utility;

import com.example.vaibhavchahal93788.myapplication.billdesk.model.customer.JSONCustomerResponse;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("allitems")
    Call<JsonObject> getAllProductsList();

    @GET("category")
    Call<JsonObject> getAllCategoriesList();

    @GET(Constants.GET_ALL_CUSTOMER)
    Call<JSONCustomerResponse> getCustomers(@Query(Constants.SESSION_ID) String sid);

  //  @POST("mobiles")
  //  Call<JsonObject> getProductListByCategory(@Body SearchProductRequestModel searchProductRequestModel);
}
