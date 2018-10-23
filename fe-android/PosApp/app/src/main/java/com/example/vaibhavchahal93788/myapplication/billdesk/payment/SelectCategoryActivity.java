package com.example.vaibhavchahal93788.myapplication.billdesk.payment;

import android.app.Activity;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.SelectCategoryAdapter;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.AllCategory;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.api.ApiClient;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.api.ApiInterface;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.api.ApiUtils;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Utility;
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

public class SelectCategoryActivity extends AppCompatActivity implements SelectCategoryAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private SelectCategoryAdapter mAdapterSelectCategory;
    private ProgressBar progreeBar;
    private EditText etxtSearch;
    private TextView txtActionBarRight;
    private int itemSelectCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);

        setUpToolbar();
        init();
        fetchCategoryList();
    }

    private void init() {
        itemSelectCount = getIntent().getIntExtra("item_select_count", 0);

        recyclerView = findViewById(R.id.recyclerView);
        progreeBar = findViewById(R.id.progress_bar);
        etxtSearch = findViewById(R.id.etxt_search);

        mAdapterSelectCategory = new SelectCategoryAdapter(this, new ArrayList<AllCategory>(), this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapterSelectCategory);

        txtActionBarRight.setText(itemSelectCount + (itemSelectCount > 1 ? " items" : " item"));

        findViewById(R.id.imv_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etxtSearch.getText().toString().trim())) {
                    Toast.makeText(SelectCategoryActivity.this, "Please enter search text", Toast.LENGTH_LONG).show();
                    return;
                }
                Utility.hideKeyboard(SelectCategoryActivity.this);
                Intent intent = new Intent();
                intent.putExtra("search", etxtSearch.getText().toString().trim());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }


    private void fetchCategoryList() {
        progreeBar.setVisibility(View.VISIBLE);
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<JsonObject> call = apiService.getAllCategoriesList();
        Log.e("request", call.request().url().toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject>call, Response<JsonObject> response) {
                progreeBar.setVisibility(View.GONE);
                Log.e("response", response.body().toString());
                if(ApiUtils.isSuccess(response.body().toString())) {
                    buildUpData(response.body().toString());
                }
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
            JSONArray jsonArray = respObj.getJSONArray("categories");
            List<AllCategory> list = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<AllCategory>>(){}.getType());
            mAdapterSelectCategory.addData(list);
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
        txtActionBarRight = toolbar.findViewById(R.id.txt_right);
        txtActionBarRight.setText("0 item");
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
    public void onExpandCollapse(int position) {
        AllCategory category = mAdapterSelectCategory.getData().get(position);
        category.setSelected(!category.isSelected());
        mAdapterSelectCategory.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(String subCategory) {
        if(!subCategory.equalsIgnoreCase("Mobile Phones")) return;

        Intent intent = new Intent();
        intent.putExtra("subcategory", subCategory);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
