package com.example.vaibhavchahal93788.myapplication.billdesk.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
public class AppPreferences {

    public static AppPreferences mAppPreferences;
    private SharedPreferences mSharedPreferences;
    private Editor mEditor;

    enum SharePreferencesKey{
        userID,
        password,
        userName,
        jsessionid,
        langOpt

    }
    public AppPreferences(Context context){
        mSharedPreferences=context.getSharedPreferences(PreferenceID.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        mEditor=mSharedPreferences.edit();
    }

    public static synchronized AppPreferences getInstance(Context context){
        if (mAppPreferences==null){
            mAppPreferences= new AppPreferences(context);
        }

        return mAppPreferences;
    }

    public void clearAppPreferences(){

        if (mSharedPreferences!=null){
            mEditor.clear().commit();
        }
    }

    public void setLangOpt(String langOpt){
        mEditor.putString(SharePreferencesKey.langOpt.toString(),langOpt);
        mEditor.commit();
    }

    public String getLangOpt(){
        return mSharedPreferences.getString(SharePreferencesKey.langOpt.toString(),"en_US");
    }

    public void setJsesssionId(String jsesssionId){
        mEditor.putString(SharePreferencesKey.jsessionid.toString(),jsesssionId);
        mEditor.commit();
    }

    public String getJsessionId(){
        return mSharedPreferences.getString(SharePreferencesKey.jsessionid.toString(),"");
    }
}
