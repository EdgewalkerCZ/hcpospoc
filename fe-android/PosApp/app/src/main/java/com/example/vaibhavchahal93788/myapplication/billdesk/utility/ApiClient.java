package com.example.vaibhavchahal93788.myapplication.billdesk.utility;

import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient{
    public static final String BASE_URL = "https://private-14bc25-hcinpospoc.apiary-mock.com/";   //private-83b6be-durgesh1.apiary-mock.com   //private-14bc25-hcinpospoc.apiary-mock.com
        private static Retrofit retrofit = null;

        public static Retrofit getClient() {
        /*HttpUrl baseUrl = new HttpUrl.Builder()
                .scheme(SCHEME)
                .host(HOST_NAME)
                .encodedPath("/")
                .build();*/

            if (retrofit==null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(getUnsafeOkHttpClient())
                        .build();
            }
            return retrofit;
        }


        public static OkHttpClient getUnsafeOkHttpClient() {

            try {
                // Create a trust manager that does not validate certificate chains
                final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] chain,
                            String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] chain,
                            String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[0];
                    }
                } };

                // Install the all-trusting trust manager
                final SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustAllCerts,
                        new java.security.SecureRandom());
                // Create an ssl socket factory with our all-trusting manager
                final SSLSocketFactory sslSocketFactory = sslContext
                        .getSocketFactory();

                OkHttpClient okHttpClient = new OkHttpClient();
                okHttpClient = okHttpClient.newBuilder()
                        .sslSocketFactory(sslSocketFactory)
                        .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build();

                return okHttpClient;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

}
