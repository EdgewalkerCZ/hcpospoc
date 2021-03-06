package com.example.vaibhavchahal93788.myapplication.billdesk.api;

import com.example.vaibhavchahal93788.myapplication.billdesk.model.AddProductModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.CategoryModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.LoginBodyModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductCategoryModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.SaveHistorySuccessModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.SaveInvoiceModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.addproduct.PostAddProduct;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.allproduct.AllProductResponse;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.customer.JSONCustomerResponse;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.productsuccess.AddProductResponse;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.profile.ProfileResponse;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.userlogin.LoginSuccessResponse;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Constants;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
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

    @GET("categories")
    Call<List<CategoryModel>> getCategoryList(@Header("DOLAPIKEY") String dolApiKey, @Query("sortfield") String sortfield, @Query("sortorder") String sortorder,
                                              @Query("limit") long limit, @Query("type") String type);

    @PUT("products/{id}")
    Call<ResponseBody> updateProduct(@Header("DOLAPIKEY") String dolApiKey, @Path("id") String id, @Body AddProductModel addProductModel);
    @GET("allitems")
    Call<JsonObject> getAllProductList();


    @GET("profiles")
    Call<ProfileResponse> getProfileDetails();

    @GET("category")
    Call<ProductCategoryModel> getCategoryList();


    @PUT("product")
    Call<AddProductResponse> postAddProduct(@HeaderMap HashMap<String,String> headerValues, @Body PostAddProduct body);

    @Headers({"Content-Type: application/json",
              "Accept: application/json"})
    @POST("login")
    Call<LoginSuccessResponse> UserLogin(@Body LoginBodyModel user_data);

    @GET("removeProduct")
    Call<ProductCategoryModel> removeProduct(  @Query("id") long id, @Query("updateStock") String updatestock);

    @GET("updateProduct")
    Call<ProductCategoryModel> updateProduct(@Query("id") long id,@Query("updateStock") String updatestock);

    @GET("product")
    Call<AllProductResponse> getAllProduct(@HeaderMap HashMap<String,String> headerValues);




    @Headers({"Content-Type: application/json",
            "Accept: application/json"})
    @GET(Constants.GET_ALL_CUSTOMER)
    Call<JSONCustomerResponse> getcustomers(@Header(Constants.SESSION_ID) String sid);

    //Save Invoice history
    @Headers({"Content-Type: application/json",
            "Accept: application/json"})
    @POST("invoice")
    Call<SaveHistorySuccessModel> SaveInvoiceHistory(@Query(Constants.SESSION_ID) String sid, @Body SaveInvoiceModel saveInvoice);
}
