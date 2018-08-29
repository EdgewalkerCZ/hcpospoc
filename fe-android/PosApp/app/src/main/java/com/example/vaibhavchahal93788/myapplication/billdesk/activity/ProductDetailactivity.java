package com.example.vaibhavchahal93788.myapplication.billdesk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.ProductListAdapter;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;

import java.util.ArrayList;

public class ProductDetailactivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editTextSearch;
    private ArrayList<ProductListModel> productList;
    private ProductListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        setTitle("Product List");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        populateList();

        initViews();

        actionEditSearch();

        findViewById(R.id.btn_payment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetailactivity.this, BillDetailActivity.class);
                startActivity(intent);
            }
        });
    }

    private void populateList() {
        productList = new ArrayList<>();

        productList.add(new ProductListModel("Coffee"));
        productList.add(new ProductListModel("Black Coffee"));
        productList.add(new ProductListModel("Tea"));
        productList.add(new ProductListModel("Green Tea"));
        productList.add(new ProductListModel("Black Tea"));
        productList.add(new ProductListModel("Red Tea"));
        productList.add(new ProductListModel("Nescafe"));
        productList.add(new ProductListModel("Nestle"));
        productList.add(new ProductListModel("Taza"));
        productList.add(new ProductListModel("Red Label"));
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        editTextSearch = (EditText) findViewById(R.id.editTextSearch);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        adapter = new ProductListAdapter(productList);

        recyclerView.setAdapter(adapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProductDetailactivity.this, AddProductActivity.class));
            }
        });
    }

    private void actionEditSearch() {
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                filter(editable.toString());
            }
        });
    }


    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<ProductListModel> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (ProductListModel object : productList) {
            //if the existing elements contains the search input
            if (object.getText().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(object);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        adapter.filterList(filterdNames);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
