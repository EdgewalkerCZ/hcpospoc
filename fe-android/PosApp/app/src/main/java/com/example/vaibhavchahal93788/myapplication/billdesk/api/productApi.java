package com.example.vaibhavchahal93788.myapplication.billdesk.api;

import com.example.vaibhavchahal93788.myapplication.billdesk.model.AddProductModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.CategoryModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductCategoryModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;

import com.google.gson.JsonObject;

import com.example.vaibhavchahal93788.myapplication.billdesk.model.productsuccess.AddProductResponse;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.profile.ProfileResponse;


import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface productApi {
    //http://52.172.129.14:80/api/index.php/products?sortfield=t.ref&sortorder=ASC&limit=100
///products/{id}

    @GET("products")
    Call<List<ProductListModel>> getProductList(@Header("DOLAPIKEY") String dolApiKey, @Query("sortfield") String sortfield, @Query("sortorder") String sortorder,
                                                @Query("limit") long limit, @Query("category") String category);

    @POST("products")
    Call<ResponseBody> addNewProduct(@Header("DOLAPIKEY") String dolApiKey, @Body AddProductModel addProductModel);

  /*  @GET("categories")
    Call<List<CategoryModel>> getCategoryList(@Header("DOLAPIKEY") String dolApiKey, @Query("sortfield") String sortfield, @Query("sortorder") String sortorder,
                                              @Query("limit") long limit, @Query("type") String type);*/


    @GET("categories")
    Call<List<CategoryModel>> getCategoryList(@Header("DOLAPIKEY") String dolApiKey, @Query("sortfield") String sortfield, @Query("sortorder") String sortorder,
                                              @Query("limit") long limit, @Query("type") String type);

    @PUT("products/{id}")
    Call<ResponseBody> updateProduct(@Header("DOLAPIKEY") String dolApiKey, @Path("id") String id, @Body AddProductModel addProductModel);


    @GET("allitems")
    Call<JsonObject> getAllProductList();



    @GET("profiles")
    Call<ProfileResponse> getProfileDetails();

    @Headers("Content-Type: application/json")
    @POST("products")
    Call<AddProductResponse> postAddProduct(@Body String body);


    @GET("category")
    Call<ProductCategoryModel> getCategoryList();
}
