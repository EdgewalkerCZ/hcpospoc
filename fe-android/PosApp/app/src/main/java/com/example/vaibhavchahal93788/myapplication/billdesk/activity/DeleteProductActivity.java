package com.example.vaibhavchahal93788.myapplication.billdesk.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.vaibhavchahal93788.myapplication.R;

public class DeleteProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_product);
        setTitle("Remove");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
