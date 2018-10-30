package com.example.vaibhavchahal93788.myapplication.billdesk.payment;

import android.app.Activity;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.SearchProductAdapter;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.SelectCategoryAdapter;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.AllCategory;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.SearchProductRequestModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.api.ApiClient;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.api.ApiInterface;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.api.ApiUtils;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Utility;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchProductActivity extends AppCompatActivity implements SearchProductAdapter.OnItemClickListener {

    private Context mContext;
    private RecyclerView recyclerView;
    private TextView txtActionBarRight;
    private EditText etxtSearch;
    private SearchProductAdapter mAdapterSearchProduct;
    private boolean running;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);

        mContext = this;

        setUpToolbar();
        init();
    }


    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitle.setText("Search Product");
        txtActionBarRight = toolbar.findViewById(R.id.txt_right);
    }


    private void init() {
        recyclerView = findViewById(R.id.recyclerView);
        etxtSearch = findViewById(R.id.etxt_search);

        mAdapterSearchProduct = new SearchProductAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapterSearchProduct);

        int itemSelectCount = getIntent().getIntExtra("item_select_count", 0);
        txtActionBarRight.setText(itemSelectCount + (itemSelectCount > 1 ? " Items" : " Item"));
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
    protected void onResume() {
        super.onResume();
        running = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        running = false;
    }

    @Override
    public void onItemClick(int position) {
        if (TextUtils.isEmpty(etxtSearch.getText().toString().trim())) return;

        Utility.hideKeyboard(SearchProductActivity.this);
        Intent intent = new Intent();
        intent.putExtra("search", etxtSearch.getText().toString().trim());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    /*private void fetchKeywordList(String keyword) {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<JsonObject> call = apiService.getKeywordList(new SearchProductRequestModel(keyword));
        Log.e("request", call.request().url().toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("response", response.body().toString());
                if (ApiUtils.isSuccess(response.body().toString())) {
                    //buildUpData(response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("response", t.toString());
            }
        });
    }*/
}
