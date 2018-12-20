package com.example.vaibhavchahal93788.myapplication.billdesk.crm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.activity.HomeActivity;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Utility;


public class CRMActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crm);
        getToolbar();
        bindview();

    }

    private void bindview() {
        findViewById(R.id.cardcrmaddcustomer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utility.isNetworkAvailable(CRMActivity.this)) {
                    Intent intent = new Intent(CRMActivity.this, CRMAddCustomerActivity.class);
                    startActivity(intent);
                } else{Toast.makeText(CRMActivity.this,getString(R.string.network_error),Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.crmcardviewcustomer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utility.isNetworkAvailable(CRMActivity.this)) {
                    Intent intent = new Intent(CRMActivity.this, CRMCustomerSearchActivity.class);
                    startActivity(intent);
                }
                else{Toast.makeText(CRMActivity.this,getString(R.string.network_error),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void getToolbar() {
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.customer_relation));
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
}
