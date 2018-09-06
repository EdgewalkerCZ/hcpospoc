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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.ProductStockListAdapter;
import com.example.vaibhavchahal93788.myapplication.billdesk.api.ProductApiHelper;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.IApiRequestComplete;

import java.util.ArrayList;
import java.util.List;

public class StockDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editTextSearch;
    private ArrayList<String> names;
    private List<ProductListModel> productsList;
    private ProductStockListAdapter adapter;
    private ProgressBar progreeBar;
    private int REQUEST_CODE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        setTitle("Product List");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        populateList();

        initViews();

//        actionEditSearch();
        getProductList();

    }

//    private void populateList() {
//        names = new ArrayList<>();
//
//        names.add("Coffee");
//        names.add("Black Coffee");
//        names.add("Tea");
//        names.add("Green Tea");
//        names.add("Black Tea");
//        names.add("Red Tea");
//        names.add("Nescafe");
//        names.add("Nestle");
//        names.add("Taza");
//        names.add("Red Label");
//    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        progreeBar = (ProgressBar) findViewById(R.id.progress_bar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(StockDetailActivity.this, AddProductActivity.class), REQUEST_CODE);
            }
        });
    }

    private void setAdapter(List<ProductListModel> productList) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        adapter = new ProductStockListAdapter(productList);

        recyclerView.setAdapter(adapter);
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

    public void getProductList() {
        progreeBar.setVisibility(View.VISIBLE);
        new ProductApiHelper().fetchProductList("t.ref", "ASC", 100, new IApiRequestComplete<List<ProductListModel>>() {

            @Override
            public void onSuccess(List<ProductListModel> productList) {
                if (!productList.isEmpty()) {
                    editTextSearch.setEnabled(true);
                }
                productsList = productList;
                progreeBar.setVisibility(View.GONE);
                setAdapter(productList);
            }

            @Override
            public void onFailure(String message) {
                progreeBar.setVisibility(View.GONE);
                Toast.makeText(StockDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            getProductList();
        }
    }
}
