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

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.JsonCustomerSet;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.api.ApiClient;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.api.ApiInterface;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.KeyValue;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Validation;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewCustomerActivity extends AppCompatActivity {
    private TextInputLayout mInputCustomerName,mInputCustomerphone,mInputCustomeremail,mInputCustomeraddress,mInputCustomerdob,mInputCustomerNote;
    private String mCustomerName,mCustomerphone,mCustomeremail,mCustomeraddress,mCustomerdob,mCustomerNote;
    private EditText mCustomerNameEDT,mCustomerphoneEDT,mCustomeremailEDT,mCustomeraddressEDT,mCustomerdobEDT,mCustomerNoteEDT;
    private Button mSaveBTN;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_customer);
        getToolbar();
        bindView();
    }

    private void bindView() {
        mCustomerNameEDT=findViewById(R.id.add_customer_name);
        mCustomerphoneEDT=findViewById(R.id.add_customer_phone);
        mCustomeremailEDT=findViewById(R.id.add_customer_email);
        mCustomeraddressEDT=findViewById(R.id.add_customer_address);
        mCustomerdobEDT=findViewById(R.id.add_customer_dob);
        mCustomerNoteEDT=findViewById(R.id.add_customer_note);

        mInputCustomerName=findViewById(R.id.input_layout_name);
        mInputCustomeremail=findViewById(R.id.input_layout_email);
        mInputCustomerphone=findViewById(R.id.input_layout_phone);
        mInputCustomerdob=findViewById(R.id.input_layout_dob);
        mInputCustomeraddress=findViewById(R.id.input_layout_address);
        mInputCustomerNote=findViewById(R.id.input_layout_note);
        mSaveBTN=findViewById(R.id.add_customer_btn);

        mCustomerNameEDT.addTextChangedListener(new MyTextWatcher(mCustomerNameEDT));
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
                case R.id.add_customer_name:
                    Validation.isValidName(mCustomerNameEDT.getText().toString());
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
        mCustomerName=mCustomerNameEDT.getText().toString();
        mCustomerphone=mCustomerphoneEDT.getText().toString();
        mCustomeremail=mCustomeremailEDT.getText().toString();
        mCustomeraddress=mCustomeraddressEDT.getText().toString();
        mCustomerdob=mCustomerdobEDT.getText().toString();
        mCustomerNote=mCustomerNoteEDT.getText().toString();

        if(!Validation.isValidName(mCustomerName)){
            mCustomerNameEDT.setError("Please enter the customer name");
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
        object.put("name",mCustomerName);
        object.put("email",mCustomeremail);
        object.put("phone",mCustomerphone);
        object.put("address",mCustomeraddress);
        object.put("dob",mCustomerdob);
        object.put("note",mCustomerNote);
        baseArray.put(object);

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Intent in=new Intent(AddNewCustomerActivity.this,ViewCustomerDetailActivity.class);
        in.putExtra(KeyValue.NAME,mCustomerName);
        in.putExtra(KeyValue.EMAIL,mCustomeremail);
        in.putExtra(KeyValue.PHONE,mCustomerphone);
        in.putExtra(KeyValue.ADDRESS,mCustomeraddress);
        in.putExtra(KeyValue.DOB,mCustomerdob);
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
}
