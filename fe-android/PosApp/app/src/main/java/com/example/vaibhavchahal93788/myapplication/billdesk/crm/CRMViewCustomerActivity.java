package com.example.vaibhavchahal93788.myapplication.billdesk.crm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.activity.HomeActivity;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.JSONAddCustomer;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.userlogin.JSONAddCustomerResponse;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.SelectProductActivity;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.ViewCustomerDetailActivity;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.api.ApiClient;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.api.ApiInterface;
import com.example.vaibhavchahal93788.myapplication.billdesk.preferences.AppPreferences;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Constants;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.KeyValue;

import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CRMViewCustomerActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView view_name_value,view_phone_value,view_email_value,view_address_value,view_dob_value,view_note_value;
    private String view_full_name_str,view_name_str,view_phone_str,view_email_str,view_address_str,view_first_name,view_note_str,view_id_str;
    private AppPreferences mAppPreferences;
    private String mSessionId;
    int view_customerid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crmviewactivity);
        getToolbar();
        bindView();

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
    public void bindView(){
        view_name_value=findViewById(R.id.view_name_value);
        view_phone_value=findViewById(R.id.view_phone_value);
        view_email_value=findViewById(R.id.view_email_value);
        view_address_value=findViewById(R.id.view_address_value);
        view_dob_value=findViewById(R.id.view_dob_value);
        view_note_value=findViewById(R.id.view_note_value);
        mAppPreferences = AppPreferences.getInstance(this);


        mSessionId=mAppPreferences.getJsessionId();
        Intent in=getIntent();
        Bundle bn=in.getExtras();
        view_name_str=in.getStringExtra(KeyValue.NAME);
        view_first_name=in.getStringExtra(KeyValue.FIRST_NAME);
        view_full_name_str=in.getStringExtra(KeyValue.FULL_NAME);
        view_phone_str=in.getStringExtra(KeyValue.PHONE);
        view_email_str=in.getStringExtra(KeyValue.EMAIL);
        view_address_str=in.getStringExtra(KeyValue.ADDRESS);
        view_note_str=in.getStringExtra(KeyValue.NOTE);
        view_name_value.setText(view_full_name_str);
        view_customerid=bn.getInt(KeyValue.CUSTOMER_IDs);
        view_phone_value.setText(view_phone_str);
        view_email_value.setText(view_email_str);
        view_address_value.setText(view_address_str);

        view_note_value.setText(Html.fromHtml(view_note_str+" "+""));

//        findViewById(R.id.crm_edit_customer).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent in=new Intent(CRMViewCustomerActivity.this,CRMAddCustomerActivity.class);
//
//                in.putExtra(KeyValue.NAME,view_name_str);
//                in.putExtra(KeyValue.PHONE,view_phone_str);
//                in.putExtra(KeyValue.EMAIL,view_email_str);
//                in.putExtra(KeyValue.ADDRESS,view_address_str);
//                in.putExtra(KeyValue.NOTE,view_note_str);
////                KeyValue.setString(CRMViewCustomerActivity.this, KeyValue.NAME,view_name_str);
////                KeyValue.setString(CRMViewCustomerActivity.this, KeyValue.PHONE,view_phone_str);
////                KeyValue.setString(CRMViewCustomerActivity.this, KeyValue.EMAIL,view_email_str);
////                KeyValue.setString(CRMViewCustomerActivity.this, KeyValue.ADDRESS,view_address_str);
////                KeyValue.setString(CRMViewCustomerActivity.this, KeyValue.DOB,view_dob_str);
////                KeyValue.setString(CRMViewCustomerActivity.this, KeyValue.NOTE,view_note_str);
//                startActivity(in);
//
//            }
//        });

//        findViewById(R.id.crm_delete_customer).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

    }

    private void getToolbar() {
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.customer_details));
    }

    public void updatecustomer(final boolean customer_status){
        JSONAddCustomer addCustomer=new JSONAddCustomer();
        addCustomer.setName(view_name_str);
        addCustomer.setAddress(view_address_str);
        addCustomer.setEmail(view_email_str);
        addCustomer.setPhone(view_phone_str);
        addCustomer.setDescription(view_note_str);
        addCustomer.setId(view_customerid);
        addCustomer.setIsCustomer(customer_status);
        addCustomer.setPartnerCategoryId(1);
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        HashMap<String,String> headerkey=new HashMap<>();
        headerkey.put("Content-Type","application/json");
        headerkey.put("Accept","application/json");
        headerkey.put(Constants.SESSION_ID,mSessionId);
        Call<JSONObject> call = apiService.updatecustomer(view_customerid,headerkey,addCustomer);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if(response!=null){
                    if( response.code()==200){
                        JSONObject res=response.body();

//                        JSONAddCustomerResponse getresponse=response.body();
//                        Intent in=new Intent(CRMViewCustomerActivity.this,CRMViewCustomerActivity.class);
//                        in.putExtra(KeyValue.NAME,getresponse.getData().get(0).getFirstName()+" " +getresponse.getData().get(0).getName());
//                        in.putExtra(KeyValue.PHONE,getresponse.getData().get(0).getPhone());
//                        in.putExtra(KeyValue.EMAIL,getresponse.getData().get(0).getEmail());
//                        in.putExtra(KeyValue.ADDRESS,getresponse.getData().get(0).getAddress());
//                        in.putExtra(KeyValue.NOTE,getresponse.getData().get(0).getDescription());
//                        in.putExtra(KeyValue.CUSTOMER_ID,getresponse.getData().get(0).getId());
//                        startActivity(in);
                        if(!customer_status){
                            Toast.makeText(CRMViewCustomerActivity.this,"Customer deleted successfully",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else{
                            Toast.makeText(CRMViewCustomerActivity.this,"Customer add successfully",Toast.LENGTH_SHORT).show();
                        }

                    }
                    else{
                        Toast.makeText(CRMViewCustomerActivity.this,getString(R.string.network_error),Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Toast.makeText(CRMViewCustomerActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.crm_edit_customer:
//                updatecustomer(true);
                Intent in=new Intent(CRMViewCustomerActivity.this,CRMAddCustomerActivity.class);
                in.putExtra(KeyValue.NAME,view_name_str);
                in.putExtra(KeyValue.FIRST_NAME,view_first_name);
                in.putExtra(KeyValue.FULL_NAME,view_full_name_str);
                in.putExtra(KeyValue.PHONE,view_phone_str);
                in.putExtra(KeyValue.EMAIL,view_email_str);
                in.putExtra(KeyValue.ADDRESS,view_address_str);
                in.putExtra(KeyValue.NOTE,view_note_str);
                in.putExtra(KeyValue.CUSTOMER_ID,view_note_str);
                startActivity(in);
                break;
            case R.id.crm_delete_customer:
                updatecustomer(false);
                break;

            case R.id.crm_submit_customer:
                startActivity(new Intent(CRMViewCustomerActivity.this,CRMActivity.class));
                break;
        }
    }
}
