package com.example.vaibhavchahal93788.myapplication.billdesk.api;

import com.example.vaibhavchahal93788.myapplication.billdesk.model.AddProductModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.IApiRequestComplete;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.ResponseHandler;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.RetrofitClient;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Constants;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;


public class ProductApiHelper {

    private ProductApi productApi;

    public ProductApiHelper() {
        productApi = RetrofitClient.getInstance().create(ProductApi.class);
    }

    public void fetchProductList(String sortfield, String sortorder, long limit, final IApiRequestComplete successInterface) {
        Call<List<ProductListModel>> productsApiResponseCall = productApi.getProductList(Constants.API_KEY, sortfield, sortorder, limit);
        productsApiResponseCall.enqueue(new ResponseHandler<List<ProductListModel>>(successInterface));
    }

    public void addNewProduct(AddProductModel addProductModel, final IApiRequestComplete successInterface) {
        Call<ResponseBody> productsApiResponseCall = productApi.addNewProduct(Constants.API_KEY, addProductModel);
        productsApiResponseCall.enqueue(new ResponseHandler<ResponseBody>(successInterface));
    }
}
