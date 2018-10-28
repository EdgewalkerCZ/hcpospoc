package com.example.vaibhavchahal93788.myapplication.billdesk.crm;

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
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.api.ApiClient;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.api.ApiInterface;
import com.example.vaibhavchahal93788.myapplication.billdesk.preferences.AppPreferences;

import java.util.ArrayList;

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
        fetchCustomerList();

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
    }

    public void filter(String input){
        ArrayList<DataItem> filtercustomerList= new ArrayList<>();
        for(DataItem object : filtercustomerList){

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

    public void setAdaptor(JSONCustomerResponse JSONCustomerResponse) {
       adaptor=new CRMCustomerSearchAdaptor(CRMCustomerSearchActivity.this, JSONCustomerResponse, new CRMCustomerSearchAdaptor.OnItemClickListener() {
           @Override
           public void onItemClick(int position) {

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

    public void fetchCustomerList() {
        progressBar.setVisibility(View.VISIBLE);
        new ProductApiHelper().getCustomers(mSessionId, new IApiRequestComplete<JSONCustomerResponse>() {
            @Override
            public void onSuccess(JSONCustomerResponse response) {
                if(response.getStatus()==0){
                    customerResponse=response;
                    setAdaptor(customerResponse);
                }
            }
            @Override
            public void onFailure(String message) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CRMCustomerSearchActivity.this,message,Toast.LENGTH_SHORT).show();
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
