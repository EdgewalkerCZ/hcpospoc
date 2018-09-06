package com.example.vaibhavchahal93788.myapplication.billdesk.activity;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.ProductListAdapter;
import com.example.vaibhavchahal93788.myapplication.billdesk.api.ProductApiHelper;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.IApiRequestComplete;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailactivity extends AppCompatActivity implements View.OnClickListener, ProductListAdapter.OnDataChangeListener {

    private RecyclerView recyclerView;
    private EditText editTextSearch;
    private List<ProductListModel> productsList;
    private ProductListAdapter adapter;
    private TextView txtviewEstPrice;
    private int totalItem, totalPrice;
    private RelativeLayout rlTotalCharge;
    private ProgressBar progreeBar;
    private int REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        setTitle("Product List");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViews();
        actionEditSearch();
        getProductList();
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        txtviewEstPrice = (TextView) findViewById(R.id.tv_est_price);
        rlTotalCharge = (RelativeLayout) findViewById(R.id.rl_charge);
        progreeBar = (ProgressBar) findViewById(R.id.progress_bar);

        findViewById(R.id.fab).setOnClickListener(this);
        findViewById(R.id.btn_payment).setOnClickListener(this);
    }

    private void setadapter(List<ProductListModel> productList) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        adapter = new ProductListAdapter(productList, this);

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
        ArrayList<ProductListModel> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (ProductListModel object : productsList) {
            //if the existing elements contains the search input
            if (object.getLabel().toLowerCase().contains(text.toLowerCase())) {
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_payment:
                ArrayList<ProductListModel> list = getSelectedItemList();
                Intent intent = new Intent(ProductDetailactivity.this, BillDetailActivity.class);
                intent.putExtra("selectedItemList", list);
                intent.putExtra("totalItems", totalItem);
                intent.putExtra("totalPrice", totalPrice);
                startActivity(intent);
                break;

            case R.id.fab:
                startActivityForResult(new Intent(ProductDetailactivity.this, AddProductActivity.class), REQUEST_CODE);
                break;

            default:
                break;

        }
    }

    private ArrayList<ProductListModel> getSelectedItemList() {
        ArrayList<ProductListModel> selectedItemList = new ArrayList<ProductListModel>();
        for (ProductListModel model : productsList) {
            if (model.isSelected()) {
                selectedItemList.add(model);
            }
        }
        return selectedItemList;
    }

    @Override
    public void onDataChanged(int totalItems, int totalPrice) {
        this.totalItem = totalItems;
        this.totalPrice = totalPrice;
        if (totalItems > 0) {
            rlTotalCharge.setVisibility(View.VISIBLE);
        } else {
            rlTotalCharge.setVisibility(View.GONE);
        }
        txtviewEstPrice.setText(String.format(getString(R.string.text_estimated_price), totalItems, totalPrice));
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
                setadapter(productList);
            }

            @Override
            public void onFailure(String message) {
                progreeBar.setVisibility(View.GONE);
                Toast.makeText(ProductDetailactivity.this, message, Toast.LENGTH_SHORT).show();
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
