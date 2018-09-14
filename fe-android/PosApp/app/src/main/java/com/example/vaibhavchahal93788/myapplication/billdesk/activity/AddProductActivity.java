package com.example.vaibhavchahal93788.myapplication.billdesk.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.IApiRequestComplete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


public class AddProductActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar progressBar;
    private EditText etProductName, etBasePrice, etProductAvlbleQty, etProductDescptn, etProductTax, etFinalPrice, etTaxInfo;
    private Spinner spinnerCategories;
    private HashMap<String, String> mapCategoriesId;
    private ProductListModel productModel;
    private HashMap<String, String> mapCategoriesTax;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        setTitle("Add Product");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();

        populateCategories();
    }

    private void initViews() {
        progressBar = findViewById(R.id.progress_bar);
        findViewById(R.id.btn_add_product).setOnClickListener(this);
        etProductName = findViewById(R.id.et_productname);
        etProductTax = findViewById(R.id.et_tax);
        etBasePrice = findViewById(R.id.et_price);
        etProductAvlbleQty = findViewById(R.id.et_quantity);
        etProductDescptn = findViewById(R.id.et_productdescription);
        etFinalPrice = findViewById(R.id.et_final_price);
        etTaxInfo = findViewById(R.id.et_taxinfo);

        etBasePrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int taxPrice = 0;
                int finalPrice = 0;

                if (mapCategoriesTax.get(spinnerCategories.getSelectedItem().toString()) != null) {
                    if (!etBasePrice.getText().toString().isEmpty()) {
                        taxPrice = Integer.parseInt(etBasePrice.getText().toString()) * Integer.parseInt(mapCategoriesTax.get(spinnerCategories.getSelectedItem().toString())) / 100;
                        etTaxInfo.setText("+ " + taxPrice + " tax");
                        finalPrice = Integer.parseInt(etBasePrice.getText().toString()) + taxPrice;
                        etFinalPrice.setText("" + finalPrice);
                    } else {
                        etTaxInfo.setText("+ " + taxPrice + " tax");
                        etFinalPrice.setText("" + finalPrice);
                    }
                } else {
                    etFinalPrice.setText("" + etBasePrice.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void populateCategories() {
        spinnerCategories = findViewById(R.id.spinner_product_category);

        ArrayList<String> categoriesList = getIntent().getStringArrayListExtra("listCategories");
        mapCategoriesId = (HashMap<String, String>) getIntent().getSerializableExtra("CategoriesIdMap");
        mapCategoriesTax = (HashMap<String, String>) getIntent().getSerializableExtra("CategoriesTaxMap");

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (AddProductActivity.this, android.R.layout.simple_spinner_item, categoriesList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(spinnerArrayAdapter);

        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                int taxPrice = 0;
                int finalPrice = 0;

                if (mapCategoriesTax.get(spinnerCategories.getSelectedItem().toString()) != null) {
                    if (!etBasePrice.getText().toString().isEmpty()) {
                        taxPrice = Math.round(Float.parseFloat(etBasePrice.getText().toString())) * Integer.parseInt(mapCategoriesTax.get(spinnerCategories.getSelectedItem().toString())) / 100;
                        etTaxInfo.setText("+ " + taxPrice + " tax");
                        finalPrice = Integer.parseInt(etBasePrice.getText().toString()) + taxPrice;
                        etFinalPrice.setText("" + finalPrice);
                    } else {
                        etTaxInfo.setText("");
                        etFinalPrice.setText("" + finalPrice);
                    }
                } else {
                    etTaxInfo.setText("");
                    etFinalPrice.setText("" + etBasePrice.getText().toString());
                }

                if (mapCategoriesTax.get(spinnerCategories.getSelectedItem().toString()) != null) {
                    etProductTax.setText("Gst@ " + mapCategoriesTax.get(spinnerCategories.getSelectedItem().toString()) + "%");
                } else {
                    etProductTax.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        prefillProductDetails(categoriesList);
    }

    private void prefillProductDetails(ArrayList<String> categoriesList) {
        productModel = (ProductListModel) getIntent().getSerializableExtra("productModel");
        if (productModel != null) {
            etProductName.setText(productModel.getLabel());
            etProductDescptn.setText(productModel.getDescription());
            etBasePrice.setText("" + Math.round(Float.parseFloat(productModel.getPrice())));
            etFinalPrice.setText("" + Math.round(Float.parseFloat(productModel.getFinalPrice())));

//            String productCategory = productModel.getCategoryModel().getCategoryName();
//            int selectedCategoryPosition = 0;
//            for (int i = 0; i < categoriesList.size(); i++) {
//                if (categoriesList.get(i).equals(productCategory)) {
//                    selectedCategoryPosition = i;
//                    break;
//                }
//            }
//            spinnerCategories.setSelection(selectedCategoryPosition);
        }
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
                addOrUpdateProduct();
                break;

            default:
                break;
        }
    }

    private void addOrUpdateProduct() {
        String selectedCategoryId = mapCategoriesId.get(spinnerCategories.getSelectedItem().toString());
        if (etProductName.getText().toString().isEmpty() || etBasePrice.getText().toString().isEmpty() || etProductDescptn.getText().toString().isEmpty() || etProductAvlbleQty.getText().toString().isEmpty() || selectedCategoryId == null || etProductName.getText().toString().isEmpty()) {
            if (selectedCategoryId == null) {
                Toast.makeText(this, R.string.text_select_category, Toast.LENGTH_LONG).show();
            } else if (etProductName.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.prodct_name_req, Toast.LENGTH_LONG).show();
            } else if (etProductAvlbleQty.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.text_quantity_req, Toast.LENGTH_LONG).show();
            } else if (etBasePrice.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.text_price_req, Toast.LENGTH_LONG).show();
            } else if (etProductDescptn.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.text_decrtn_required, Toast.LENGTH_LONG).show();
            }
        } else {
            progressBar.setVisibility(View.VISIBLE);
            AddCategoryModel addCategoryModel = new AddCategoryModel(selectedCategoryId, spinnerCategories.getSelectedItem().toString());
            if (productModel != null) {
                updateProduct(productModel.getId(), addCategoryModel);
            } else {
                addProduct(addCategoryModel);
            }
        }
    }

    private void addProduct(AddCategoryModel addCategoryModel) {
        AddProductModel addProductModel = new AddProductModel(etProductName.getText().toString(), etProductDescptn.getText().toString(), etBasePrice.getText().toString(), etFinalPrice.getText().toString(), etProductName.getText().toString() + UUID.randomUUID().toString(), addCategoryModel, "1", "1");
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

    private void updateProduct(String productId, AddCategoryModel addCategoryModel) {
        AddProductModel addProductModel = new AddProductModel(etProductName.getText().toString(), etProductDescptn.getText().toString(), etBasePrice.getText().toString(), addCategoryModel);
        new ProductApiHelper().updateProduct(productId, addProductModel, new IApiRequestComplete() {

            @Override
            public void onSuccess(Object response) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AddProductActivity.this, R.string.msg_product_update_success, Toast.LENGTH_SHORT).show();
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


