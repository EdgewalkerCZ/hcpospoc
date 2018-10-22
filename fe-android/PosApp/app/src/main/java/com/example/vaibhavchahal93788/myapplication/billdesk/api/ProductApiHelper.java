package com.example.vaibhavchahal93788.myapplication.billdesk.api;

import com.example.vaibhavchahal93788.myapplication.billdesk.model.AddProductModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.CategoryModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;
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

    public void getAllProductList(String category, String subCategory, final IApiRequestComplete successInterface) {
        Call<JsonObject> productsApiResponseCall = productApi.getAllProductList();
        productsApiResponseCall.enqueue(new ResponseHandler<JsonObject>(successInterface));
    }
}
