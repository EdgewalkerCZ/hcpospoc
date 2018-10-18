package com.example.vaibhavchahal93788.myapplication.billdesk.payment.api;

import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String BASE_URL = "https://private-83b6be-durgesh1.apiary-mock.com/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        HttpUrl baseUrl = new HttpUrl.Builder()
                .scheme("https")
                .host("private-83b6be-durgesh1.apiary-mock.com")//.encodedPath("/")
                .build();

        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
