package com.example.vaibhavchahal93788.myapplication.billdesk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Constants;

public class AddNewProduct extends AppCompatActivity implements View.OnClickListener {

    private Spinner  sp_product_type,sp_category,sp_sub_category;
    private Button bt_add;
    public static void startActivity(Activity activity) {
        Intent intent= new Intent(activity, AddNewProduct.class);
        intent.putExtra(activity.getResources().getString(R.string.parent_class_name), activity.getClass().getSimpleName());
        activity.overridePendingTransition(R.anim.animation_enter,R.anim.animation_leave);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);
        getToolbar();
        iniView();
        iniListener();

    }

    private void iniListener() {
        bt_add.setOnClickListener(this);
    }

    private void iniView() {
        bt_add=findViewById(R.id.bt_add);
        sp_product_type=findViewById(R.id.sp_product_type);
        sp_category=findViewById(R.id.sp_category);
        sp_sub_category=findViewById(R.id.sp_sub_category);
    }

    private void getToolbar() {

        getSupportActionBar().setHomeButtonEnabled(true);
        // getSupportActionBar().setHomeAsUpIndicator(R.drawable.rupee_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_icon_pos);
        getSupportActionBar().setTitle(getResources().getString(R.string.add_product_new));

    }
    @Override
    public Intent getSupportParentActivityIntent() {
        Intent parentIntent = getIntent();
        String className = parentIntent.getStringExtra(getResources().getString(R.string.parent_class_name)); //getting the parent class name
        Intent newIntent = null;
        try {
            //you need to define the class with package name
            newIntent = new Intent(AddNewProduct.this, Class.forName(Constants.PACKAGE + className));
            overridePendingTransition(R.anim.animation_enter_backward,R.anim.animation_leave_backward);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return newIntent;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_add:
                AddProductSubmit.startActivity(this);
                overridePendingTransition(R.anim.animation_enter,R.anim.animation_leave);
                break;
            default:

                break;
        }
    }
}
