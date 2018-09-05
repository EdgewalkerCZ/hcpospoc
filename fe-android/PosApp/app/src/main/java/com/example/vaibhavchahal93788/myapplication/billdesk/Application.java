package com.example.vaibhavchahal93788.myapplication.billdesk;

import android.content.Context;

public class Application extends android.app.Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getAppContext() {
        return context;
    }
}
