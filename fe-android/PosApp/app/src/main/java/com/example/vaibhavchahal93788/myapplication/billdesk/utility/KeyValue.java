package com.example.vaibhavchahal93788.myapplication.billdesk.utility;


import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by AKM on 01,February,2016
 * Source soft solution india pvt. ltd company,
 * Noida, India.
 */
public class KeyValue {

    private static final String KeyValue = "KeyValue";

    public static final String SOUND = "sound";
    public static final String SOUND_PUSH = "sound_push";
    public static final String NAME = "NAME";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";
    public static final String ADDRESS = "address";
    public static final String DOB = "dob";
    public static final String NOTE = "note";


    public static void setInt(Context context, String key, int value) {
        if (context == null)
            return;
//        if (LogFile.requestResponse)
//            LogFile.requestResponse("++   updateNotificationCounter setInt:" + key + " value:" + value);

        SharedPreferences.Editor editor = context.getSharedPreferences(KeyValue, Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getInt(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(KeyValue, Context.MODE_PRIVATE);
        return pref.getInt(key, 0);
    }

    public static void setLong(Context context, String key, long value) {
        if (context == null)
            return;
        SharedPreferences.Editor editor = context.getSharedPreferences(KeyValue, Context.MODE_PRIVATE).edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static long getLong(Context context, String key) {

        SharedPreferences pref = context.getSharedPreferences(KeyValue,
                Context.MODE_PRIVATE);
        return pref.getLong(key, 0);
    }

    public static void setString(Context context, String key, String value) {
        if (context == null)
            return;
        SharedPreferences.Editor editor = context.getSharedPreferences(KeyValue, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(Context context, String key) {
        try {
            SharedPreferences pref = context.getSharedPreferences(KeyValue, Context.MODE_PRIVATE);
            return pref.getString(key, null);
        } catch (Exception e) {
            return null;
        }
    }

    public static void setBoolean(Context context, String key, boolean value) {
        if (context == null)
            return;
        SharedPreferences.Editor editor = context.getSharedPreferences(KeyValue, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getBoolean(Context context, String key) {
        try {
            SharedPreferences pref = context.getSharedPreferences(KeyValue,Context.MODE_PRIVATE);
            boolean res = pref.getBoolean(key, false);
            if (key != null && key.equalsIgnoreCase(SOUND)) {
                if (res)
                    res = true;
                else
                    res = false;
            }
            return res;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean getBooleanPush(Context context, String key) {
        try {
            SharedPreferences pref = context.getSharedPreferences(KeyValue,
                    Context.MODE_PRIVATE);
            boolean res = pref.getBoolean(key, false);
            if (key != null && key.equalsIgnoreCase(SOUND_PUSH)) {
                if (res)
                    res = true;
                else
                    res = false;
            }
            return res;
        } catch (Exception e) {
            return false;
        }
    }
}
