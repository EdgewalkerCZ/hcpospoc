package com.example.vaibhavchahal93788.myapplication.billdesk.payment.apiary;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiUtils {

    public static boolean isSuccess(String response) {
        try {
            JSONObject resp = new JSONObject(response);
            return resp.getString("status").equalsIgnoreCase("success");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

}
