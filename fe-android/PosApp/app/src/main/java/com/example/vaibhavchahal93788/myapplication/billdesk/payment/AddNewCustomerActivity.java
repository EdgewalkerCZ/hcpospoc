package com.example.vaibhavchahal93788.myapplication.billdesk.payment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.crm.CRMAddCustomerActivity;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.JSONAddCustomer;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.JsonCustomerSet;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.userlogin.JSONAddCustomerResponse;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.api.ApiClient;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.api.ApiInterface;
import com.example.vaibhavchahal93788.myapplication.billdesk.preferences.AppPreferences;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Constants;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.KeyValue;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Validation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewCustomerActivity extends AppCompatActivity {
    private TextInputLayout mInputCustomerName,mInputCustomerphone,mInputCustomeremail,mInputCustomeraddress,mInputCustomerdob,mInputCustomerNote;
    private String mCustomerfirstName,mCustomerlastName,mCustomerphone,mCustomeremail,mCustomeraddress,mCustomerdob,mCustomerNote;
    private EditText mCustomerfirstNameEDT,mCustomerlastNameEDT,mCustomerphoneEDT,mCustomeremailEDT,mCustomeraddressEDT,mCustomerNoteEDT;
    private Button mSaveBTN;
    private AppPreferences mAppPreferences;
    private String mSessionId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_customer);
        getToolbar();
        bindView();
    }

    private void bindView() {
        mCustomerfirstNameEDT=findViewById(R.id.add_customer_firstname);
        mCustomerlastNameEDT=findViewById(R.id.add_customer_lastname);
        mCustomerphoneEDT=findViewById(R.id.add_customer_phone);
        mCustomeremailEDT=findViewById(R.id.add_customer_email);
        mCustomeraddressEDT=findViewById(R.id.add_customer_address);
//        mCustomerdobEDT=findViewById(R.id.add_customer_dob);
        mCustomerNoteEDT=findViewById(R.id.add_customer_note);

        mInputCustomerName=findViewById(R.id.input_layout_firstname);
        mInputCustomeremail=findViewById(R.id.input_layout_email);
        mInputCustomerphone=findViewById(R.id.input_layout_phone);
        mInputCustomerdob=findViewById(R.id.input_layout_dob);
        mInputCustomeraddress=findViewById(R.id.input_layout_address);
        mInputCustomerNote=findViewById(R.id.input_layout_note);
        mSaveBTN=findViewById(R.id.add_customer_btn);
        mAppPreferences = AppPreferences.getInstance(this);


        mSessionId=mAppPreferences.getJsessionId();
        mCustomerfirstNameEDT.addTextChangedListener(new MyTextWatcher(mCustomerfirstNameEDT));
        mCustomerlastNameEDT.addTextChangedListener(new MyTextWatcher(mCustomerlastNameEDT));
        mCustomeremailEDT.addTextChangedListener(new MyTextWatcher(mCustomeremailEDT));
        mCustomerphoneEDT.addTextChangedListener(new MyTextWatcher(mCustomerphoneEDT));

        mSaveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitform();
            }
        });
    }

    private void getToolbar() {


        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setIcon(R.drawable.ic_icon_pos);
        getSupportActionBar().setTitle(getResources().getString(R.string.add_new_customer));

    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.add_customer_firstname:
                    Validation.isValidName(mCustomerfirstNameEDT.getText().toString());
                    break;
                case R.id.add_customer_lastname:
                    Validation.isValidName(mCustomerlastNameEDT.getText().toString());
                    break;
                case R.id.add_customer_email:
                    Validation.isValidEmail(mCustomeremailEDT.getText().toString());
                    break;
                case R.id.add_customer_phone:
                    Validation.isValidMobile(mCustomerphoneEDT.getText().toString()) ;
                    break;
            }
        }
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

    public void submitform(){
        mCustomerfirstName=mCustomerfirstNameEDT.getText().toString();
        mCustomerphone=mCustomerphoneEDT.getText().toString();
        mCustomeremail=mCustomeremailEDT.getText().toString();
        mCustomeraddress=mCustomeraddressEDT.getText().toString();
//        mCustomerdob=mCustomerdobEDT.getText().toString();
        mCustomerNote=mCustomerNoteEDT.getText().toString();

        if(!Validation.isValidName(mCustomerfirstName)){
            mCustomerfirstNameEDT.setError("Please enter the customer name");
        }
        else if(!Validation.isValidEmail(mCustomeremail)){
            mCustomeremailEDT.setError("Please enter the Valid Email");
        }
        else if(!Validation.isValidMobile(mCustomerphone)){
            mCustomerphoneEDT.setError("Please enter the Valid Phone Number");
        }
        else{
            try {
                submitcustomerserver();
            }catch (Exception e){
                e.printStackTrace();
            }


        }






    }

    public void submitcustomerserver() throws Exception{
        JSONArray baseArray=new JSONArray();
//        JSONAddCustomer addCustomer=new JSONAddCustomer();
//        addCustomer.setAddress(mCustomeraddress);
//        addCustomer.setName();
        JSONAddCustomer addCustomer=new JSONAddCustomer();

        addCustomer.setName(mCustomerlastNameEDT.getText().toString());
        addCustomer.setFirstName(mCustomerfirstName);
        addCustomer.setAddress(mCustomeraddress);
        addCustomer.setEmail(mCustomeremail);
        addCustomer.setPhone(mCustomerphone);
        addCustomer.setDescription(mCustomerNote);
        addCustomer.setIsCustomer(true);
        addCustomer.setPartnerCategoryId(1);
        HashMap<String,String> headerkey=new HashMap<>();
        headerkey.put("Content-Type","application/json");
        headerkey.put("Accept","application/json");
        headerkey.put(Constants.SESSION_ID,mSessionId);
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<JSONAddCustomerResponse> call = apiService.addnewcustomer(headerkey,addCustomer);
        call.enqueue(new Callback<JSONAddCustomerResponse>() {
            @Override
            public void onResponse(Call<JSONAddCustomerResponse> call, Response<JSONAddCustomerResponse> response) {
                if(response!=null){
                    Intent in=new Intent(AddNewCustomerActivity.this,ViewCustomerDetailActivity.class);
                    in.putExtra(KeyValue.NAME,mCustomerfirstName+" "+mCustomerlastNameEDT.getText().toString());
                    in.putExtra(KeyValue.EMAIL,mCustomeremail);
                    in.putExtra(KeyValue.PHONE,mCustomerphone);
                    in.putExtra(KeyValue.ADDRESS,mCustomeraddress);
                    in.putExtra(KeyValue.CUSTOMER_ID,response.body().getData().get(0).getId()+"");
                    Log.e("HC Add==>",response.body().getData().get(0).getId()+"");
//                in.putExtra(KeyValue.DOB,customerSets.getData().get(position).getDob());
                    in.putExtra(KeyValue.NOTE,mCustomerNote);
                    startActivity(in);
                }
            }

            @Override
            public void onFailure(Call<JSONAddCustomerResponse> call, Throwable t) {
                Toast.makeText(AddNewCustomerActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
//        Intent in=new Intent(AddNewCustomerActivity.this,ViewCustomerDetailActivity.class);
//        in.putExtra(KeyValue.NAME,mCustomerfirstName);
//        in.putExtra(KeyValue.EMAIL,mCustomeremail);
//        in.putExtra(KeyValue.PHONE,mCustomerphone);
//        in.putExtra(KeyValue.ADDRESS,mCustomeraddress);
////        in.putExtra(KeyValue.DOB,mCustomerdob);
//        in.putExtra(KeyValue.NOTE,mCustomerNote);
//        startActivity(in);

//        Call<JSONObject> call = apiService.addnewcustomer(baseArray.toString());
//        call.enqueue(new Callback<JSONObject>() {
//            @Override
//            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
//                Log.e("Response",response.toString());
//
//                Intent in=new Intent(AddNewCustomerActivity.this,ViewCustomerDetailActivity.class);
//                in.putExtra(KeyValue.NAME,mCustomerName);
//                in.putExtra(KeyValue.EMAIL,mCustomeremail);
//                in.putExtra(KeyValue.PHONE,mCustomerphone);
//                in.putExtra(KeyValue.ADDRESS,mCustomeraddress);
//                in.putExtra(KeyValue.DOB,mCustomerdob);
//                in.putExtra(KeyValue.NOTE,mCustomerNote);
//                startActivity(in);
//
//            }
//
//            @Override
//            public void onFailure(Call<JSONObject> call, Throwable t) {
//                Intent in=new Intent(AddNewCustomerActivity.this,ViewCustomerDetailActivity.class);
//                in.putExtra(KeyValue.NAME,mCustomerName);
//                in.putExtra(KeyValue.EMAIL,mCustomeremail);
//                in.putExtra(KeyValue.PHONE,mCustomerphone);
//                in.putExtra(KeyValue.ADDRESS,mCustomeraddress);
//                in.putExtra(KeyValue.DOB,mCustomerdob);
//                in.putExtra(KeyValue.NOTE,mCustomerNote);
//                startActivity(in);
//            }
//        });
    }
}
