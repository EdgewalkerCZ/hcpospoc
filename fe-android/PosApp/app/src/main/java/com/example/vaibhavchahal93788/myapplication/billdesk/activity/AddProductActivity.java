package com.example.vaibhavchahal93788.myapplication.billdesk.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.api.ProductApiHelper;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.AddCategoryModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.AddProductModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.IApiRequestComplete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


public class AddProductActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar progressBar;
    private EditText etProductName, etProductPrice, etProductAvlbleQty, etProductDescptn;
    private Spinner spinnerCategories;
    private HashMap<String, String> mapCategoriesId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        setTitle("Add Product");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        populateCategories();

        progressBar = findViewById(R.id.progress_bar);
        findViewById(R.id.btn_add_product).setOnClickListener(this);
        etProductName = findViewById(R.id.et_productname);
        etProductPrice = findViewById(R.id.et_price);
        etProductAvlbleQty = findViewById(R.id.et_quantity);
        etProductDescptn = findViewById(R.id.et_productdescription);
    }


    private void populateCategories() {
        spinnerCategories = findViewById(R.id.spinner_product_category);

        ArrayList<String> categoriesList = getIntent().getStringArrayListExtra("listCategories");
        mapCategoriesId = (HashMap<String, String>) getIntent().getSerializableExtra("CategoriesIdMap");

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (AddProductActivity.this, android.R.layout.simple_spinner_item, categoriesList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(spinnerArrayAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

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
        String selectedCategoryId = mapCategoriesId.get(spinnerCategories.getSelectedItem().toString());
        if (etProductName.getText().toString().isEmpty() || etProductPrice.getText().toString().isEmpty() || etProductDescptn.getText().toString().isEmpty() || etProductAvlbleQty.getText().toString().isEmpty() || selectedCategoryId == null) {
            if (selectedCategoryId == null) {
                Toast.makeText(this, R.string.text_select_category, Toast.LENGTH_LONG).show();
            } else if (etProductName.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.prodct_name_req, Toast.LENGTH_LONG).show();
            } else if (etProductAvlbleQty.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.text_quantity_req, Toast.LENGTH_LONG).show();
            } else if (etProductPrice.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.text_price_req, Toast.LENGTH_LONG).show();
            } else if (etProductDescptn.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.text_decrtn_required, Toast.LENGTH_LONG).show();
            }
        } else {
            progressBar.setVisibility(View.VISIBLE);
            AddCategoryModel addCategoryModel = new AddCategoryModel(selectedCategoryId, spinnerCategories.getSelectedItem().toString());
            AddProductModel addProductModel = new AddProductModel(etProductName.getText().toString(), etProductDescptn.getText().toString(), etProductPrice.getText().toString(), etProductName.getText().toString() + UUID.randomUUID().toString(), addCategoryModel);
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


