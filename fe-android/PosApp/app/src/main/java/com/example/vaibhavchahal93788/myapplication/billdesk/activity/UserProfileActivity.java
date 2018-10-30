package com.example.vaibhavchahal93788.myapplication.billdesk.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.api.LoginApiHelper;
import com.example.vaibhavchahal93788.myapplication.billdesk.api.ProductApiHelper;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.profile.ProfileResponse;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.IApiRequestComplete;
import com.example.vaibhavchahal93788.myapplication.billdesk.preferences.AppPreferences;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Constants;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.KeyValue;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Utility;

import java.util.Locale;

public class UserProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner sp_lang;
    private Locale locale;
    private AppPreferences mAppPreferences;
    private ImageView im_profile;
    private ImageButton im_camera;
    private TextView tv_store_name, tv_mobile, tv_email_id, tv_address, tv_merchant_name;
    private ProgressBar pb_dialogue;


    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, UserProfileActivity.class);
        intent.putExtra(activity.getResources().getString(R.string.parent_class_name), activity.getClass().getSimpleName());
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mAppPreferences = AppPreferences.getInstance(this);
        iniview();
        getToolbar();
        iniListner();
        getUserProfile();
    }

    private void getUserProfile() {
        pb_dialogue.setVisibility(View.VISIBLE);
        pb_dialogue.bringToFront();
        new LoginApiHelper().userProfile(new IApiRequestComplete<ProfileResponse>() {
            @Override
            public void onSuccess(ProfileResponse response) {
                if (response != null) {
                    //im_profile;
                    Glide.with(getApplicationContext())
                            .load(response.getImageUrl())
                            .thumbnail(.1f)
                            .apply(RequestOptions.circleCropTransform())
                            .into(im_profile);
                    tv_store_name.setText(response.getStoreName());
                    tv_mobile.setText(response.getMobileNumber());
                    tv_email_id.setText(response.getEmail());
                    KeyValue.setString(UserProfileActivity.this,KeyValue.USER_EMAIL,response.getEmail());
                    tv_address.setText(response.getAddress());
                    tv_merchant_name.setText(response.getMerchant());
                    pb_dialogue.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(String message) {
                pb_dialogue.setVisibility(View.GONE);
            }
        });
    }

    private void iniListner() {
        sp_lang.setOnItemSelectedListener(this);

    }

    private void iniview() {
        sp_lang = findViewById(R.id.sp_lang);
        im_profile = findViewById(R.id.im_profile);
        im_camera = findViewById(R.id.im_camera);
        tv_store_name = findViewById(R.id.tv_store_name);
        tv_mobile = findViewById(R.id.tv_mobile);
        tv_email_id = findViewById(R.id.tv_email_id);
        tv_address = findViewById(R.id.tv_address);
        tv_merchant_name = findViewById(R.id.tv_merchant_name);
        pb_dialogue=findViewById(R.id.pb_dialogue);
    }


    private void getToolbar() {

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.profile));

    }

    @Override
    public Intent getSupportParentActivityIntent() {
        Intent parentIntent = getIntent();
        String className = parentIntent.getStringExtra(getResources().getString(R.string.parent_class_name)); //getting the parent class name
        Intent newIntent = null;
        try {
            //you need to define the class with package name
            newIntent = new Intent(UserProfileActivity.this, Class.forName(Constants.PACKAGE + className));
            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

            case R.id.sp_lang:
                //Resources resources = getResources();
                if (position == 0) {
                   // Utility.showToast(getApplicationContext(), Constants.ENGLISH_LOCALE);

                  /*  locale = new Locale(Constants.ENGLISH_LOCALE);
                    mAppPreferences.setLangOpt(Constants.ENGLISH_LOCALE);*/
                } else if (position == 1) {
                   // Utility.showToast(getApplicationContext(), Constants.HINDI_LOCALE);
               /*     locale = new Locale(Constants.HINDI_LOCALE);
                    mAppPreferences.setLangOpt(Constants.HINDI_LOCALE);*/
                }
               /* Configuration configuration = resources.getConfiguration();
                configuration.setLocale(locale);
                getBaseContext().getResources().updateConfiguration(configuration,
                        getBaseContext().getResources().getDisplayMetrics());
                recreate();*/
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
