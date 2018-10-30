package com.example.vaibhavchahal93788.myapplication.billdesk.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.api.ProductApiHelper;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductCategoryModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.UpdateStatusResponse;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.allproduct.DataItem;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.updateProduct.UpdateProductModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.IApiRequestComplete;
import com.example.vaibhavchahal93788.myapplication.billdesk.preferences.AppPreferences;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Constants;

import java.util.HashMap;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;


public class DeleteProductActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvReduceCount,tvProductName,tvTag,tvProduct,tvRamSize,tvHdSize,tvColor,tvTotalprice,tvCurrentStock,tvUpdatedStock,tvReduceStock;
    private EditText edtStore,edtDamage,edtTheft,edtLoss,edtRestock;
    private Button btnUpdate,btnStoreRecountInrement,btnStoreRecountdecrement,btnDamageInrement,btnDamagedecrement,btnTheftInrement,btnTheftdecrement,btnLossInrement,btnLossdecrement,btnRestockInrement,btnRestockdecrement;
    private int storeCounter = 0, damageCounter =0,theftCounter =0,lossCounter = 0,restockCounter = 0;
    private DataItem productListModel;
    private Context context;
    private int updatedTotalQuantity;
    private AppPreferences mAppPreferences;
    private UpdateProductModel updateProductModel;
    private ProgressBar pb_dialogue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_product);
        mAppPreferences=AppPreferences.getInstance(this);
        setTitle("Remove");
        context = this;
        init();
        getStockData();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
