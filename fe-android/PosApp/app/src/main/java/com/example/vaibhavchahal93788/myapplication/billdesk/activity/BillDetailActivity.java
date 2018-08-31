package com.example.vaibhavchahal93788.myapplication.billdesk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.BillDetailRecyclerAdapter;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.BillProduct;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.HeadingBillSummary;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.HeadingPaymentMode;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.PaymentMode;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.SelectedProduct;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.TotalBillDetail;

import java.util.ArrayList;
import java.util.List;

public class BillDetailActivity extends AppCompatActivity implements BillDetailRecyclerAdapter.OnDataChangeListener {
    private List<Object> list = new ArrayList();
    private RecyclerView recyclerView;
    private BillDetailRecyclerAdapter adapter;
    private ArrayList<ProductListModel> selectedItemList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail);

        setTitle("Payment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        populateList();
        initViews();
    }

    private void populateList() {
        selectedItemList = getIntent().getParcelableArrayListExtra("selectedItemList");
        int totalItems = getIntent().getIntExtra("totalItems", 0);
        int totalPrice = getIntent().getIntExtra("totalPrice", 0);

        list = new ArrayList<>();

        for (ProductListModel listModel : selectedItemList) {
            SelectedProduct selectedProduct = new SelectedProduct(listModel.getText(), listModel.getQuantity(), listModel.getPrice());
            list.add(selectedProduct);
        }

        list.add(new HeadingBillSummary("Bill Summary"));

        for (ProductListModel listModel : selectedItemList) {
            BillProduct billProduct = new BillProduct(listModel.getText(), listModel.getQuantity(), listModel.getPrice());
            list.add(billProduct);
        }

        TotalBillDetail totalBillDetail = new TotalBillDetail("Total Amount" + " (" + totalItems + " items)", totalPrice);
        list.add(totalBillDetail);

        list.add(new HeadingPaymentMode("Payment Mode"));
        list.add(new PaymentMode());

    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BillDetailRecyclerAdapter(list, this);

        recyclerView.setAdapter(adapter);

        findViewById(R.id.btn_view_bill).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BillDetailActivity.this, BillSummaryActivity.class));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home) {
            Intent intent = new Intent(BillDetailActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDataChanged(int quantity, int position) {
        int index = selectedItemList.size() + 1 + position;
        BillProduct billProduct = (BillProduct) list.get(index);
        billProduct.setQuantity(quantity);

        int totalBillIndex = (selectedItemList.size() * 2) + 1;

        TotalBillDetail totalBillDetail = (TotalBillDetail) list.get(totalBillIndex);

        int startIndex = (selectedItemList.size() + 1);
        int endIndex = (selectedItemList.size() * 2) + 1;

        int totalItems = 0, totalPrice = 0;
        for (int i = startIndex; i < endIndex; i++) {
            totalItems = totalItems + ((BillProduct) list.get(i)).getQuantity();
            totalPrice = totalPrice + ((BillProduct) list.get(i)).getPrice() * ((BillProduct) list.get(i)).getQuantity();
        }
        totalBillDetail.setTitle("Total Amount" + " (" + totalItems + " items)");
        totalBillDetail.setTotalPrice(totalPrice);

        adapter.notifyItemChanged(index);
        adapter.notifyItemChanged(totalBillIndex);
    }
}
