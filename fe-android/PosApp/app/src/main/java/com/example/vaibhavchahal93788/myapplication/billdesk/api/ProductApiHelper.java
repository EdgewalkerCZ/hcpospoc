package com.example.vaibhavchahal93788.myapplication.billdesk.api;

import com.example.vaibhavchahal93788.myapplication.billdesk.model.AddProductModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.CategoryModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.LoginBodyModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductCategoryModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.customer.JSONCustomerResponse;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.productsuccess.AddProductResponse;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.profile.ProfileResponse;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.userlogin.LoginSuccessResponse;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.userlogin.UserLoginModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.IApiRequestComplete;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.ResponseHandler;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.RetrofitClient;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Constants;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;


public class ProductApiHelper {

    private com.example.vaibhavchahal93788.myapplication.billdesk.api.productApi productApi;

    public ProductApiHelper() {
        productApi = RetrofitClient.getInstance().create(com.example.vaibhavchahal93788.myapplication.billdesk.api.productApi.class);
    }

    public void fetchProductList(String sortfield, String sortorder, long limit, String category, final IApiRequestComplete successInterface) {
        Call<List<ProductListModel>> productsApiResponseCall = productApi.getProductList(Constants.API_KEY, sortfield, sortorder, limit, category);
        productsApiResponseCall.enqueue(new ResponseHandler<List<ProductListModel>>(successInterface));
    }

    public void fetchCategoryList(String sortfield, String sortorder, long limit, String type, final IApiRequestComplete successInterface) {
        Call<List<CategoryModel>> categoryApiResponseCall = productApi.getCategoryList(Constants.API_KEY, sortfield, sortorder, limit, type);
        categoryApiResponseCall.enqueue(new ResponseHandler<List<CategoryModel>>(successInterface));
    }

    public void addNewProduct(AddProductModel addProductModel, final IApiRequestComplete successInterface) {
        Call<ResponseBody> productsApiResponseCall = productApi.addNewProduct(Constants.API_KEY, addProductModel);
        productsApiResponseCall.enqueue(new ResponseHandler<ResponseBody>(successInterface));
    }

    public void updateProduct(String productId, AddProductModel addProductModel, final IApiRequestComplete successInterface) {
        Call<ResponseBody> productsApiResponseCall = productApi.updateProduct(Constants.API_KEY, productId, addProductModel);
        productsApiResponseCall.enqueue(new ResponseHandler<ResponseBody>(successInterface));
    }


    public void getCategoryList(String sortfield, String sortorder, long limit, String type, final IApiRequestComplete successInterface) {
        Call<List<CategoryModel>> categoryApiResponseCall = productApi.getCategoryList(Constants.API_KEY, sortfield, sortorder, limit, type);
        categoryApiResponseCall.enqueue(new ResponseHandler<List<CategoryModel>>(successInterface));
    }

    public void getCategoryList(final IApiRequestComplete successInterface) {
        Call<ProductCategoryModel> categoryApiResponseCall = productApi.getCategoryList();
        categoryApiResponseCall.enqueue(new ResponseHandler<ProductCategoryModel>(successInterface));
    }


    public void getAllProductList(String category, String subCategory, final IApiRequestComplete successInterface) {
        Call<JsonObject> productsApiResponseCall = productApi.getAllProductList();
        productsApiResponseCall.enqueue(new ResponseHandler<JsonObject>(successInterface));


    }
    public void userProfile (IApiRequestComplete successInterface){

        Call<ProfileResponse> profileResponseCall = productApi.getProfileDetails();
        profileResponseCall.enqueue(new ResponseHandler<ProfileResponse>(successInterface));

    }
//    public void userLogin (String user_data ,IApiRequestComplete successInterface){
//
//        Call<UserLoginModel> userLoginModelCall = productApi.UserLogin(user_data);
//        userLoginModelCall.enqueue(new ResponseHandler<UserLoginModel>(successInterface));
//
//    }
public void userLogin (LoginBodyModel user_data , IApiRequestComplete successInterface){

        Call<LoginSuccessResponse> userLoginModelCall = productApi.UserLogin(user_data);
        userLoginModelCall.enqueue(new ResponseHandler<LoginSuccessResponse>(successInterface));

    }

    public void addProduct(String body, IApiRequestComplete successInterface){
        Call<AddProductResponse> addProductResponseCall=productApi.postAddProduct(body);
        addProductResponseCall.enqueue(new ResponseHandler<AddProductResponse>(successInterface));

    }

    /* remove product */

    public void removeProduct(long productId,String updatedstock ,final IApiRequestComplete successInterface) {
        Call<ProductCategoryModel> categoryApiResponseCall = productApi.removeProduct(productId,updatedstock);
        categoryApiResponseCall.enqueue(new ResponseHandler<ProductCategoryModel>(successInterface));
    }

    public void updateProduct(long productId,String product,final IApiRequestComplete successInterface) {
        Call<ProductCategoryModel> categoryApiResponseCall = productApi.updateProduct(productId,product);
        categoryApiResponseCall.enqueue(new ResponseHandler<ProductCategoryModel>(successInterface));
    }

    //get customers
    public void getCustomers(String sessionid,final IApiRequestComplete requestComplete){
        Call<JSONCustomerResponse> customerResponseCall=productApi.getcustomers(sessionid);
        customerResponseCall.enqueue(new ResponseHandler<JSONCustomerResponse>(requestComplete));

    }
}
