package com.example.vaibhavchahal93788.myapplication.billdesk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Constants;


public class AddProductSubmit extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner sp_product_type, sp_category, sp_sub_category,  sp_color, sp_variant;
    private LinearLayout  ll_mobile, ll_power_bank, ll_wired_ear_phones,ll_television,ll_refregerator;

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, AddProductSubmit.class);
        intent.putExtra(activity.getResources().getString(R.string.parent_class_name), activity.getClass().getSimpleName());
        activity.overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_submit);
        getToolbar();
        iniView();
        iniListner();
    }

    private void iniListner() {

        sp_category.setOnItemSelectedListener(this);
        sp_product_type.setOnItemSelectedListener(this);
        sp_sub_category.setOnItemSelectedListener(this);
    }

    private void iniView() {


        sp_product_type = findViewById(R.id.sp_product_type);
        sp_category = findViewById(R.id.sp_category);
        sp_sub_category = findViewById(R.id.sp_sub_category);

        sp_color= findViewById(R.id.sp_color);
        sp_variant= findViewById(R.id.sp_variant);

        ll_mobile= findViewById(R.id.ll_mobile);
        ll_power_bank= findViewById(R.id.ll_power_bank);
        ll_wired_ear_phones= findViewById(R.id.ll_wired_ear_phones);
        ll_television= findViewById(R.id.ll_television);
        ll_refregerator= findViewById(R.id.ll_refregerator);
    }

    private void getToolbar() {

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.add_product_new));

    }

    @Override
    public Intent getSupportParentActivityIntent() {
        Intent parentIntent = getIntent();
        String className = parentIntent.getStringExtra(getResources().getString(R.string.parent_class_name)); //getting the parent class name
        Intent newIntent = null;
        try {
            //you need to define the class with package name
            newIntent = new Intent(AddProductSubmit.this, Class.forName(Constants.PACKAGE + className));
            newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            overridePendingTransition(R.anim.animation_enter_backward, R.anim.animation_leave_backward);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return newIntent;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.sp_product_type:
                if (position == 1) {

                    hideAndResetAllVariantValues();

                    ArrayAdapter<CharSequence> dataAdapter =  ArrayAdapter.createFromResource(this,
                            R.array.product_categories, android.R.layout.simple_spinner_item);
                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // attaching data adapter to spinner
                    sp_category.setAdapter(dataAdapter);


                } else if (position == 2) {
                    hideAndResetAllVariantValues();
                    ArrayAdapter<CharSequence> dataAdapter =  ArrayAdapter.createFromResource(this,
                            R.array.product_categories, android.R.layout.simple_spinner_item);
                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // attaching data adapter to spinner
                    sp_category.setAdapter(dataAdapter);
                }

                break;
            case R.id.sp_category:
                if (position==1){
                    hideAndResetAllVariantValues();

                    ArrayAdapter<CharSequence> dataAdapter =  ArrayAdapter.createFromResource(this,
                            R.array.product_sub_categories_mobile, android.R.layout.simple_spinner_item);
                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // attaching data adapter to spinner
                    sp_sub_category.setAdapter(dataAdapter);

                }else if (position==2){

                    hideAndResetAllVariantValues();
                    ArrayAdapter<CharSequence> dataAdapter =  ArrayAdapter.createFromResource(this,
                            R.array.product_sub_categories_mobile_accessories, android.R.layout.simple_spinner_item);
                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // attaching data adapter to spinner
                    sp_sub_category.setAdapter(dataAdapter);





                }else if (position==3){

                    hideAndResetAllVariantValues();
                    ArrayAdapter<CharSequence> dataAdapter =  ArrayAdapter.createFromResource(this,
                            R.array.product_sub_categories_home_appliances, android.R.layout.simple_spinner_item);
                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // attaching data adapter to spinner
                    sp_sub_category.setAdapter(dataAdapter);
                }
                break;
            case R.id.sp_sub_category:
                  if (position==1 && sp_sub_category.getSelectedItem().equals("Power Bank")){
                      /* Mobile Accessories*/
                      ll_mobile.setVisibility(View.GONE);
                      ll_power_bank.setVisibility(View.VISIBLE);
                      ll_wired_ear_phones.setVisibility(View.GONE);
                      ll_television.setVisibility(View.GONE);
                      ll_refregerator.setVisibility(View.GONE);


                  }else if (position==1 && sp_sub_category.getSelectedItem().equals("Mobile")){
                      ll_mobile.setVisibility(View.VISIBLE);
                      ll_power_bank.setVisibility(View.GONE);
                      ll_wired_ear_phones.setVisibility(View.GONE);
                      ll_television.setVisibility(View.GONE);
                      ll_refregerator.setVisibility(View.GONE);
                  }else if (position==2&&sp_sub_category.getSelectedItem().equals("Wired Ear Phones")){
                      ll_mobile.setVisibility(View.GONE);
                      ll_power_bank.setVisibility(View.GONE);
                      ll_wired_ear_phones.setVisibility(View.VISIBLE);
                      ll_television.setVisibility(View.GONE);
                      ll_refregerator.setVisibility(View.GONE);
                  }else if (position==1&&sp_sub_category.getSelectedItem().equals("Televisions")){
                      ll_mobile.setVisibility(View.GONE);
                      ll_power_bank.setVisibility(View.GONE);
                      ll_wired_ear_phones.setVisibility(View.GONE);
                      ll_television.setVisibility(View.VISIBLE);
                      ll_refregerator.setVisibility(View.GONE);
                  }else if (position==2 && sp_sub_category.getSelectedItem().equals("Refrigerator")){
                      ll_mobile.setVisibility(View.GONE);
                      ll_power_bank.setVisibility(View.GONE);
                      ll_wired_ear_phones.setVisibility(View.GONE);
                      ll_television.setVisibility(View.GONE);
                      ll_refregerator.setVisibility(View.VISIBLE);


                  }
                break;

        }
    }

    private void hideAndResetAllVariantValues() {

        ll_mobile.setVisibility(View.GONE);
        ll_power_bank.setVisibility(View.GONE);
        ll_wired_ear_phones.setVisibility(View.GONE);
        ll_television.setVisibility(View.GONE);
        ll_refregerator.setVisibility(View.GONE);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
