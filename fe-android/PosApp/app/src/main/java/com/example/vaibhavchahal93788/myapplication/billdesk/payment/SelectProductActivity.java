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
import com.example.vaibhavchahal93788.myapplication.billdesk.activity.BillDetailActivity;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.SelectProductAdapter;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.AllProduct;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.SearchProductRequestModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.api.ApiClient;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.api.ApiInterface;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.api.ApiUtils;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Utility;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectProductActivity extends AppCompatActivity
        implements SelectProductAdapter.OnItemClickListener, View.OnClickListener {

    private RecyclerView recyclerView;
    private SelectProductAdapter mAdapterSelectProduct;
    private ProgressBar progreeBar;
    private TextView txtActionBarRight;
    private EditText etxtSearch;
    private Set<AllProduct> mDataSelected = new HashSet<>();
    private boolean isSearchingProduct;

    public static final int REQ_CODE_SELECT_CATEGORY = 1;

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
        etxtSearch = findViewById(R.id.etxt_search);

        mAdapterSelectProduct = new SelectProductAdapter(this, new ArrayList<AllProduct>(), this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapterSelectProduct);

        findViewById(R.id.txt_category).setOnClickListener(this);
        findViewById(R.id.btn_continue).setOnClickListener(this);
        findViewById(R.id.imv_search).setOnClickListener(this);
    }


    private void fetchProductsList() {
        progreeBar.setVisibility(View.VISIBLE);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<JsonObject> call = apiService.getAllProductsList();
        Log.e("request", call.request().url().toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                isSearchingProduct = false;
                progreeBar.setVisibility(View.GONE);
                Log.e("response", response.body().toString());
                if (ApiUtils.isSuccess(response.body().toString())) {
                    try {
                        JSONObject respObj = new JSONObject(response.body().toString());
                        JSONArray jsonArray = respObj.getJSONArray("items");
                        Set<AllProduct> set = new Gson().fromJson(jsonArray.toString(), new TypeToken<Set<AllProduct>>() {
                        }.getType());
                        if(set != null) {
                            mAdapterSelectProduct.addData(new ArrayList<>(getDataWithSelectedItems(set, mDataSelected)));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                isSearchingProduct = false;
                progreeBar.setVisibility(View.GONE);
                Log.e("response", t.toString());
            }
        });
    }


    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitle.setText("Select Product");
        txtActionBarRight = toolbar.findViewById(R.id.txt_right);
        txtActionBarRight.setText(mDataSelected.size() + " item");
    }


    @Override
    public void onBackPressed() {
        if(isSearchingProduct) {
            etxtSearch.setText("");
            fetchProductsList();
        } else if(mDataSelected.size() > 0) {
            if(mAdapterSelectProduct.getData() != null) {
                for(AllProduct product : mAdapterSelectProduct.getData()) {
                    product.setSelected(false);
                }
                mDataSelected.clear();
                mAdapterSelectProduct.notifyDataSetChanged();
            }
        } else {
            super.onBackPressed();
        }
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
        AllProduct product = mAdapterSelectProduct.getData().get(position);
        mAdapterSelectProduct.getData().get(position).setSelected(!product.isSelected());

        if(product.isSelected()) {
            mDataSelected.add(product);
        } else {
            mDataSelected.remove(product);
        }

        mAdapterSelectProduct.notifyDataSetChanged();
        txtActionBarRight.setText(mDataSelected.size() + (mDataSelected.size() > 1 ? " items" : " item"));
        if (mDataSelected.size() > 0) {
            findViewById(R.id.ll_bottom_bar).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.ll_bottom_bar).setVisibility(View.GONE);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_SELECT_CATEGORY:
                    //Toast.makeText(SelectProductActivity.this, data.getStringExtra("subcategory"), Toast.LENGTH_LONG).show();
                    fetchProductByCategory(data.getStringExtra("subcategory"), data.getStringExtra("search"));
                    if(!TextUtils.isEmpty(data.getStringExtra("search"))) {
                        etxtSearch.setText(data.getStringExtra("search"));
                    }
                    break;
            }
        }
    }


    private void fetchProductByCategory(String subCategory, String brand) {
        progreeBar.setVisibility(View.VISIBLE);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<JsonObject> call = null;

        if(!TextUtils.isEmpty(brand)) {
            call = apiService.getProductByBrand(new SearchProductRequestModel("samsung"));
        } else {
            call = apiService.getProductListByCategory(new SearchProductRequestModel("mobile"));
        }

        Log.e("request", call.request().url().toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                progreeBar.setVisibility(View.GONE);
                Log.e("response", response.body().toString());
                if (ApiUtils.isSuccess(response.body().toString())) {
                    try {
                        JSONObject respObj = new JSONObject(response.body().toString());
                        JSONArray jsonArray = respObj.getJSONArray("items");
                        Set<AllProduct> set = new Gson().fromJson(jsonArray.toString(), new TypeToken<Set<AllProduct>>() {
                        }.getType());
                        isSearchingProduct = true;
                        mAdapterSelectProduct.addData(new ArrayList<>(getDataWithSelectedItems(set, mDataSelected)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progreeBar.setVisibility(View.GONE);
                Log.e("response", t.toString());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_category:
                Intent intent = new Intent(SelectProductActivity.this, SelectCategoryActivity.class);
                intent.putExtra("item_select_count", mDataSelected.size());
                startActivityForResult(intent, REQ_CODE_SELECT_CATEGORY);
                break;

            case R.id.btn_continue:
                if(mDataSelected.size() == 0) {
                    Toast.makeText(SelectProductActivity.this, "Please select product", Toast.LENGTH_LONG).show();
                    return;
                }
                startActivity(getBillDetailActivityIntent());
                break;

            case R.id.imv_search:
                if(TextUtils.isEmpty(etxtSearch.getText().toString().trim())) {
                    Toast.makeText(SelectProductActivity.this, "Please enter search text", Toast.LENGTH_LONG).show();
                    return;
                }
                Utility.hideKeyboard(SelectProductActivity.this);
                fetchProductByCategory(null, etxtSearch.getText().toString().trim());
                break;
        }
    }


    /**
     * Method to send data to bill details activity
     * @return
     */
    private Intent getBillDetailActivityIntent() {
        ArrayList<ProductListModel> list = new ArrayList<>();
        int totalPrice = 0;
        int totalQuantity = mDataSelected.size();
        for(AllProduct item : mDataSelected) {
            ProductListModel product = new ProductListModel();
            product.setQuantity(1);
            product.setDescription(item.getDesc());
            product.setPrice(""+item.getPrice());
            product.setFinalPrice(""+item.getPrice());
            product.setId(""+item.getId());
            product.setLabel(item.getName());
            product.setTaxPercentage(item.getGstPercent());
            list.add(product);

            totalPrice += item.getPrice();
        }

        Intent intent = new Intent(SelectProductActivity.this, BillDetailActivity.class);
        intent.putExtra("selectedItemList", list);
        intent.putExtra("totalItems", totalQuantity);
        intent.putExtra("totalPrice", totalPrice);

        return intent;
    }


    private Set<AllProduct> getDataWithSelectedItems(Set<AllProduct> setAllData, Set<AllProduct> setSelectedData) {
        Set<AllProduct> set = new HashSet<>(setSelectedData);
        set.retainAll(setAllData);
        set.addAll(setAllData);
        return set;
    }
}
