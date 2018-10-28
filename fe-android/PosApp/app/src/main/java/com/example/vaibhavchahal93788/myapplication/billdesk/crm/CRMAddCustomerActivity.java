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

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.ViewCustomerDetailActivity;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.api.ApiClient;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.api.ApiInterface;
import com.example.vaibhavchahal93788.myapplication.billdesk.preferences.AppPreferences;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.KeyValue;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Validation;

import org.json.JSONArray;
import org.json.JSONObject;

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
        JSONObject object=new JSONObject();
        object.put("name",mCustomerfirstName);
        object.put("email",mCustomeremail);
        object.put("phone",mCustomerphone);
        object.put("address",mCustomeraddress);
//        object.put("dob",mCustomerdob);
        object.put("note",mCustomerNote);
        baseArray.put(object);

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Intent in=new Intent(CRMAddCustomerActivity.this,CRMViewCustomerActivity.class);
        in.putExtra(KeyValue.NAME,mCustomerfirstName);
        in.putExtra(KeyValue.EMAIL,mCustomeremail);
        in.putExtra(KeyValue.PHONE,mCustomerphone);
        in.putExtra(KeyValue.ADDRESS,mCustomeraddress);
//        in.putExtra(KeyValue.DOB,mCustomerdob);
        in.putExtra(KeyValue.NOTE,mCustomerNote);
        startActivity(in);

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
