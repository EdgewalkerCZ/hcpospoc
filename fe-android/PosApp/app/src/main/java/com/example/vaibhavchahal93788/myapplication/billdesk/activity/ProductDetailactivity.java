package com.example.vaibhavchahal93788.myapplication.billdesk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.ProductListAdapter;
import com.example.vaibhavchahal93788.myapplication.billdesk.api.ProductApiHelper;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.CategoryModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.IApiRequestComplete;

import java.util.ArrayList;
import java.util.HashMap;
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
    private Spinner spinnerCategories;
    private String load_category_id = "0";
    private HashMap<String, String> hashMapCategories = new HashMap<>();
    private HashMap<String, String> hashMapCategoriesTax = new HashMap<>();
    private ArrayList<String> categoriesList;
    private SwipeRefreshLayout pullToRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        setTitle("Product List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViews();
        actionEditSearch();
        getCategoriesList();
        actionCategorySelection();
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        spinnerCategories = (Spinner) findViewById(R.id.spinner_categories);
        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        txtviewEstPrice = (TextView) findViewById(R.id.tv_est_price);
        rlTotalCharge = (RelativeLayout) findViewById(R.id.rl_charge);
        progreeBar = (ProgressBar) findViewById(R.id.progress_bar);

        findViewById(R.id.btn_payment).setOnClickListener(this);

        pullToRefresh();
    }

    private void pullToRefresh() {
        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.pull_to_refresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCategoriesList();
                rlTotalCharge.setVisibility(View.GONE);
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    private void actionCategorySelection() {
        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (productsList != null && productsList.size() > 0) {
                    productsList.clear();
                }

                rlTotalCharge.setVisibility(View.GONE);
                load_category_id = hashMapCategories.get(spinnerCategories.getSelectedItem().toString());
                getProductList(load_category_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setadapter(List<ProductListModel> productList) {
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
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_payment:
                ArrayList<ProductListModel> list = getSelectedItemList();
                Intent intent = new Intent(ProductDetailactivity.this, BillDetailActivity.class);
                intent.putExtra("selectedItemList", list);
                intent.putExtra("totalItems", totalItem);
                intent.putExtra("totalPrice", totalPrice);
//                intent.putExtra("categoryList", totalPrice);
                startActivity(intent);
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

    public void getProductList(String category) {
        progreeBar.setVisibility(View.VISIBLE);
        new ProductApiHelper().fetchProductList("t.ref", "ASC", 100, category, new IApiRequestComplete<List<ProductListModel>>() {

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

    public void getCategoriesList() {
        new ProductApiHelper().fetchCategoryList("t.rowid", "ASC", 100, "product", new IApiRequestComplete<List<CategoryModel>>() {

            @Override
            public void onSuccess(List<CategoryModel> categoryList) {
                categoriesList = new ArrayList<>();
                categoriesList.add("Categories*");
                for (CategoryModel categoryModel : categoryList) {
                    if (categoryModel.getParentType().equals("0")) {
                        categoriesList.add(categoryModel.getLabel());
                        hashMapCategories.put(categoryModel.getLabel(), categoryModel.getId());
                        hashMapCategoriesTax.put(categoryModel.getLabel(), categoryModel.getTaxCode().substring(2));
                    }
                }
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                        (ProductDetailactivity.this, android.R.layout.simple_spinner_item, categoriesList);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                        .simple_spinner_dropdown_item);
                spinnerCategories.setAdapter(spinnerArrayAdapter);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(ProductDetailactivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            getProductList(load_category_id);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.product_list_menu, menu);

        View view = menu.findItem(R.id.action_add_product).getActionView();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoriesList != null && !categoriesList.isEmpty()) {
                    Intent intent = new Intent(ProductDetailactivity.this, AddProductActivity.class);
                    intent.putStringArrayListExtra("listCategories", categoriesList);
                    intent.putExtra("CategoriesTaxMap", hashMapCategoriesTax);
                    intent.putExtra("CategoriesIdMap", hashMapCategories);
                    startActivityForResult(intent, REQUEST_CODE);
                } else {
                    Toast.makeText(ProductDetailactivity.this, R.string.msg_category_loading_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_product) {
            Intent intent = new Intent(ProductDetailactivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
