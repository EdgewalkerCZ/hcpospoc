package com.example.vaibhavchahal93788.myapplication.billdesk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.BillDetailRecyclerAdapter;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.ProductListAdapter;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.ProductStockListAdapter;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.model.BillProduct;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.model.HeadingBillSummary;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.model.HeadingPaymentMode;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.model.PaymentMode;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.model.SelectedProduct;

import java.util.ArrayList;
import java.util.List;

public class StockDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editTextSearch;
    private ArrayList<String> names;
    private ProductStockListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        setTitle("Product List");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        populateList();

        initViews();

//        actionEditSearch();

    }

    private void populateList() {
        names = new ArrayList<>();

        names.add("Coffee");
        names.add("Black Coffee");
        names.add("Tea");
        names.add("Green Tea");
        names.add("Black Tea");
        names.add("Red Tea");
        names.add("Nescafe");
        names.add("Nestle");
        names.add("Taza");
        names.add("Red Label");
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        editTextSearch = (EditText) findViewById(R.id.editTextSearch);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        adapter = new ProductStockListAdapter(names);

        recyclerView.setAdapter(adapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StockDetailActivity.this, AddProductActivity.class));
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
        ArrayList<String> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (String s : names) {
            //if the existing elements contains the search input
            if (s.toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list
//        adapter.filterList(filterdNames);
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
