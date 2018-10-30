package com.example.vaibhavchahal93788.myapplication.billdesk.crm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.activity.HomeActivity;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.SelectProductActivity;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.ViewCustomerDetailActivity;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.KeyValue;

public class CRMViewCustomerActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView view_name_value,view_phone_value,view_email_value,view_address_value,view_dob_value,view_note_value;
    private String view_name_str,view_phone_str,view_email_str,view_address_str,view_dob_str,view_note_str;

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

        Intent in=getIntent();
        view_name_str=in.getStringExtra(KeyValue.NAME);
        view_phone_str=in.getStringExtra(KeyValue.PHONE);
        view_email_str=in.getStringExtra(KeyValue.EMAIL);
        view_address_str=in.getStringExtra(KeyValue.ADDRESS);

        view_note_str=in.getStringExtra(KeyValue.NOTE);


        view_name_value.setText(view_name_str);
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
        getSupportActionBar().setTitle(getResources().getString(R.string.add_new_customer));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.crm_edit_customer:
                Intent in=new Intent(CRMViewCustomerActivity.this,CRMAddCustomerActivity.class);
                in.putExtra(KeyValue.NAME,view_name_str);
                in.putExtra(KeyValue.PHONE,view_phone_str);
                in.putExtra(KeyValue.EMAIL,view_email_str);
                in.putExtra(KeyValue.ADDRESS,view_address_str);
                in.putExtra(KeyValue.NOTE,view_note_str);
                startActivity(in);
                break;
            case R.id.crm_delete_customer:

                break;
        }
    }
}
