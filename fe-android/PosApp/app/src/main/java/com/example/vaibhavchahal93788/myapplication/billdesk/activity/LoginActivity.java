package com.example.vaibhavchahal93788.myapplication.billdesk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.api.LoginApiHelper;
import com.example.vaibhavchahal93788.myapplication.billdesk.api.ProductApiHelper;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.LoginBodyModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.LoginModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.userlogin.LoginSuccessResponse;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.userlogin.UserLoginModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.IApiRequestComplete;
import com.example.vaibhavchahal93788.myapplication.billdesk.preferences.AppPreferences;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Utility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private TextView txtLogin;
    private EditText edtUsername;
    private EditText edtPassword;
    private ProgressBar progress_bar;
    private boolean isUsernameFilled = false;
    private boolean isPasswordFilled = false;
    private AppPreferences mAppPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAppPreferences = AppPreferences.getInstance(this);
        txtLogin = (TextView) findViewById(R.id.txtLogin);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);


        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUsernameFilled && isPasswordFilled)
                    validateLogin();
            }
        });

        edtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isUsernameFilled = s.length() != 0;
            }

            @Override
            public void afterTextChanged(Editable s) {
                txtLogin.setBackgroundResource(isUsernameFilled && isPasswordFilled ?
                        R.drawable.login_rounded_background_enable :
                        R.drawable.login_rounded_background_disable);
            }
        });

        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isPasswordFilled = s.length() != 0;
            }

            @Override
            public void afterTextChanged(Editable s) {
                txtLogin.setBackgroundResource(isUsernameFilled && isPasswordFilled ?
                        R.drawable.login_rounded_background_enable :
                        R.drawable.login_rounded_background_disable);
            }
        });

    }

    private void validateLogin() {

        progress_bar.setVisibility(View.VISIBLE);

        LoginBodyModel loginBodyModel = new LoginBodyModel();
        loginBodyModel.setUsername(edtUsername.getText().toString());
        loginBodyModel.setPassword(edtPassword.getText().toString());
        new ProductApiHelper().userLogin(loginBodyModel, new IApiRequestComplete<LoginSuccessResponse>() {
            @Override
            public void onSuccess(LoginSuccessResponse response) {
                if (response != null) {
                    progress_bar.setVisibility(View.GONE);
                    mAppPreferences.setJsesssionId(response.getJSESSIONID());
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    LoginActivity.this.startActivity(intent);
                    overridePendingTransition(R.anim.animation_enter_backward, R.anim.animation_leave_backward);
                    finish();
                }
            }

            @Override
            public void onFailure(String message) {
                Utility.showToast(getApplicationContext(), message);
                progress_bar.setVisibility(View.GONE);
            }
        });

    }
    /*{
        progress_bar.setVisibility(View.VISIBLE);
        LoginBodyModel loginBodyModel = new LoginBodyModel();
        loginBodyModel.setUsername(edtUsername.getText().toString());
        loginBodyModel.setPassword(edtPassword.getText().toString());

        new LoginApiHelper().validateLogin(loginBodyModel, new IApiRequestComplete<LoginModel>() {
            @Override
            public void onSuccess(LoginModel response) {
                if(response.getJSESSIONID() != null)
                {
                    //for now, considering this as successful login
                    //save this JSESSIONID for future communication
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    LoginActivity.this.startActivity(intent);
                    overridePendingTransition(R.anim.animation_enter,R.anim.animation_leave);

                    finish();
                }
                else
                {
                    onFailure("");
                }
                progress_bar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String message) {
                progress_bar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Login failed : " + message, Toast.LENGTH_SHORT).show();

            }
        });
    }*/
}
