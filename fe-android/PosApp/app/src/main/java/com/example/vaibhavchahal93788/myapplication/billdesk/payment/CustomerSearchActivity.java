package com.example.vaibhavchahal93788.myapplication.billdesk.payment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.CustomerListAdaptor;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.ProductStockListAdapter;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.JsonCustomer;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.JsonCustomerSet;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.api.ApiClient;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.api.ApiInterface;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.KeyValue;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Utility;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerSearchActivity extends AppCompatActivity {
    EditText mCustomerSearchEdtTxt;
    ImageView mCustomerSeachIcon;
    RecyclerView mCustomerRecyclerList;
    CustomerListAdaptor customerListAdaptor;
    JsonCustomerSet customerSets;
    ArrayList<JsonCustomer> filtercustomername;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_search);
        getToolbar();
        bindView();
        fetchCustomerList();
        Utility.hideKeyboard(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Utility.hideKeyboard(CustomerSearchActivity.this,mCustomerSearchEdtTxt);

//        fetchCustomerList();
    }

    public void fetchCustomerList() {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<JsonCustomerSet> call = apiService.getallCustomers();
        Log.e("request", call.request().url().toString());
        call.enqueue(new Callback<JsonCustomerSet>() {
            @Override
            public void onResponse(Call<JsonCustomerSet> call, Response<JsonCustomerSet> response) {
                customerSets = response.body();
                setAdapter(customerSets);
                progressBar.setVisibility(View.GONE);


            }

            @Override
            public void onFailure(Call<JsonCustomerSet> call, Throwable t) {

            }
        });
        try {
            customerListAdaptor.notifyDataSetChanged();
        } catch (Exception e) {

        }

    }

    private void setAdapter(final JsonCustomerSet customerSets) {
        customerListAdaptor = new CustomerListAdaptor(CustomerSearchActivity.this, customerSets, new CustomerListAdaptor.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent in=new Intent(CustomerSearchActivity.this,ViewCustomerDetailActivity.class);
                in.putExtra(KeyValue.NAME,customerSets.getCustomers().get(position).getName());
                in.putExtra(KeyValue.EMAIL,customerSets.getCustomers().get(position).getEmail());
                in.putExtra(KeyValue.PHONE,customerSets.getCustomers().get(position).getPhone());
                in.putExtra(KeyValue.ADDRESS,customerSets.getCustomers().get(position).getAddress());
                in.putExtra(KeyValue.DOB,customerSets.getCustomers().get(position).getDob());
                in.putExtra(KeyValue.NOTE,customerSets.getCustomers().get(position).getNote());
            startActivity(in);
            }
        });

        mCustomerRecyclerList.setAdapter(customerListAdaptor);
    }

    public void bindView() {
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        mCustomerSearchEdtTxt = findViewById(R.id.search_customer_edt_txt);
        mCustomerSeachIcon = findViewById(R.id.search_customer_icon);
        mCustomerRecyclerList = findViewById(R.id.customer_recycler_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mCustomerRecyclerList.setLayoutManager(mLayoutManager);

        mCustomerRecyclerList.setHasFixedSize(true);
        mCustomerRecyclerList.setItemAnimator(new DefaultItemAnimator());

        findViewById(R.id.add_new_customer_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerSearchActivity.this, AddNewCustomerActivity.class));
            }
        });
        mCustomerSearchEdtTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    private void filter(String input) {
//looping through existing elements
        ArrayList<JsonCustomer> filterdNames = new ArrayList<>();
        for (JsonCustomer object : customerSets.getCustomers()) {
            //if the existing elements contains the search input
            if (object.getName().toLowerCase().contains(input.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(object);
            }
        }
        filterList(filterdNames);
    }

    public void filterList(ArrayList<JsonCustomer> filterdNames) {
        this.filtercustomername = filterdNames;
        customerListAdaptor.notifyDataSetChanged();
    }

    private void getToolbar() {

        getSupportActionBar().setHomeButtonEnabled(true);
        // getSupportActionBar().setHomeAsUpIndicator(R.drawable.rupee_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setIcon(R.drawable.ic_icon_pos);
        getSupportActionBar().setTitle(getResources().getString(R.string.search_customer_header));

    }
}
