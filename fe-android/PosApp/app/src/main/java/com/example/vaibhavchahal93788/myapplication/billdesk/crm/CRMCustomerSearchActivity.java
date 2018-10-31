package com.example.vaibhavchahal93788.myapplication.billdesk.crm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.CRMCustomerSearchAdaptor;
import com.example.vaibhavchahal93788.myapplication.billdesk.api.ProductApiHelper;
import com.example.vaibhavchahal93788.myapplication.billdesk.api.productApi;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.JsonCustomerSet;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.customer.DataItem;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.customer.JSONCustomerResponse;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.IApiRequestComplete;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.CustomerSearchActivity;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.ViewCustomerDetailActivity;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.api.ApiClient;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.api.ApiInterface;
import com.example.vaibhavchahal93788.myapplication.billdesk.preferences.AppPreferences;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Constants;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.KeyValue;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CRMCustomerSearchActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private AppPreferences mAppPreferences;
    private String mSessionId;
    CRMCustomerSearchAdaptor adaptor;
    EditText mSearchEdtTxt;
    RecyclerView mCustomerRecyclerList;
    private JSONCustomerResponse customerResponse;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crm_customer_search);
        getToolbar();
        bindView();
    }

    public void bindView(){
        mAppPreferences = AppPreferences.getInstance(this);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        mSessionId=mAppPreferences.getJsessionId();
        mSearchEdtTxt=findViewById(R.id.crm_search_customer_edt_txt);
        fetchList();

        mCustomerRecyclerList = findViewById(R.id.crm_customer_recycler_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mCustomerRecyclerList.setLayoutManager(mLayoutManager);

        mCustomerRecyclerList.setHasFixedSize(true);
        mCustomerRecyclerList.setItemAnimator(new DefaultItemAnimator());
        mSearchEdtTxt.addTextChangedListener(new TextWatcher() {
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
        findViewById(R.id.crm_add_new_customer_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                startActivity(new Intent(CRMCustomerSearchActivity.this,CRMAddCustomerActivity.class));

            }
        });
    }

    public void filter(String input){
        ArrayList<DataItem> filtercustomerList= new ArrayList<>();
        for(DataItem object : customerResponse.getData()){

            if (object.getName().toLowerCase().contains(input.toLowerCase())) {
                //adding the element to filtered list
                filtercustomerList.add(object);

            }
            else if(object.getPhone().toLowerCase().contains(input.toLowerCase())){
                filtercustomerList.add(object);
            }
        }
        JSONCustomerResponse set= new JSONCustomerResponse();
        set.setData(filtercustomerList);
        setAdaptor(set);
    }

    public void setAdaptor(final JSONCustomerResponse JSONCustomerResponse) {
       adaptor=new CRMCustomerSearchAdaptor(CRMCustomerSearchActivity.this, JSONCustomerResponse, new CRMCustomerSearchAdaptor.OnItemClickListener() {
           @Override
           public void onItemClick(int position) {

               Intent in=new Intent(CRMCustomerSearchActivity.this,CRMViewCustomerActivity.class);
            // in.putExtra(KeyValue.FIRST_NAME,getresponse.getData().get(0).getFirstName());
               //                       in.putExtra(KeyValue.NAME, getresponse.getData().get(0).getName());
               //                       in.putExtra(KeyValue.FULL_NAME, getresponse.getData().get(0).getFullName());
               //                       in.putExtra(KeyValue.PHONE,getresponse.getData().get(0).getPhone());
               //                       in.putExtra(KeyValue.EMAIL,getresponse.getData().get(0).getEmail());
               //                       in.putExtra(KeyValue.ADDRESS,getresponse.getData().get(0).getAddress());
               //                       in.putExtra(KeyValue.NOTE,getresponse.getData().get(0).getDescription());
               //                       in.putExtra(KeyValue.CUSTOMER_ID,getresponse.getData().get(0).getId()+"");
               in.putExtra(KeyValue.FIRST_NAME,JSONCustomerResponse.getData().get(0).getFirstName()+"");
               in.putExtra(KeyValue.FULL_NAME, JSONCustomerResponse.getData().get(0).getFullName());
               in.putExtra(KeyValue.NAME,JSONCustomerResponse.getData().get(position).getName());
               in.putExtra(KeyValue.EMAIL,JSONCustomerResponse.getData().get(position).getEmail());
               in.putExtra(KeyValue.PHONE,JSONCustomerResponse.getData().get(position).getPhone());
               in.putExtra(KeyValue.ADDRESS,JSONCustomerResponse.getData().get(position).getAddress());
               in.putExtra(KeyValue.CUSTOMER_IDs,JSONCustomerResponse.getData().get(position).getId());
//               in.putExtra(KeyValue.DOB,JSONCustomerResponse.getData().get(position).get;
               if(JSONCustomerResponse.getData().get(position).getDescription()!=null){
                   try {
                       in.putExtra(KeyValue.NOTE,JSONCustomerResponse.getData().get(position).getDescription().toString());
                   }catch (Exception e){

                   }
               }

               startActivity(in);

           }
       });
        progressBar.setVisibility(View.GONE);
        mCustomerRecyclerList.setAdapter(adaptor);
        adaptor.notifyDataSetChanged();
    }

    private void getToolbar() {
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.customer_details));

    }

//    public void fetchCustomerList() {
//        progressBar.setVisibility(View.VISIBLE);
//        new ProductApiHelper().getCustomers(mSessionId, new IApiRequestComplete<JSONCustomerResponse>() {
//            @Override
//            public void onSuccess(JSONCustomerResponse response) {
//                if(response.getStatus()==0){
//                    customerResponse=response;
//                    setAdaptor(customerResponse);
//                }
//            }
//            @Override
//            public void onFailure(String message) {
//                progressBar.setVisibility(View.GONE);
//                Toast.makeText(CRMCustomerSearchActivity.this,message,Toast.LENGTH_SHORT).show();
//            }
//        });
//    }


    public void fetchList() {
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
                    if (customerResponse!=null) {
                        setAdaptor(customerResponse);
                    }
                }
            }

            @Override
            public void onFailure(Call<JSONCustomerResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CRMCustomerSearchActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:

                finish();

                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}
