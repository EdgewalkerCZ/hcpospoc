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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.activity.BillSummaryActivityNew;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.CustomerListAdaptor;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.ProductStockListAdapter;
import com.example.vaibhavchahal93788.myapplication.billdesk.api.ProductApiHelper;
import com.example.vaibhavchahal93788.myapplication.billdesk.crm.CRMCustomerSearchActivity;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.CustomerModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.JsonCustomer;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.JsonCustomerSet;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.customer.DataItem;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.customer.JSONCustomerResponse;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.IApiRequestComplete;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.api.ApiClient;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.api.ApiInterface;
import com.example.vaibhavchahal93788.myapplication.billdesk.preferences.AppPreferences;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Constants;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.KeyValue;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Utility;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
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

    private AppPreferences mAppPreferences;
    private String mSessionId;
    private JSONCustomerResponse customerResponse;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_search);
        getToolbar();
        bindView();
        fetchCustomerList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utility.hideKeyboard(CustomerSearchActivity.this,mCustomerSearchEdtTxt);
    }

    public void fetchCustomerList() {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiService =ApiClient.getClient().create(ApiInterface.class);
        HashMap<String,String> headerkey=new HashMap<>();
        headerkey.put("Content-Type","application/json");
        headerkey.put("Accept","application/json");
        headerkey.put(Constants.SESSION_ID,mSessionId);
        Call<JSONCustomerResponse> call = apiService.getallCustomers(headerkey);
        call.enqueue(new Callback<JSONCustomerResponse>() {
            @Override
            public void onResponse(Call<JSONCustomerResponse> call, Response<JSONCustomerResponse> response) {

                progressBar.setVisibility(View.GONE);
                if(response!=null){
                    customerResponse= response.body();
                    setAdapter(customerResponse);
                }
            }

            @Override
            public void onFailure(Call<JSONCustomerResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CustomerSearchActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

    }
    //private JSONCustomerResponse customerResponse;

    private void setAdapter(final JSONCustomerResponse customerSets) {
        customerListAdaptor = new CustomerListAdaptor(CustomerSearchActivity.this, customerSets, new CustomerListAdaptor.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent in=new Intent(CustomerSearchActivity.this,SelectProductActivity.class);
                in.putExtra(KeyValue.NAME,customerSets.getData().get(position).getFullName());
                in.putExtra(KeyValue.EMAIL,customerSets.getData().get(position).getEmail());
                in.putExtra(KeyValue.PHONE,customerSets.getData().get(position).getPhone());
                in.putExtra(KeyValue.ADDRESS,customerSets.getData().get(position).getAddress());
                in.putExtra(KeyValue.NOTE,customerSets.getData().get(position).getDescription());
                KeyValue.setString(CustomerSearchActivity.this, KeyValue.NAME,customerSets.getData().get(position).getFullName());
                KeyValue.setString(CustomerSearchActivity.this, KeyValue.PHONE,customerSets.getData().get(position).getPhone());
                KeyValue.setString(CustomerSearchActivity.this, KeyValue.EMAIL,customerSets.getData().get(position).getEmail());
                KeyValue.setString(CustomerSearchActivity.this, KeyValue.CUSTOMER_ID,customerSets.getData().get(position).getId()+"");
//                 KeyValue.setString(ViewCustomerDetailActivity.this, KeyValue.NAME,view_name_str);
                //                KeyValue.setString(ViewCustomerDetailActivity.this, KeyValue.PHONE,view_phone_str);
                //                KeyValue.setString(ViewCustomerDetailActivity.this, KeyValue.EMAIL,view_email_str);
                //                KeyValue.setString(ViewCustomerDetailActivity.this, KeyValue.ADDRESS,view_address_str);
                //                KeyValue.setString(ViewCustomerDetailActivity.this, KeyValue.DOB,view_dob_str);
                //                KeyValue.setString(ViewCustomerDetailActivity.this, KeyValue.NOTE,view_note_str);
                //
                //                startActivity(new Intent(ViewCustomerDetailActivity.this,SelectProductActivity.class));
            startActivity(in);

            //getCustomerDetails(customerSets.getData().get(position).getId());
            }
        });

        mCustomerRecyclerList.setAdapter(customerListAdaptor);
    }

    public void bindView() {
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        mAppPreferences = AppPreferences.getInstance(this);
        mSessionId=mAppPreferences.getJsessionId();
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
        ArrayList<DataItem> filterdNames = new ArrayList<>();
        for (DataItem object : customerResponse.getData()) {
            //if the existing elements contains the search input
            if (object.getName().toLowerCase().contains(input.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(object);

            }
            else if(object.getPhone().toLowerCase().contains(input.toLowerCase())){
                filterdNames.add(object);
            }
        }
        JSONCustomerResponse filterset=new JSONCustomerResponse();
        filterset.setData(filterdNames);
        setAdapter(filterset);
    }
    private void getToolbar() {
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.search_customer_header));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.customer_search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.menu_close:
                KeyValue.setString(CustomerSearchActivity.this, KeyValue.NAME," ");
                KeyValue.setString(CustomerSearchActivity.this, KeyValue.PHONE," ");
                KeyValue.setString(CustomerSearchActivity.this, KeyValue.EMAIL," ");
                KeyValue.setString(CustomerSearchActivity.this, KeyValue.CUSTOMER_ID,"0");
                startActivity(new Intent(CustomerSearchActivity.this,SelectProductActivity.class));

                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    //Get customer details from server
    public void getCustomerDetails(int id){
        //progreeBar.setVisibility(View.VISIBLE);
        HashMap<String,String> headerValues= new HashMap<>();
        headerValues.put("Content-Type", "application/json");
        headerValues.put("Accept", "application/json");
        headerValues.put(Constants.SESSION_ID,mAppPreferences.getJsessionId());

        new ProductApiHelper().getCustomerDetails(headerValues,String.valueOf(id), new IApiRequestComplete<CustomerModel>() {
            @Override
            public void onSuccess(final CustomerModel response) {
                //progreeBar.setVisibility(View.GONE);
                if (response!=null){
                    if (response.getData().size()!=0){
                        response.getData().get(0).getAddress();
                        int status = response.getStatus();
                        Log.v("Status", status+"");

                        Intent in=new Intent(CustomerSearchActivity.this,ViewCustomerDetailActivity.class);
                        in.putExtra(KeyValue.NAME,response.getData().get(0).getFullName());
                        in.putExtra(KeyValue.EMAIL,response.getData().get(0).getEmail());
                        in.putExtra(KeyValue.PHONE,response.getData().get(0).getPhone());
                        in.putExtra(KeyValue.ADDRESS,response.getData().get(0).getAddress());
//                in.putExtra(KeyValue.DOB,customerSets.getData().get(position).getDob());
                        //in.putExtra(KeyValue.NOTE,response.getData().get(0).getNote());
                        in.putExtra(KeyValue.NOTE,"Note");
                        startActivity(in);

                    }else {
                        Utility.showToast(getApplicationContext(),getResources().getString(R.string.no_data));
                    }
                }
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }
}
