package com.example.vaibhavchahal93788.myapplication.billdesk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Toast;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.ProductStockListAdapter;
import com.example.vaibhavchahal93788.myapplication.billdesk.api.ProductApiHelper;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.CategoryModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.IApiRequestComplete;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StockDetailActivity extends AppCompatActivity implements ProductStockListAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private EditText editTextSearch;
    private RelativeLayout relativeHeader;
    private List<ProductListModel> productsList;
    private ProductStockListAdapter adapter;
    private ProgressBar progreeBar;
    private int REQUEST_CODE = 11;
    private Spinner spinnerCategories;
    private String load_category_id = "0";
    private HashMap<String, String> hashMapCategories = new HashMap<>();
    private HashMap<String, String> hashMapCategoriesTax = new HashMap<>();
    private ArrayList<String> categoriesList;
    private SwipeRefreshLayout pullToRefresh;
    private boolean delProductFlag = false;
    MenuItem crossmenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        setTitle("Product List");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        updateFlag();
        initViews();

        actionEditSearch();
        getCategoriesList();
        actionCategorySelection();
    }

    /*
     manage the delete product flow
     */
    private void updateFlag() {
        delProductFlag = getIntent().getBooleanExtra(Constants.DELETE_FLAG,false);
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        progreeBar = (ProgressBar) findViewById(R.id.progress_bar);
        spinnerCategories = (Spinner) findViewById(R.id.spinner_categories);
        relativeHeader = (RelativeLayout) findViewById(R.id.rtl_header);

        pullToRefresh();
        actionAddProduct();
    }

    private void pullToRefresh() {
        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.pull_to_refresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCategoriesList();
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    private void actionAddProduct() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoriesList != null && !categoriesList.isEmpty()) {
                    Intent intent = new Intent(StockDetailActivity.this, AddNewProduct.class);
                    intent.putStringArrayListExtra("listCategories", categoriesList);
                    intent.putExtra("CategoriesIdMap", hashMapCategories);
                    intent.putExtra("CategoriesTaxMap", hashMapCategoriesTax);
                    startActivityForResult(intent
                            , REQUEST_CODE);
                    overridePendingTransition(R.anim.animation_enter,R.anim.animation_leave);
                } else {
                    Toast.makeText(StockDetailActivity.this, R.string.msg_category_loading_failed, Toast.LENGTH_SHORT).show();
                }
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
                load_category_id = hashMapCategories.get(spinnerCategories.getSelectedItem().toString());
                getProductList(load_category_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setAdapter(List<ProductListModel> productList) {
        adapter = new ProductStockListAdapter(productList, this);

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
        if(delProductFlag){
            int size = filterdNames.size();
            setTitle(filterdNames.size()+" item found");
            if(size==1)
                relativeHeader.setVisibility(View.GONE);
            else relativeHeader.setVisibility(View.VISIBLE);
            crossmenu.setVisible(true);

        }

        adapter.filterList(filterdNames);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cross_menu, menu);
        return true;
    }
    public boolean onPrepareOptionsMenu(Menu menu) {
        //  preparation code here
         crossmenu = menu.findItem(R.id.cross);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

            case R.id.cross:
                editTextSearch.setText("");
                getProductList(load_category_id);
                relativeHeader.setVisibility(View.VISIBLE);
                setTitle("Product List");
                crossmenu.setVisible(false);
                return true;
        }

        return super.onOptionsItemSelected(item);
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
                setAdapter(productList);
            }

            @Override
            public void onFailure(String message) {
                progreeBar.setVisibility(View.GONE);
                Toast.makeText(StockDetailActivity.this, message, Toast.LENGTH_SHORT).show();
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
                        (StockDetailActivity.this, android.R.layout.simple_spinner_item, categoriesList);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                        .simple_spinner_dropdown_item);
                spinnerCategories.setAdapter(spinnerArrayAdapter);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(StockDetailActivity.this, message, Toast.LENGTH_SHORT).show();
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
    public void onItemClick(int position) {

        if(delProductFlag){

        }else {
            Intent intent = new Intent(StockDetailActivity.this, AddProductActivity.class);
            intent.putStringArrayListExtra("listCategories", categoriesList);
            intent.putExtra("CategoriesIdMap", hashMapCategories);
            intent.putExtra("CategoriesTaxMap", hashMapCategoriesTax);
            intent.putExtra("productModel", productsList.get(position));
            startActivityForResult(intent, REQUEST_CODE);
        }
    }
}
