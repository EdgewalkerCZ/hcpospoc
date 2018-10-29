package com.example.vaibhavchahal93788.myapplication.billdesk.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.api.ProductApiHelper;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductCategoryModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.addproduct.PostAddProduct;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.allproduct.DataItem;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.IApiRequestComplete;
import com.example.vaibhavchahal93788.myapplication.billdesk.preferences.AppPreferences;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Constants;

import java.util.HashMap;

public class ViewProductActivity extends AppCompatActivity implements View.OnClickListener {

    private MenuItem removeMenu;
    private DataItem productListModel;
    private TextView tvProductName, tvProduct, tvTag, tvRamSize, tvHdSize, tvColor, tvSerialNumber, tvGSTPercent, tvGSTPrice, tvTotalprice, tvDescription;
    private EditText edtQuantity, edtPrice;
    private Button addUnitBtn, updateBtn;
    private int unitCounter = 1;
    private PostAddProduct postAddProduct;
    private AppPreferences mAppPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);
        mAppPreferences=AppPreferences.getInstance(this);
        init();
        getStockData();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /*
    initialize variable
    */
    private void init() {
        tvProductName = findViewById(R.id.tv_product_name);
        tvProduct = findViewById(R.id.tv_pname);
        tvTag = findViewById(R.id.tv_product_tag);
        tvRamSize = findViewById(R.id.tv_rsize);
        tvHdSize = findViewById(R.id.tv_hsize);
        tvColor = findViewById(R.id.tv_color);
        tvSerialNumber = findViewById(R.id.serial_number);
        edtQuantity = findViewById(R.id.edt_quantity);
        edtPrice = findViewById(R.id.edt_price);
        tvGSTPercent = findViewById(R.id.tv_cal_gst);
        tvGSTPrice = findViewById(R.id.tv_gst_amount);
        tvTotalprice = findViewById(R.id.tv_final_price);
        tvDescription = findViewById(R.id.tv_description_text);
        addUnitBtn = findViewById(R.id.btn_add_unit);
        updateBtn = findViewById(R.id.btn_update);
        addUnitBtn.setOnClickListener(this);
        updateBtn.setOnClickListener(this);
    }

    /* set the data in view mode */
    private void setData() {
        if (productListModel != null) {
            tvProductName.setText(productListModel.getName());
            tvProduct.setText(productListModel.getDescription());
            tvTag.setText(productListModel.getName().charAt(0) + "".toUpperCase());
            // tvRamSize.setText(productListModel.getRam());
            // tvHdSize.setText(productListModel.getRom());
            //tvColor.setText(productListModel.getColor());
            edtQuantity.setText(productListModel.getQuantity() + "");
            tvDescription.setText(productListModel.getDescription());
            tvGSTPercent.setText("GST(" + 18 + "%)");

            tvGSTPrice.setText("");
            double price = Double.valueOf(productListModel.getSalePrice());
            double roundOff_price = Math.round(price * 100) / 100;
            edtPrice.setText("" + roundOff_price);


            double gst_price, base_price, total_price = 0.0;
            gst_price = roundOff_price * 18 / 100;
            total_price = roundOff_price + gst_price;
            tvTotalprice.setText("₹" + total_price);
           /* if(productListModel.getGst()>0) {
                   int taxPercent = (productListModel.getGst());
                   Float percentage =(float)taxPercent/100;
                   Float percentageAmount = (float)percentage*productListModel.getPrice();
                   tvGSTPrice.setText("(₹"+percentageAmount+")");
                   float finalPrice = productListModel.getPrice()+percentageAmount;
                   tvTotalprice.setText("₹"+finalPrice);
            }*/
            unitCounter = Integer.parseInt(edtQuantity.getText().toString());
        }

    }

    /*
    getting the stock data,display in view mode
     */
    private void getStockData() {
        productListModel = (DataItem) getIntent().getSerializableExtra(Constants.STOCK_DATA);
        setData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.remove_menu, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        //  preparation code here
        removeMenu = menu.findItem(R.id.btn_remove);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.btn_remove:
                Intent intent = new Intent(ViewProductActivity.this, DeleteProductActivity.class);
                intent.putExtra(Constants.STOCK_DATA, productListModel);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_unit:
                int numberOfUnit = ++unitCounter;
                edtQuantity.setText(numberOfUnit + "");
                break;

            case R.id.btn_update:
                //Toast.makeText(this,R.string.update_message,Toast.LENGTH_LONG).show();
                updateProduct();
                break;
        }
    }

    private void updateProduct() {
        HashMap<String,String> headerValues= new HashMap<>();
        headerValues.put("Content-Type", "application/json");
        headerValues.put("Accept", "application/json");
        headerValues.put(Constants.SESSION_ID,mAppPreferences.getJsessionId());
        postAddProduct= new PostAddProduct();
        postAddProduct.setId(productListModel.getId());

        new ProductApiHelper().updateProduct(productListModel.getId(), edtQuantity.getText().toString(), new IApiRequestComplete<ProductCategoryModel>() {

            @Override
            public void onSuccess(ProductCategoryModel categoryList) {

            }

            @Override
            public void onFailure(String message) {

            }
        });
    }
}