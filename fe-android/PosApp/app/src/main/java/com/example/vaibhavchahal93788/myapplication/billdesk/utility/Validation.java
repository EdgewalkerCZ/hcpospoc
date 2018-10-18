package com.example.vaibhavchahal93788.myapplication.billdesk.utility;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Validation {


    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static boolean isValidPassword(String pass) {
        return pass != null && pass.length() >= 4;
    }
    public static boolean isGmailOTP(String pass) {
        return pass != null && pass.length() == 4;
    }

    public static boolean isTextEmpty(String message) {
        return message.length() == 0;
    }


    public static boolean isValidName(String name) {
        return name != null && name.length() >= 1;
    }

    public static boolean isValidMobile(String phone2) {
        boolean check;
        check = !(phone2.length() < 6 || phone2.length() > 13);
        return check;
    }

    public static boolean isValidMobileNumber(String mobile_num){
        boolean validnumber;
        Pattern pattern= Pattern.compile("^[6-9]\\d{9}$");
        Matcher matcher=pattern.matcher(mobile_num);
        validnumber=matcher.matches();


  return validnumber;
    }

}
