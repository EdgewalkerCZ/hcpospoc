package com.example.vaibhavchahal93788.myapplication.billdesk.payment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.activity.ProductDetailactivity;
import com.example.vaibhavchahal93788.myapplication.billdesk.activity.StockDetailActivity;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.SelectProductAdapter;
import com.example.vaibhavchahal93788.myapplication.billdesk.api.ProductApiHelper;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.AllProduct;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.IApiRequestComplete;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.api.ApiClient;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.api.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectProductActivity extends AppCompatActivity implements SelectProductAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private SelectProductAdapter mAdapterSelectProduct;
    private ProgressBar progreeBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_product);

        setUpToolbar();
        init();
        fetchProductsList();
    }

    private void init() {
        recyclerView = findViewById(R.id.recyclerView);
        progreeBar = findViewById(R.id.progress_bar);

        mAdapterSelectProduct = new SelectProductAdapter(new ArrayList<AllProduct>(), this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapterSelectProduct);
    }


    private void fetchProductsList() {
        progreeBar.setVisibility(View.VISIBLE);
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<JsonObject> call = apiService.getAllProductsList();
        Log.e("request", call.request().url().toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject>call, Response<JsonObject> response) {
                progreeBar.setVisibility(View.GONE);
                Log.e("response", response.body().toString());
                buildUpData(response.body().toString());
            }

            @Override
            public void onFailure(Call<JsonObject>call, Throwable t) {
                progreeBar.setVisibility(View.GONE);
                Log.e("response", t.toString());
            }
        });
    }

    private void buildUpData(String response) {
        try {
            JSONObject respObj = new JSONObject(response);
            JSONArray jsonArray = respObj.getJSONArray("items");
            List<AllProduct> list = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<AllProduct>>(){}.getType());
            mAdapterSelectProduct.addProducts(list);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitle.setText("Select Product");
        TextView txtRight = toolbar.findViewById(R.id.txt_right);
        txtRight.setText("0 item");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(int position) {
        AllProduct product = mAdapterSelectProduct.getAllProducts().get(position);
        mAdapterSelectProduct.getAllProducts().get(position).setSelected(!product.isSelected());
        mAdapterSelectProduct.notifyDataSetChanged();
    }
}
