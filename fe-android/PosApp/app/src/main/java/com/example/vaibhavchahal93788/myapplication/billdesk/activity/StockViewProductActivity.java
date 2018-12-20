package com.example.vaibhavchahal93788.myapplication.billdesk.activity;

import android.content.Context;
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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.StockViewAdapter;
import com.example.vaibhavchahal93788.myapplication.billdesk.api.ProductApiHelper;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.AllProductModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.allproduct.AllProductResponse;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.allproduct.DataItem;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.IApiRequestComplete;
import com.example.vaibhavchahal93788.myapplication.billdesk.preferences.AppPreferences;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Constants;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private AppPreferences mAppPreferences;
    private List<DataItem> dataItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
        mAppPreferences=AppPreferences.getInstance(this);
        setTitle("Product List");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();

        actionEditSearch();
        // getCategoriesList();
        actionCategorySelection();

        actionAddProduct();
        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    closeKeyboard();
                    return true;
                }
                return false;
            }
        });


    }

    private void fetchProductsList() {

        progreeBar.setVisibility(View.VISIBLE);
        HashMap<String,String> headerValues= new HashMap<>();
        headerValues.put("Content-Type", "application/json");
        headerValues.put("Accept", "application/json");
        headerValues.put(Constants.SESSION_ID,mAppPreferences.getJsessionId());
        new ProductApiHelper().getProductList(headerValues, new IApiRequestComplete<AllProductResponse>() {
            @Override
            public void onSuccess(final AllProductResponse response) {
                isSearchingProduct = false;
                progreeBar.setVisibility(View.GONE);
                if (response!=null){
                    if (response.getData().size()!=0){
                        setAdapter(response);
                    }else {
                        Utility.showToast(getApplicationContext(),getResources().getString(R.string.no_data));
                    }
                }
            }

            @Override
            public void onFailure(String message) {
                progreeBar.setVisibility(View.GONE);
            }
        });

    }

    public void setAdapter(final AllProductResponse response){
        editTextSearch.setEnabled(true);
        dataItems=response.getData();
        adapter = new StockViewAdapter(StockViewProductActivity.this, response.getData(), new StockViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, List<DataItem> productList) {
                Intent intent = new Intent(StockViewProductActivity.this, ViewProductActivity.class);
                intent.putExtra(Constants.STOCK_DATA, productList.get(position));
                startActivity(intent);
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.addItemDecoration(new DividerItemDecoration(this,
//                DividerItemDecoration.VERTICAL));

        editTextSearch =  findViewById(R.id.editTextSearch);
        progreeBar = findViewById(R.id.progress_bar);
        spinnerCategories =  findViewById(R.id.spinner_categories);
        relativeHeader = findViewById(R.id.rtl_header);

    }

    private void actionAddProduct() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddProductSubmit.startActivity(StockViewProductActivity.this);
                overridePendingTransition(R.anim.animation_enter_backward,R.anim.animation_leave_backward);

            }
        });
    }
   /* private void getCategoriesList() {
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
*/


    private void actionCategorySelection() {
        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                if (position==1){
                    categoryData(7);
                }else if (position==2){
                    categoryData(8);
                }else if (position==3){
                    categoryData(9);
                }else {
                    fetchProductsList();
                }
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
                closeKeyboard();

                editTextSearch.setText("");
                fetchProductsList();
                relativeHeader.setVisibility(View.VISIBLE);
                setTitle("Product List");
                crossmenu.setVisible(false);
                closeKeyboard();
                resetSpinnerCategorySelection();
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
        ArrayList<DataItem> filterdNames = new ArrayList<>();
       // AllProductResponse res=new AllProductResponse();

        //looping through existing elements
        //List<DataItem>
        if (dataItems!=null&&dataItems.size()!=0){
            for (DataItem dataItem : dataItems) {
                //if the existing elements contains the search input
                if (dataItem.getName().toLowerCase().contains(text.toLowerCase())) {
                    //adding the element to filtered list
                    filterdNames.add(dataItem);
                }
            }
           // res.setData(filterdNames);
            adapter.filterList(filterdNames);
        }

        //calling a method of the adapter class and passing the filtered list

        int size = filterdNames.size();
        setTitle(filterdNames.size()+" item found");
        if(size==1){
            relativeHeader.setVisibility(View.VISIBLE);
        }else {
            if (text.equals("")){
                relativeHeader.setVisibility(View.VISIBLE);
                crossmenu.setVisible(false);
                setTitle("Product List");
            }else {
                relativeHeader.setVisibility(View.VISIBLE);
                crossmenu.setVisible(true);
            }

        }
        if (filterdNames!=null&&filterdNames.size()!=0){
            adapter.filterList(filterdNames);

        }

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemClick(int position, List<DataItem> productList) {
        Intent intent = new Intent(StockViewProductActivity.this, ViewProductActivity.class);
        intent.putExtra(Constants.STOCK_DATA, productList.get(position));
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

    private void categoryData(int load_category_id) {
        //new array list that will hold the filtered data
        ArrayList<DataItem> filterdNames = new ArrayList<>();

        //looping through existing elements

        if(load_category_id!=0){
            if (dataItems!=null&&dataItems.size()!=0){
                for (DataItem dataItem : dataItems) {
                    //if the existing elements contains the search input

                    if(dataItem.getProductFamilyId()!=0){
                        if (dataItem.getProductFamilyId()==( load_category_id)) {
                            //adding the element to filtered list
                            filterdNames.add(dataItem);
                        }
                    }
                }
                adapter.filterList(filterdNames);
            }else
            {
                fetchProductsList();
            }
        }



        if(filterdNames.size()!=0)
            setTitle(filterdNames.size()+" item found");
        //calling a method of the adapter class and passing the filtered list
        int size = filterdNames.size();
        setTitle(filterdNames.size()+" item found");
        //################# Below line comment
       /* if(size==1)
            relativeHeader.setVisibility(View.GONE);
        else relativeHeader.setVisibility(View.VISIBLE);*/
        crossmenu.setVisible(true);


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fetchProductsList();
        relativeHeader.setVisibility(View.VISIBLE);
        crossmenu.setVisible(false);
        setTitle("Product List");
        editTextSearch.setText("");
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void resetSpinnerCategorySelection() {
        ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(this,
                R.array.product_categories, android.R.layout.simple_spinner_item);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinnerCategories.setAdapter(dataAdapter);
    }


}
