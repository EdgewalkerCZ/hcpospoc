package com.example.vaibhavchahal93788.myapplication.billdesk.payment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.KeyValue;

public class ViewCustomerDetailActivity extends AppCompatActivity {
    private TextView view_name_value,view_phone_value,view_email_value,view_address_value,view_dob_value,view_note_value;
    private String view_name_str,view_phone_str,view_email_str,view_address_str,view_dob_str,view_note_str,view_id_str;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_customer);
        getToolbar();
        bindView();

    }

    private void getToolbar() {


        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setIcon(R.drawable.ic_icon_pos);
        getSupportActionBar().setTitle(getResources().getString(R.string.customer_details));

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
        view_dob_str=in.getStringExtra(KeyValue.DOB);
        view_note_str=in.getStringExtra(KeyValue.NOTE);
        view_id_str=in.getStringExtra(KeyValue.CUSTOMER_ID);


Log.e("HC view_id_str==>",view_id_str+">>>>>>>>>>>>>>>>>>");

        view_name_value.setText(view_name_str);
        view_phone_value.setText(view_phone_str);
        view_email_value.setText(view_email_str);
        view_address_value.setText(view_address_str);
//        view_dob_value.setText(view_dob_str);
        if(view_note_str!=null) {
            view_note_value.setText(Html.fromHtml(view_note_str + " " + ""));
        }
        findViewById(R.id.view_customer_countinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyValue.setString(ViewCustomerDetailActivity.this, KeyValue.NAME,view_name_str);
                KeyValue.setString(ViewCustomerDetailActivity.this, KeyValue.PHONE,view_phone_str);
                KeyValue.setString(ViewCustomerDetailActivity.this, KeyValue.EMAIL,view_email_str);
                KeyValue.setString(ViewCustomerDetailActivity.this, KeyValue.ADDRESS,view_address_str);
                KeyValue.setString(ViewCustomerDetailActivity.this, KeyValue.DOB,view_dob_str);
                KeyValue.setString(ViewCustomerDetailActivity.this, KeyValue.NOTE,view_note_str);
                KeyValue.setString(ViewCustomerDetailActivity.this, KeyValue.CUSTOMER_ID,view_id_str);

                startActivity(new Intent(ViewCustomerDetailActivity.this,SelectProductActivity.class));

            }
        });

    }
}
