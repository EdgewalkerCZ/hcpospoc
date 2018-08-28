package com.example.vaibhavchahal93788.myapplication.billdesk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.BillDetailRecyclerAdapter;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.model.BillProduct;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.model.HeadingBillSummary;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.model.HeadingPaymentMode;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.model.PaymentMode;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.model.SelectedProduct;

import java.util.ArrayList;
import java.util.List;

public class BillDetailActivity extends AppCompatActivity {
    private List<Object> list = new ArrayList();
    private RecyclerView recyclerView;
    private BillDetailRecyclerAdapter adapter;

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
        list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            SelectedProduct selectedProduct = new SelectedProduct();
            selectedProduct.setName("coffee" + i);
            selectedProduct.setPrice("100" + (i + 1));
            selectedProduct.setQuantity("" + i + 1);
            list.add(selectedProduct);
        }
        list.add(new HeadingBillSummary("Bill Summary"));
        for (int i = 0; i < 2; i++) {
            BillProduct billProduct = new BillProduct(("coffee" + i), "100" + (i + 1), "" + i + 1);
            list.add(billProduct);
        }
        list.add(new HeadingPaymentMode("Payment Mode"));
        list.add(new PaymentMode());

    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BillDetailRecyclerAdapter(list);

        recyclerView.setAdapter(adapter);
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
}
