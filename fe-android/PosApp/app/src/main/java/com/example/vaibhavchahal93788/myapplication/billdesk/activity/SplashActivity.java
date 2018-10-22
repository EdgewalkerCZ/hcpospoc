package com.example.vaibhavchahal93788.myapplication.billdesk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.SelectProductActivity;
import com.example.vaibhavchahal93788.myapplication.billdesk.printing.Utils;

import okhttp3.internal.Util;

public class SplashActivity extends AppCompatActivity
{
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(Utils.isLogin())
                {
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    SplashActivity.this.startActivity(intent);
                    overridePendingTransition(R.anim.animation_enter,R.anim.animation_leave);
                }
                else
                {   //ToDo: LoginActivity
                    Intent intent = new Intent(SplashActivity.this, SelectProductActivity.class);
                    SplashActivity.this.startActivity(intent);
                    overridePendingTransition(R.anim.animation_enter,R.anim.animation_leave);
                }
                finish();


        }
        }, 3000);
    }
}
