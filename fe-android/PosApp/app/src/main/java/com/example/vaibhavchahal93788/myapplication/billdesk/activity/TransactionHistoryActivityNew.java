package com.example.vaibhavchahal93788.myapplication.billdesk.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.TransactionHistoryAdapterNew;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.TranHistoryNew;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TransactionHistoryActivityNew extends BaseActivity {

    List<TranHistoryNew> tranHistoryNewList;
    RecyclerView rvProducts;
    PieChart chart;
    TextView tvTodayDAte;
    Button btnDownloadOrMailHistory;
    View dialogDownloadOrMail;
    EditText etSearchHistory;
    TransactionHistoryAdapterNew adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history_new);

        setTitle("Transaction History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Initialize Views
        initViews();

        populateList();
        populateList2();
        //showPieChart();
        drawPieGraph();
        tvTodayDAte.setText(getSystemDate());

        btnDownloadOrMailHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(TransactionHistoryActivityNew.this);
            }
        });

        etSearchHistory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter(charSequence.toString());
            }
            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                //filter(editable.toString());
            }
        });
    }
      /*Initialize views*/
      public void initViews(){
          rvProducts = findViewById(R.id.rvHistory);
          LinearLayoutManager lm = new LinearLayoutManager(TransactionHistoryActivityNew.this);
          lm.setOrientation(LinearLayoutManager.VERTICAL);
          rvProducts.setLayoutManager(lm);
          chart = findViewById(R.id.chart);
          tvTodayDAte = findViewById(R.id.tvTodayDAte);
          btnDownloadOrMailHistory = findViewById(R.id.btnDownloadOrMailHistory);
          dialogDownloadOrMail = findViewById(R.id.dialogDownload);
          etSearchHistory = findViewById(R.id.etSearchHistory);
      }

     private void populateList2(){

     }

    private void populateList() {

        tranHistoryNewList = new ArrayList<>();

        tranHistoryNewList.add(new TranHistoryNew("Opp F9", "10-Oct-2018, 11:10", 2, 17000));
        tranHistoryNewList.add(new TranHistoryNew("Mi Note 3", "15-Oct-2018, 13:10", 1, 15000));
        tranHistoryNewList.add(new TranHistoryNew("Redmi A5", "10-Oct-2018, 10:10", 3, 14000));
        tranHistoryNewList.add(new TranHistoryNew("Moto G3", "24-Sep-2018, 16:10", 1, 17000));
        tranHistoryNewList.add(new TranHistoryNew("Samsung", "10-Sep-2018, 11:15", 2, 20000));

        adapter = new TransactionHistoryAdapterNew(TransactionHistoryActivityNew.this, tranHistoryNewList);

        rvProducts.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filter) {
            Intent intent = new Intent(TransactionHistoryActivityNew.this, FilterHistoryActivity.class);
            startActivity(intent);
        }else
            finish();

        return super.onOptionsItemSelected(item);
    }
    /**
     * Draw Pie chart
     **/
    public void drawPieGraph(){
        try{
            final PieChart pieChart = findViewById(R.id.chart);
            List<PieEntry> entries = new ArrayList<>();
            entries.add(new PieEntry(52000, " Card  \u20B9 15000"));
            entries.add(new PieEntry(38000, " Cash  \u20B9 20000"));
            pieChart.setEntryLabelColor(Color.RED);
            //PieDataSet set = new PieDataSet(entries, "History Evaluation");
            PieDataSet set = new PieDataSet(entries,"");
            set.setDrawValues(false);
            pieChart.setDrawEntryLabels(false);
            set.setColors(new int[] { R.color.color_33a4a0, R.color.color_33a470}, this);
            PieData data = new PieData(set);
            pieChart.setData(data);
            //pieChart.invalidate(); // refresh
            set.setDrawValues(false);
            pieChart.setCenterText(generateCenterSpannableText());
            // if no need to add description
            pieChart.getDescription().setEnabled(false);
            pieChart.setDrawSliceText(false);
            // animation and the center text color
            pieChart.animateY(5000);
            pieChart.setEntryLabelColor(Color.BLACK);
            /**/
            pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener()
            {
                @Override
                public void onValueSelected(Entry e, Highlight h)
                {
                    int c =e.describeContents();
                    float x=e.getX();
                    float y=e.getY();
                    PieEntry pe = (PieEntry) e;
                    //Log.e("LABEL",pe.getLabel());
                    //showDialog(TransactionHistoryActivityNew.this, "Info", pe.getLabel()+"- Total Amount[In Crore]="+y,0);
                }
                @Override
                public void onNothingSelected()
                {

                }
            });

        }catch (Exception e){}
    }

    /**
     * Custom Dialog for Download Options
     **/
    public void showDialog(final Context ctx) {
        //dialogDownloadOrMail.setVisibility(View.VISIBLE);
        //btnDownloadOrMailHistory.setVisibility(View.GONE);
        final BottomSheetDialog dialog = new BottomSheetDialog(ctx);
        View view = getLayoutInflater().inflate(R.layout.dialog_download_history, null);
        ImageView ivClose=view.findViewById(R.id.ivClose);
        TextView tvDownloadPDF =view. findViewById(R.id.tvDownloadPDFDialog);
        TextView tvSendMail = view.findViewById(R.id.tvSendMailDialog);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });





        //ImageView ivClose =  findViewById(R.id.ivClose);



       /* ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDownloadOrMail.setVisibility(View.GONE);
                btnDownloadOrMailHistory.setVisibility(View.VISIBLE);
            }
        });*/
        tvDownloadPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadPDF();
                dialogDownloadOrMail.setVisibility(View.GONE);
                btnDownloadOrMailHistory.setVisibility(View.VISIBLE);
            }
        });
        tvSendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
                //$$$$$$$$$$$$$$$$

                //dialogDownloadOrMail.setVisibility(View.GONE);
                //btnDownloadOrMailHistory.setVisibility(View.VISIBLE);

            }
        });

        dialog.setContentView(view);
        dialog.show();
    }

    /*Download PDF*/
    public void downloadPDF(){

    }
    /*Send Mail*/
    public void sendMail(){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"recipient@example.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
        i.putExtra(Intent.EXTRA_TEXT   , "body of email");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(TransactionHistoryActivityNew.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    // If we need to display center text with textStyle
    private SpannableString generateCenterSpannableText() {
        SpannableString s = new SpannableString("\u20B9 90,000 \n Total Value ");
        s.setSpan(new StyleSpan(Typeface.BOLD), 11, s.length(), 0);
        s.setSpan(new RelativeSizeSpan(1f), 11, s.length(), 0);
        return s;
    }

    private void filter(String text) {
        //new array list that will hold the filtered data
        List<TranHistoryNew> thList = new ArrayList<>();

        //looping through existing elements
        for (TranHistoryNew th : tranHistoryNewList) {
            //if the existing elements contains the search input
            if (th.getpName().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                thList.add(th);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        adapter.filterList(thList);
    }
}




