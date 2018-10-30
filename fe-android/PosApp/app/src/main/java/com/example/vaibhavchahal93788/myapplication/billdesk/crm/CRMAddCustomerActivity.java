package com.example.vaibhavchahal93788.myapplication.billdesk.crm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.JSONAddCustomer;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.customer.JSONCustomerResponse;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.userlogin.JSONAddCustomerResponse;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.ResponseHandler;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.ViewCustomerDetailActivity;
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

public class CRMAddCustomerActivity extends AppCompatActivity  {
    private TextInputLayout mInputCustomerName,mInputCustomerphone,mInputCustomeremail,mInputCustomeraddress,mInputCustomerdob,mInputCustomerNote;
    private String mCustomerfirstName,mCustomerlastName,mCustomerphone,mCustomeremail,mCustomeraddress,mCustomerdob,mCustomerNote;
    private EditText mCustomerfirstNameEDT,mCustomerlastNameEDT,mCustomerphoneEDT,mCustomeremailEDT,mCustomeraddressEDT,mCustomerNoteEDT;
    private Button mSaveBTN;
    private ProgressBar progressBar;
    private AppPreferences mAppPreferences;
    private String mSessionId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crmaddcustomer);
        getToolbar();
        bindView();
    }

    private void bindView() {
        mCustomerfirstNameEDT=findViewById(R.id.crm_add_customer_firstname);
        mCustomerlastNameEDT=findViewById(R.id.crm_add_customer_lastname);
        mCustomerphoneEDT=findViewById(R.id.crm_add_customer_phone);
        mCustomeremailEDT=findViewById(R.id.crm_add_customer_email);
        mCustomeraddressEDT=findViewById(R.id.crm_add_customer_address);
//        mCustomerdobEDT=findViewById(R.id.add_customer_dob);
        mCustomerNoteEDT=findViewById(R.id.crm_add_customer_note);

//        mInputCustomerName=findViewById(R.id.input_layout_firstname);
//        mInputCustomeremail=findViewById(R.id.input_layout_email);
//        mInputCustomerphone=findViewById(R.id.input_layout_phone);
//        mInputCustomerdob=findViewById(R.id.input_layout_dob);
//        mInputCustomeraddress=findViewById(R.id.input_layout_address);
//        mInputCustomerNote=findViewById(R.id.input_layout_note);
        mSaveBTN=findViewById(R.id.crm_add_customer_btn);
        mAppPreferences = AppPreferences.getInstance(this);
        progressBar = findViewById(R.id.progress_bar);

        mSessionId=mAppPreferences.getJsessionId();
        mCustomerfirstNameEDT.addTextChangedListener(new MyTextWatcher(mCustomerfirstNameEDT));
        mCustomerlastNameEDT.addTextChangedListener(new MyTextWatcher(mCustomerlastNameEDT));
        mCustomeremailEDT.addTextChangedListener(new MyTextWatcher(mCustomeremailEDT));
        mCustomerphoneEDT.addTextChangedListener(new MyTextWatcher(mCustomerphoneEDT));
        mCustomerlastName=mCustomerlastNameEDT.getText().toString();
        Intent in=getIntent();
        String name=in.getStringExtra(KeyValue.NAME);
        if(name!=null){
            mCustomerfirstNameEDT.setText(name);
            mCustomerphoneEDT.setText(in.getStringExtra(KeyValue.PHONE));
            mCustomeremailEDT.setText(in.getStringExtra(KeyValue.EMAIL));
            mCustomeraddressEDT.setText(in.getStringExtra(KeyValue.ADDRESS));
            mCustomerNoteEDT.setText(in.getStringExtra(KeyValue.NOTE));
        }

        mSaveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitform();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
        mCustomerlastName=mCustomerlastNameEDT.getText().toString();
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

        JSONAddCustomer addCustomer=new JSONAddCustomer();

        addCustomer.setName(mCustomerlastNameEDT.getText().toString());
        addCustomer.setFirstName(mCustomerfirstName);
        addCustomer.setAddress(mCustomeraddress);
        addCustomer.setEmail(mCustomeremail);
        addCustomer.setPhone(mCustomerphone);
        addCustomer.setDescription(mCustomerNote);
        addCustomer.setIsCustomer(true);
        addCustomer.setPartnerCategoryId(1);
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        HashMap<String,String> headerkey=new HashMap<>();
        headerkey.put("Content-Type","application/json");
        headerkey.put("Accept","application/json");
        headerkey.put(Constants.SESSION_ID,mSessionId);
        Call<JSONAddCustomerResponse> call = apiService.addnewcustomer(headerkey,addCustomer);
        call.enqueue(new Callback<JSONAddCustomerResponse>() {
            @Override
            public void onResponse(Call<JSONAddCustomerResponse> call, Response<JSONAddCustomerResponse> response) {
                if(response!=null){
                   if( response.code()==200){
                       JSONAddCustomerResponse getresponse=response.body();
                       Intent in=new Intent(CRMAddCustomerActivity.this,CRMViewCustomerActivity.class);
                       in.putExtra(KeyValue.FIRST_NAME,getresponse.getData().get(0).getFirstName());
                       in.putExtra(KeyValue.NAME, getresponse.getData().get(0).getName());
                       in.putExtra(KeyValue.PHONE,getresponse.getData().get(0).getPhone());
                       in.putExtra(KeyValue.EMAIL,getresponse.getData().get(0).getEmail());
                       in.putExtra(KeyValue.ADDRESS,getresponse.getData().get(0).getAddress());
                       in.putExtra(KeyValue.NOTE,getresponse.getData().get(0).getDescription());
                       in.putExtra(KeyValue.CUSTOMER_ID,getresponse.getData().get(0).getId()+"");
                       startActivity(in);
                       Toast.makeText(CRMAddCustomerActivity.this,"Customer added successfully",Toast.LENGTH_SHORT).show();
                    }
                    else{
                       Toast.makeText(CRMAddCustomerActivity.this,getString(R.string.network_error),Toast.LENGTH_SHORT).show();
                   }
// view_name_str=in.getStringExtra(KeyValue.NAME);
//        view_phone_str=in.getStringExtra(KeyValue.PHONE);
//        view_email_str=in.getStringExtra(KeyValue.EMAIL);
//        view_address_str=in.getStringExtra(KeyValue.ADDRESS);
//
//        view_note_str=in.getStringExtra(KeyValue.NOTE);

                }
            }

            @Override
            public void onFailure(Call<JSONAddCustomerResponse> call, Throwable t) {
            Toast.makeText(CRMAddCustomerActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });


//        Intent in=new Intent(CRMAddCustomerActivity.this,CRMViewCustomerActivity.class);
//        in.putExtra(KeyValue.NAME,mCustomerfirstName);
//        in.putExtra(KeyValue.EMAIL,mCustomeremail);
//        in.putExtra(KeyValue.PHONE,mCustomerphone);
//        in.putExtra(KeyValue.ADDRESS,mCustomeraddress);
////        in.putExtra(KeyValue.DOB,mCustomerdob);
//        in.putExtra(KeyValue.NOTE,mCustomerNote);
//        startActivity(in);


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
                case R.id.crm_add_customer_firstname:
                    Validation.isValidName(mCustomerfirstNameEDT.getText().toString());
                    break;
                    case R.id.crm_add_customer_lastname:
                    Validation.isValidName(mCustomerlastNameEDT.getText().toString());
                    break;
                case R.id.crm_add_customer_email:
                    Validation.isValidEmail(mCustomeremailEDT.getText().toString());
                    break;
                case R.id.crm_add_customer_phone:
                    Validation.isValidMobile(mCustomerphoneEDT.getText().toString()) ;
                    break;
            }
        }
    }

    private void getToolbar() {
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.add_new_customer));
    }
}
