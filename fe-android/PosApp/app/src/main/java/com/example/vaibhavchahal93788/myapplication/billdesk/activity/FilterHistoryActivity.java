package com.example.vaibhavchahal93788.myapplication.billdesk.activity;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.vaibhavchahal93788.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FilterHistoryActivity extends AppCompatActivity implements View.OnClickListener{

    TextView tvFromDate, tvToDate, tvToday, tvSevenDays, tvThirtyDays, tvCustomDateFilter;
    Button btnCash, btnCard;
    LinearLayout llCustomDateFilter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_history);

        setTitle("Filters");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        Initialize views
        initViews();
        disableLLChildren(llCustomDateFilter);
    }

    public void initViews(){
        tvFromDate = findViewById(R.id.tvFromDateFilter);
        tvToDate = findViewById(R.id.tvToDateFilter);
        tvToday = findViewById(R.id.tvTodayFilter);
        tvSevenDays = findViewById(R.id.tvSevenDaysFilter);
        tvThirtyDays = findViewById(R.id.tvThirtyDaysFilter);
        tvCustomDateFilter = findViewById(R.id.tvCustomDateFilter);
        llCustomDateFilter = findViewById(R.id.llCustomDateFilter);
        btnCash = findViewById(R.id.btnCash);
        btnCard = findViewById(R.id.btnCard);
        btnCash.setOnClickListener(this);
        btnCard.setOnClickListener(this);
        tvFromDate.setOnClickListener(this);
        tvToDate.setOnClickListener(this);
        tvToday.setOnClickListener(this);
        tvSevenDays.setOnClickListener(this);
        tvThirtyDays.setOnClickListener(this);
        tvCustomDateFilter.setOnClickListener(this);
    }

    public void datePicker(final TextView tv){
        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //String myFormat = "dd MMMM yyyy"; // your format
                String myFormat = "dd/MM/yyyy"; // your format
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

                tv.setText(sdf.format(myCalendar.getTime()));
            }
        };
        new DatePickerDialog(FilterHistoryActivity.this, date,
                myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvFromDateFilter:
            case R.id.tvToDateFilter:
                datePicker((TextView)view);
                break;
            case R.id.tvTodayFilter:
            case R.id.tvSevenDaysFilter:
            case R.id.tvThirtyDaysFilter:
            case R.id.tvCustomDateFilter:
                selectDate((TextView)view);
                break;
            case R.id.btnCash:
            case R.id.btnCard:
                btnCash.setBackground(getResources().getDrawable(R.drawable.custom_border_hover));
                btnCash.setTextColor(getResources().getColor(R.color.colorPrimary));
                btnCard.setBackground(getResources().getDrawable(R.drawable.custom_border_hover));
                btnCard.setTextColor(getResources().getColor(R.color.colorPrimary));

                view.setBackground(getResources().getDrawable(R.drawable.corner_shap));
                ((Button)view).setTextColor(getResources().getColor(R.color.white_color));
        }
    }

    /*Select date*/
    public void selectDate(TextView tv){

        tvToday.setBackground(getResources().getDrawable(R.drawable.custom_border_hover));
        tvToday.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvSevenDays.setBackground(getResources().getDrawable(R.drawable.custom_border_hover));
        tvSevenDays.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvThirtyDays.setBackground(getResources().getDrawable(R.drawable.custom_border_hover));
        tvThirtyDays.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvCustomDateFilter.setBackground(getResources().getDrawable(R.drawable.custom_border_hover));
        tvCustomDateFilter.setTextColor(getResources().getColor(R.color.colorPrimary));

        tv.setBackground(getResources().getDrawable(R.drawable.corner_shap));
        tv.setTextColor(getResources().getColor(R.color.white_color));

        if(tv.getId()==R.id.tvCustomDateFilter)
            enableLLChildren(llCustomDateFilter);
        else
            disableLLChildren(llCustomDateFilter);

    }

    /*Disable linearlayout children*/
    public void disableLLChildren(LinearLayout myLayout){
        for ( int i = 0; i < myLayout.getChildCount();  i++ ){
            View view = myLayout.getChildAt(i);
            view.setEnabled(false);
        }
    }
    /*Disable linearlayout children*/
    public void enableLLChildren(LinearLayout myLayout){
        for ( int i = 0; i < myLayout.getChildCount();  i++ ){
            View view = myLayout.getChildAt(i);
            view.setEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.filter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clear) {
        }else
            finish();

        return super.onOptionsItemSelected(item);
    }
}