/* get product data */
  private void getStockData() {
      productListModel = (DataItem) getIntent().getSerializableExtra(Constants.STOCK_DATA);
      setData();
      updateQuantity();
}

    private void setData() {
        tvProductName.setText(productListModel.getName());
        tvProduct.setText(productListModel.getDescription());
        tvTag.setText(productListModel.getName().charAt(0)+"".toUpperCase());
        double price=Double.valueOf(productListModel.getSalePrice());
        double roundOff = Math.round(price*100)/100;

        tvTotalprice.setText(roundOff+"");
        tvCurrentStock.setText(productListModel.getQuantity()+"");
    }

    private void init() {
        tvProductName = findViewById(R.id.tv_product_name);
        tvProduct = findViewById(R.id.tv_pname);
        tvRamSize = findViewById(R.id.tv_rsize);
        tvTag = findViewById(R.id.tv_product_tag);
        tvReduceCount = findViewById(R.id.tv_reduce_count);
        tvHdSize = findViewById(R.id.tv_hsize);
        tvColor = findViewById(R.id.tv_color);
        tvTotalprice = findViewById(R.id.tv_price);

        tvCurrentStock = findViewById(R.id.tv_stock);
        tvUpdatedStock = findViewById(R.id.tv_updated_stock);
        btnUpdate = findViewById(R.id.btn_update);

        btnStoreRecountdecrement = findViewById(R.id.btn_store_de);
        btnStoreRecountInrement = findViewById(R.id.btn_store_in);

        btnDamagedecrement = findViewById(R.id.btn_damage_de);
        btnDamageInrement = findViewById(R.id.btn_damage_in);

        btnTheftdecrement = findViewById(R.id.btn_theft_de);
        btnTheftInrement = findViewById(R.id.btn_theft_in);
        btnLossdecrement = findViewById(R.id.btn_loss_de);
        btnLossInrement = findViewById(R.id.btn_loss_in);
        btnRestockdecrement = findViewById(R.id.btn_restock_de);
        btnRestockInrement = findViewById(R.id.btn_restock_in);

        pb_dialogue=findViewById(R.id.pb_dialogue);

        edtStore = findViewById(R.id.edt_store);
        edtDamage = findViewById(R.id.edt_damage);
        edtLoss = findViewById(R.id.edt_loss);
        edtTheft = findViewById(R.id.edt_theft);
        edtRestock = findViewById(R.id.edt_restock);
        btnStoreRecountInrement.setOnClickListener(this);
        btnStoreRecountdecrement.setOnClickListener(this);
        btnStoreRecountdecrement.setClickable(false);
        btnDamagedecrement.setOnClickListener(this);
        btnDamagedecrement.setClickable(false);
        btnDamageInrement.setOnClickListener(this);
        btnTheftdecrement.setOnClickListener(this);
        btnTheftdecrement.setClickable(false);
        btnTheftInrement.setOnClickListener(this);

        btnLossdecrement.setOnClickListener(this);
        btnLossdecrement.setClickable(false);
        btnLossInrement.setOnClickListener(this);
        btnRestockdecrement.setOnClickListener(this);
        btnRestockdecrement.setClickable(false);
        btnRestockInrement.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /* increment and decrement functionality */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_store_de:
                int counterDe = --storeCounter;
                if(counterDe>=0){
                    edtStore.setText(counterDe+"");
                    if(counterDe==0){
                        btnStoreRecountdecrement.setClickable(false);
                        btnStoreRecountdecrement.setBackgroundColor(getResources().getColor(R.color.light_green));
                    }
                }
                updateQuantity();
                break;
            case R.id.btn_store_in:
                int counterIn = ++storeCounter;
                edtStore.setText(counterIn+"");
                btnStoreRecountdecrement.setClickable(true);
                btnStoreRecountdecrement.setBackgroundColor(getResources().getColor(R.color.color_add_button));
                updateQuantity();
                break;
            case R.id.btn_damage_de:
                int damagecounterDe = --damageCounter;
                if(damagecounterDe>=0){
                    edtDamage.setText(damagecounterDe+"");
                    if(damagecounterDe==0){
                        btnDamagedecrement.setBackgroundColor(getResources().getColor(R.color.light_green));
                        btnDamagedecrement.setClickable(false);
                    }
                }
                updateQuantity();
                break;

            case R.id.btn_damage_in:
                int damagecounterIn = ++damageCounter;
                edtDamage.setText(damagecounterIn+"");
                btnDamagedecrement.setClickable(true);
                btnDamagedecrement.setBackgroundColor(getResources().getColor(R.color.color_add_button));
                updateQuantity();
                break;

            case R.id.btn_theft_de:
                int theftcounterDe = --theftCounter;
                if(theftcounterDe>=0)
                {
                    edtTheft.setText(theftcounterDe+"");
                    if(theftcounterDe==0){
                        btnTheftdecrement.setBackgroundColor(getResources().getColor(R.color.light_green));
                        btnTheftdecrement.setClickable(false);
                    }

                }
                updateQuantity();
                break;
            case R.id.btn_theft_in:
                int theftcounterIn = ++theftCounter;
                edtTheft.setText(theftcounterIn+"");
                btnTheftdecrement.setClickable(true);
                btnTheftdecrement.setBackgroundColor(getResources().getColor(R.color.color_add_button));
                updateQuantity();
                break;
            case R.id.btn_loss_de:
                int losscounterDe = --lossCounter;
                if(losscounterDe>=0){
                    edtLoss.setText(losscounterDe+"");
                    if(losscounterDe==0){
                        btnLossdecrement.setBackgroundColor(getResources().getColor(R.color.light_green));
                        btnLossdecrement.setClickable(false);
                    }

                }
                updateQuantity();
                break;
            case R.id.btn_loss_in:
                int losscounterIn = ++lossCounter;
                edtLoss.setText(losscounterIn+"");
                btnLossdecrement.setClickable(true);
                btnLossdecrement.setBackgroundColor(getResources().getColor(R.color.color_add_button));
                updateQuantity();
                break;
            case R.id.btn_restock_de:
                int recounterDe = --restockCounter;
                if(recounterDe>=0){
                    if(recounterDe==0){
                        btnRestockdecrement.setBackgroundColor(getResources().getColor(R.color.light_green));
                        btnRestockdecrement.setClickable(false);
                    }

                    edtRestock.setText(recounterDe+"");
                }
                updateQuantity();
                break;
            case R.id.btn_restock_in:
                int restockcounterIn = ++restockCounter;
                edtRestock.setText(restockcounterIn+"");
                btnRestockdecrement.setClickable(true);
                btnRestockdecrement.setBackgroundColor(getResources().getColor(R.color.color_add_button));
                updateQuantity();
                break;


            case R.id.btn_update:
                if(updatedTotalQuantity!=0){
                  /*  Toast.makeText(context,R.string.successfully_message,Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, StockViewProductActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();*/
                    deleteProduct();
                }
              //  deleteProduct();
                break;


        }
    }

  private void updateQuantity(){
        int totalQuantiy = Integer.parseInt(edtStore.getText().toString())+Integer.parseInt(edtDamage.getText().toString()) +Integer.parseInt(edtTheft.getText().toString())+ Integer.parseInt(edtLoss.getText().toString())+ Integer.parseInt(edtRestock.getText().toString());
        updatedTotalQuantity = totalQuantiy;
        if(totalQuantiy>0){
            btnUpdate.setBackgroundColor(getResources().getColor(R.color.color_DA1A32));
        }else if(totalQuantiy==0){
            btnUpdate.setBackgroundColor(getResources().getColor(R.color.color_configuration));
        }
        tvReduceCount.setText(totalQuantiy+"");
        int updatedStock = productListModel.getQuantity()-totalQuantiy;
        tvUpdatedStock.setText(updatedStock+"");

  }

    private void deleteProduct() {
        pb_dialogue.setVisibility(View.VISIBLE);
        HashMap<String,String> headerValues= new HashMap<>();
        headerValues.put("Content-Type", "application/json");
        headerValues.put("Accept", "application/json");
        headerValues.put(Constants.SESSION_ID,mAppPreferences.getJsessionId());
        updateProductModel= new UpdateProductModel();
        updateProductModel.setId(productListModel.getId());
        updateProductModel.setCode(productListModel.getCode());
        updateProductModel.setProductCategoryId(productListModel.getProductCategoryId());
        updateProductModel.setProductFamilyId(productListModel.getProductFamilyId());
        updateProductModel.setQuantity(Integer.valueOf(tvUpdatedStock.getText().toString()));
        updateProductModel.setDescription(productListModel.getDescription());
        updateProductModel.setSalePrice( productListModel.getSalePrice());
        updateProductModel.setIsGst(true);
        updateProductModel.setIsSellable(true);
        updateProductModel.setName(productListModel.getName());
       /* "code": "DELL",
                "productCategoryId": 22,
                "productFamilyId": 9,
                "quantity": 60,
                "description": "\"\\\"Internal HDD 3,5'' - Capacity : 5\"",
                "salePrice": "60.0000000000",
                "isGst": false,
                "isSellable": true,
                "name": "HP headphones",
                "warrantyNbrOfMonths": 12*/

        new ProductApiHelper().productUpdateList(headerValues,updateProductModel,productListModel.getId(), new IApiRequestComplete<UpdateStatusResponse>() {

            @Override
            public void onSuccess(UpdateStatusResponse updateStatusResponse) {
                pb_dialogue.setVisibility(View.GONE);
                if (updateStatusResponse!=null){
                    if (updateStatusResponse.getStatus()==0){
                        final PrettyDialog dialog = new PrettyDialog(DeleteProductActivity.this);
                        dialog.setIcon(R.drawable.pdlg_icon_success, R.color.pdlg_color_green, null)   // Icon resource
                                .setTitle(getResources().getString(R.string.success))
                                .setMessage(getResources().getString(R.string.success_updated_product))
                                .addButton(getResources().getString(R.string.ok), R.color.pdlg_color_white, R.color.pdlg_color_green, new PrettyDialogCallback() {
                                    @Override
                                    public void onClick() {
                                        dialog.dismiss();
                                        Intent intent= new Intent(DeleteProductActivity.this,StockViewProductActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                });
                        dialog.show();

                    }
                }

            }

            @Override
            public void onFailure(String message) {
                pb_dialogue.setVisibility(View.GONE);
                final PrettyDialog dialog = new PrettyDialog(DeleteProductActivity.this);
                dialog.setIcon(R.drawable.pdlg_icon_success, R.color.pdlg_color_green, null)   // Icon resource
                        .setTitle(getResources().getString(R.string.success))
                        .setMessage(message)
                        .addButton(getResources().getString(R.string.ok), R.color.pdlg_color_white, R.color.pdlg_color_green, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                dialog.dismiss();

                            }
                        });
                dialog.show();
            }
        });
    }

}
