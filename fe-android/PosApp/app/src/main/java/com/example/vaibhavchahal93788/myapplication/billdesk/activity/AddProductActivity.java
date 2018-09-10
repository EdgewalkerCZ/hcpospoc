package com.example.vaibhavchahal93788.myapplication.billdesk.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.api.ProductApiHelper;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.AddCategoryModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.AddProductModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.IApiRequestComplete;

import java.util.List;
import java.util.UUID;

import okhttp3.ResponseBody;


public class AddProductActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar progressBar;
    private EditText etProductName, etProductPrice, etProductAvlbleQty, etProductDescptn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        setTitle("Add Product");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progress_bar);
        findViewById(R.id.btn_add_product).setOnClickListener(this);
        etProductName = findViewById(R.id.et_productname);
        etProductPrice = findViewById(R.id.et_price);
        etProductAvlbleQty = findViewById(R.id.et_quantity);
        etProductDescptn = findViewById(R.id.et_productdescription);
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
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_product:
                addProduct();
                break;

            default:
                break;
        }
    }

    private void addProduct() {
        if (etProductName.getText().toString().isEmpty() || etProductPrice.getText().toString().isEmpty() || etProductDescptn.getText().toString().isEmpty()) {
            if (etProductName.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.prodct_name_req, Toast.LENGTH_LONG).show();
            } else if (etProductPrice.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.text_price_req, Toast.LENGTH_LONG).show();
            } else if (etProductDescptn.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.text_decrtn_required, Toast.LENGTH_LONG).show();
            }
        } else {
            progressBar.setVisibility(View.VISIBLE);
            AddProductModel addProductModel = new AddProductModel(etProductName.getText().toString(), etProductDescptn.getText().toString(), etProductPrice.getText().toString(), etProductName.getText().toString() + UUID.randomUUID().toString(), new AddCategoryModel("", ""));
            new ProductApiHelper().addNewProduct(addProductModel, new IApiRequestComplete() {

                @Override
                public void onSuccess(Object response) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddProductActivity.this, R.string.text_add_product_success, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onFailure(String message) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddProductActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}


