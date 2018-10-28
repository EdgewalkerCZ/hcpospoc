package com.example.vaibhavchahal93788.myapplication.billdesk.network;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.Application;

import java.io.IOException;
import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResponseHandler<T> implements Callback<T> {

    private IApiRequestComplete iApiRequestComplete;

    public ResponseHandler(IApiRequestComplete iApiRequestComplete) {
        this.iApiRequestComplete = iApiRequestComplete;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            iApiRequestComplete.onSuccess(response.body());
        } else {
            switch (response.code()) {
                case 401:
                    iApiRequestComplete.onFailure("Unauthorized user");
                    break;
                case 422:
                    iApiRequestComplete.onFailure("User already exists");
                    break;
                default:
                    iApiRequestComplete.onFailure("Something went wrong, please try again");
                    break;
            }
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        t.printStackTrace();
        if (t instanceof UnknownHostException){
            iApiRequestComplete.onFailure("Unable to resolve host");
        } else if (t instanceof IOException) {
            iApiRequestComplete.onFailure(t.getMessage());
        } else {
            iApiRequestComplete.onFailure("Something went wrong, please try again");
        }
    }
}
