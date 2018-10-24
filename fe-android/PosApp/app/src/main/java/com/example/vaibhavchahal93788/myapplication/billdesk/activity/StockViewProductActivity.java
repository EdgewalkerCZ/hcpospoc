package com.example.vaibhavchahal93788.myapplication.billdesk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.StockViewAdapter;
import com.example.vaibhavchahal93788.myapplication.billdesk.api.ProductApiHelper;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.AllProductModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductCategoryModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.IApiRequestComplete;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.ApiClient;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.ApiInterface;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.ApiUtils;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Constants;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockViewProductActivity extends AppCompatActivity implements  StockViewAdapter.OnItemClickListener, View.OnClickListener {

    private RecyclerView recyclerView;
    private EditText editTextSearch;
    private RelativeLayout relativeHeader;

    private StockViewAdapter adapter;
    private ProgressBar progreeBar;

    private int REQUEST_CODE = 11;
    private Spinner spinnerCategories;
    private String load_category_id = "0";
    private HashMap<String, String> hashMapCategories = new HashMap<>();
    private HashMap<String, String> hashMapCategoriesTax = new HashMap<>();
    private ArrayList<String> categoriesList;
   // private SwipeRefreshLayout pullToRefresh;
    private List<AllProductModel> productsList;
    MenuItem crossmenu;
    private Set<AllProductModel> mDataSelected = new HashSet<>();
    private boolean isSearchingProduct;

    public static final int REQ_CODE_SELECT_CATEGORY = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        setTitle("Product List");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();

        actionEditSearch();
        getCategoriesList();
        actionCategorySelection();

        actionAddProduct();

        fetchProductsList();
    }

    private void fetchProductsList() {

        progreeBar.setVisibility(View.VISIBLE);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<JsonObject> call = apiService.getAllProductsList();
        Log.e("request", call.request().url().toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                isSearchingProduct = false;
                progreeBar.setVisibility(View.GONE);
                Log.e("response", response.body().toString());
                if (ApiUtils.isSuccess(response.body().toString())) {
                    try {
                        JSONObject respObj = new JSONObject(response.body().toString());
                        JSONArray jsonArray = respObj.getJSONArray("items");
                        List<AllProductModel> list = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<AllProductModel>>() {
                        }.getType());
                        productsList = list;

                        setAdaptorData();
                       /* if(set != null) {
                            mAdapterSelectProduct.addData(new ArrayList<String>(getDataWithSeletedItems(set, mDataSelected)));
                        }*/
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                isSearchingProduct = false;
                progreeBar.setVisibility(View.GONE);
                Log.e("response", t.toString());
            }
        });
    }

    /* set Adaptor data */
    private void setAdaptorData() {
        if(productsList!=null && productsList.size()>0){
            editTextSearch.setEnabled(true);
            adapter = new StockViewAdapter(this, productsList, this);
            recyclerView.setAdapter(adapter);
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        editTextSearch =  findViewById(R.id.editTextSearch);
        progreeBar = findViewById(R.id.progress_bar);
        spinnerCategories =  findViewById(R.id.spinner_categories);
        relativeHeader = findViewById(R.id.rtl_header);


        adapter = new StockViewAdapter(this, new ArrayList<AllProductModel>(), this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void actionAddProduct() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoriesList != null && !categoriesList.isEmpty()) {
                    Intent intent = new Intent(StockViewProductActivity.this, AddNewProduct.class);
                    intent.putStringArrayListExtra("listCategories", categoriesList);
                    intent.putExtra("CategoriesIdMap", hashMapCategories);
                    intent.putExtra("CategoriesTaxMap", hashMapCategoriesTax);
                    startActivityForResult(intent
                            , REQUEST_CODE);
                    overridePendingTransition(R.anim.animation_enter,R.anim.animation_leave);
                } else {
                    Toast.makeText(StockViewProductActivity.this, R.string.msg_category_loading_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void getCategoriesList() {
        new ProductApiHelper().getCategoryList( new IApiRequestComplete<ProductCategoryModel>() {

            @Override
            public void onSuccess(ProductCategoryModel categoryList) {
                categoriesList = new ArrayList<>();
                categoriesList.add("Categories*");
                hashMapCategories.put("Categories*", "0");
                if(categoryList!=null){
                    for(int i = 0; i<categoryList.getCategories().size();i++){
                        categoriesList.add(categoryList.getCategories().get(i).getCategory());
                        hashMapCategories.put(categoryList.getCategories().get(i).getCategory(), categoryList.getCategories().get(i).getCategoryId());
                    }
                }

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                        (StockViewProductActivity.this, android.R.layout.simple_spinner_item, categoriesList);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                        .simple_spinner_dropdown_item);
                spinnerCategories.setAdapter(spinnerArrayAdapter);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(StockViewProductActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void actionCategorySelection() {
        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (productsList != null && productsList.size() > 0) {
                //    productsList.clear();
                }
                load_category_id = hashMapCategories.get(spinnerCategories.getSelectedItem().toString());
                categoryData(load_category_id);
             //   getProductList(load_category_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
               finish();
                return true;
            case R.id.cross:
                editTextSearch.setText("");
                fetchProductsList();
                relativeHeader.setVisibility(View.VISIBLE);
                setTitle("Product List");
                crossmenu.setVisible(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        ArrayList<AllProductModel> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (AllProductModel object : productsList) {
            //if the existing elements contains the search input
            if (object.getName().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(object);
            }
        }

        //calling a method of the adapter class and passing the filtered list

        int size = filterdNames.size();
        setTitle(filterdNames.size()+" item found");
        if(size==1)
            relativeHeader.setVisibility(View.GONE);
        else relativeHeader.setVisibility(View.VISIBLE);
        crossmenu.setVisible(true);

        adapter.filterList(filterdNames);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(StockViewProductActivity.this, ViewProductActivity.class);
        intent.putExtra(Constants.STOCK_DATA, productsList.get(position));
        startActivity(intent);
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


    /* get data category wise */

    private void categoryData(String load_category_id) {
        //new array list that will hold the filtered data
        ArrayList<AllProductModel> filterdNames = new ArrayList<>();

        //looping through existing elements

        if(load_category_id!=null && !load_category_id.equalsIgnoreCase("0")){
            for (AllProductModel object : productsList) {
                //if the existing elements contains the search input

                if(object.getCategoryID()!=null){
                    if (object.getCategoryID().equalsIgnoreCase(load_category_id)) {
                        //adding the element to filtered list
                        filterdNames.add(object);
                    }
                }
            }
            adapter.filterList(filterdNames);
        }else
        {
            fetchProductsList();
        }

        /*int size = filterdNames.size();
        if(size!=0)
        setTitle(filterdNames.size()+" item found");*/
        //calling a method of the adapter class and passing the filtered list

  /*      int size = filterdNames.size();
        setTitle(filterdNames.size()+" item found");
        if(size==1)
            relativeHeader.setVisibility(View.GONE);
        else relativeHeader.setVisibility(View.VISIBLE);
        crossmenu.setVisible(true);*/


    }
}
