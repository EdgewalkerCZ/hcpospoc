package com.example.vaibhavchahal93788.myapplication.billdesk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.BillSummaryRecyclerAdapter;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.BillProduct;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.BillSummaryHeaderModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.HeadingPaymentMode;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.SponceredModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.TotalBillDetail;

import java.util.ArrayList;


public class BillSummaryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<BillProduct> billProductsList;
    private ArrayList<Object> list;
    private BillSummaryRecyclerAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_summary);
        setTitle("Bill Summary");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        populateList();
        initViews();
    }

    private void populateList() {
        billProductsList = getIntent().getParcelableArrayListExtra("billProductsList");

        list = new ArrayList<>();

        list.add(new BillSummaryHeaderModel());

        for (BillProduct listModel : billProductsList) {
            list.add(listModel);
        }

        int totalPrice = 0;
        for (int i = 0; i < billProductsList.size(); i++) {
            BillProduct billProduct = ((BillProduct) billProductsList.get(i));
            int priceAfterGst = billProduct.getFinalPrice() * billProduct.getQuantity();
            totalPrice = totalPrice + priceAfterGst;
        }

        TotalBillDetail totalBillDetail = new TotalBillDetail("Net Amount", totalPrice);
        list.add(totalBillDetail);

        list.add(new HeadingPaymentMode("Payment Mode"));
        TotalBillDetail paymentSummary = new TotalBillDetail("Cash", totalPrice);
        list.add(paymentSummary);

        list.add(new SponceredModel());

    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BillSummaryRecyclerAdapter(list);

        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home) {
            Intent intent = new Intent(BillSummaryActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
