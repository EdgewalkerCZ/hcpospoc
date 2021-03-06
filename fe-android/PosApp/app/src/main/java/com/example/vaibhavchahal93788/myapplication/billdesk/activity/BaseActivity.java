package com.example.vaibhavchahal93788.myapplication.billdesk.activity;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.vaibhavchahal93788.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }
    /**
     * Gets system's current date
     **/
    public String getSystemDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(c.getTime());
    }

    /**
     * Custom Dialog
     **/
    public void showDialog(final Context ctx, String title, String msg, final int code) {
        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert);

        TextView tvMessage = (TextView) dialog.findViewById(R.id.text_dialog);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
        tvMessage.setText(msg);
        tvTitle.setText(title);

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (code == 1)
                    ((AppCompatActivity) ctx).finish();
                else if (code == 2)
                    //startActivity(new Intent(ctx, LoginActivity.class));

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    }
