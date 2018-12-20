package com.example.vaibhavchahal93788.myapplication.billdesk.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.StockViewAdapter;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.TransactionHistoryAdapterNew;
import com.example.vaibhavchahal93788.myapplication.billdesk.api.ProductApiHelper;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.BillProduct;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.CustomerModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.InvoiceLineIdList;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.InvoiceList;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.InvoiceModelNew;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.TranHistoryNew;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.allproduct.AllProductResponse;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.billproduct.BillProductInvoice;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.billproduct.BillProductInvoiceData;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.IApiRequestComplete;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.CustomerSearchActivity;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.ViewCustomerDetailActivity;
import com.example.vaibhavchahal93788.myapplication.billdesk.preferences.AppPreferences;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Constants;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.KeyValue;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Utility;
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
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TransactionHistoryActivityNew extends BaseActivity {

    List<TranHistoryNew> tranHistoryNewList;
    RecyclerView rvProducts;
    PieChart chart;
    TextView tvTodayDAte;
    Button btnDownloadOrMailHistory;
    View dialogDownloadOrMail;
    EditText etSearchHistory;
    TransactionHistoryAdapterNew adapter;
    boolean isLastProductGet = false;
    int productListCount = 0;
    int currentProduct = 0;
    private ProgressBar progreeBar;
    private AppPreferences mAppPreferences;
    ArrayList<BillProduct> billProductsList = new ArrayList<>();
    public static String filterText = "Today : ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history_new);

        mAppPreferences=AppPreferences.getInstance(this);
        setTitle("Transaction History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Initialize Views
        initViews();
        filterText = "Today : ";
        String myId = UUID.randomUUID().toString();

        getInvoiceList();
        populateList2();
        //showPieChart();
        drawPieGraph();
        filterText = filterText + getSystemDate();
        tvTodayDAte.setText(filterText);

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

    @Override
    protected void onResume() {
        super.onResume();
        tvTodayDAte.setText(filterText);
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
        progreeBar = findViewById(R.id.progress_bar);
    }

    private void populateList2(){

    }

    /*Populate invoice list*/
    private void populateList(final InvoiceModelNew response) {
        InvoiceList iv;
        tranHistoryNewList = new ArrayList<>();
        for(int i=0;i<response.getData().size();i++){
            iv = response.getData().get(i);
            tranHistoryNewList.add(new TranHistoryNew(iv.getNote(), iv.getInvoiceDate(), iv.getAmountPaid()));
        }
        adapter = new TransactionHistoryAdapterNew(TransactionHistoryActivityNew.this, tranHistoryNewList, new TransactionHistoryAdapterNew.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                isLastProductGet = false;
                int ci = response.getData().get(position).getCustomerId();
                List<InvoiceLineIdList> myList = response.getData().get(position).getInvoiceLineIdList();
                if(ci!=0 && myList!=null) {
                    getCustomerDetails(response.getData().get(position).getCustomerId(),
                            response.getData().get(position).getInvoiceLineIdList());
                }else
                    Toast.makeText(getApplicationContext(),"Details not available",
                            Toast.LENGTH_LONG).show();
            }
        });
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
            entries.add(new PieEntry(52000, " Card  \u20B9 52000"));
            entries.add(new PieEntry(38000, " Cash  \u20B9 38000"));

            pieChart.setEntryLabelColor(Color.RED);
            //PieDataSet set = new PieDataSet(entries, "History Evaluation");
            PieDataSet set = new PieDataSet(entries,"");
            set.setDrawValues(false);
            pieChart.setDrawEntryLabels(false);
            set.setColors(new int[] { R.color.color_ffac2a, R.color.color_33a4a0}, this);
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
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, 9, 0);
        s.setSpan(new RelativeSizeSpan(1f), 9, s.length(), 0);
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

    /*Satish code- Get customer details*/

    //Get invoice list from server
    public void getInvoiceList(){
        showProgress(TransactionHistoryActivityNew.this, "Getting invoices...");
        HashMap<String,String> headerValues= new HashMap<>();
        headerValues.put("Content-Type", "application/json");
        headerValues.put("Accept", "application/json");
        headerValues.put(Constants.SESSION_ID,mAppPreferences.getJsessionId());

        new ProductApiHelper().getInvoiceList(headerValues, new IApiRequestComplete<InvoiceModelNew>() {
            @Override
            public void onSuccess(final InvoiceModelNew response) {
                stopProgress();
                if (response!=null){
                    if (response.getData().size()!=0){
                        populateList(response);
                    }else {
                        //Utility.showToast(getApplicationContext(),getResources().getString(R.string.no_data));
                    }
                }
            }

            @Override
            public void onFailure(String message) {
                stopProgress();
                Utility.showToast(getApplicationContext(),getResources().getString(R.string.no_data));
            }
        });
    }

    //Get customer details from server
    public void getCustomerDetails(int customerId, final List<InvoiceLineIdList> invoiceLineIdList){
        showProgress(TransactionHistoryActivityNew.this, "Fetching details...");
        HashMap<String,String> headerValues= new HashMap<>();
        headerValues.put("Content-Type", "application/json");
        headerValues.put("Accept", "application/json");
        headerValues.put(Constants.SESSION_ID,mAppPreferences.getJsessionId());

        new ProductApiHelper().getCustomerDetails(headerValues,String.valueOf(customerId), new IApiRequestComplete<CustomerModel>() {
            @Override
            public void onSuccess(final CustomerModel response) {
                if (response!=null){
                    if (response.getData().size()!=0){
                        response.getData().get(0).getAddress();
                        int status = response.getStatus();
                        Log.v("Status", status+"");

                        KeyValue.setString(TransactionHistoryActivityNew.this, KeyValue.NAME,response.getData().get(0).getName());
                        KeyValue.setString(TransactionHistoryActivityNew.this, KeyValue.PHONE,response.getData().get(0).getPhone());
                        KeyValue.setString(TransactionHistoryActivityNew.this, KeyValue.EMAIL,response.getData().get(0).getEmail());
                        KeyValue.setString(TransactionHistoryActivityNew.this, KeyValue.ADDRESS,response.getData().get(0).getAddress());
                        KeyValue.setString(TransactionHistoryActivityNew.this, KeyValue.DOB,"01/03/1990");
                        KeyValue.setString(TransactionHistoryActivityNew.this, KeyValue.NOTE,"Test Note");

                        getBillProducts(invoiceLineIdList);
                        productListCount = invoiceLineIdList.size();
                    }else {
                        Toast.makeText(getApplicationContext(),"Customer details not available",
                                Toast.LENGTH_LONG).show();
                        stopProgress();
                    }
                }
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(getApplicationContext(),"Customer details not available",
                        Toast.LENGTH_LONG).show();
                stopProgress();
            }
        });
    }

    /**
     *Get all products for invoice
     **/
    public void getBillProducts(List<InvoiceLineIdList> invoiceLineIdList){
        billProductsList = new ArrayList<>();
        currentProduct = 0;

        if(invoiceLineIdList.size()>0) {
            getProduct(invoiceLineIdList.get(currentProduct).getId(), invoiceLineIdList.get(currentProduct).getQuantity(),invoiceLineIdList);
        }else{
            Toast.makeText(getApplicationContext(),"Product details not available",
                    Toast.LENGTH_LONG).show();
            stopProgress();
        }
    }

    public void getProduct(int productID, final int quantity, final List<InvoiceLineIdList> invoiceLineIdList){
        HashMap<String,String> headerValues= new HashMap<>();
        headerValues.put("Content-Type", "application/json");
        headerValues.put("Accept", "application/json");
        headerValues.put(Constants.SESSION_ID,mAppPreferences.getJsessionId());

        new ProductApiHelper().getBillProduct(headerValues, productID, new IApiRequestComplete<BillProductInvoice>() {
            @Override
            public void onSuccess(final BillProductInvoice response) {
                if (response!=null && response.getData()!=null && response.getData().size() > 0){
                    billProductsList.add(new BillProduct(response.getData().get(0).getId(),response.getData().get(0).getName(),
                            quantity,response.getData().get(0).getSalePrice(),18, response.getData().get(0).getSalePrice()));

                    //invoiceLineIdList.remove(invoiceLineIdList.get(0));
                    currentProduct++;
                    if(currentProduct<productListCount) {
                        getProduct(invoiceLineIdList.get(currentProduct).getId(), invoiceLineIdList.get(currentProduct).getQuantity(), invoiceLineIdList);
                    }else{
                        stopProgress();
                        Intent intent = new Intent(TransactionHistoryActivityNew.this, BillSummaryActivityNew.class);
                        intent.putExtra("billProductsList", billProductsList);
                        intent.putExtra("discount",0);
                        intent.putExtra("paymentMode","Cash");
                        startActivity(intent);
                    }
                }
            }
            @Override
            public void onFailure(String message) {
                Toast.makeText(getApplicationContext(),"Details not available",
                        Toast.LENGTH_LONG).show();
                stopProgress();
            }
        });
    }
    /*End*/
}

/*TransactionHistoryActivityNew extends BaseActivity {

    List<TranHistoryNew> tranHistoryNewList;
    RecyclerView rvProducts;
    PieChart chart;
    TextView tvTodayDAte;
    Button btnDownloadOrMailHistory;
    View dialogDownloadOrMail;
    EditText etSearchHistory;
    TransactionHistoryAdapterNew adapter;
    boolean isLastProductGet = false;
    int productListCount = 0;
    private ProgressBar progreeBar;
    private AppPreferences mAppPreferences;
    ArrayList<BillProduct> billProductsList = new ArrayList<>();
    public static String filterText = "Today : ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history_new);

        mAppPreferences=AppPreferences.getInstance(this);
        setTitle("Transaction History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        filterText = "Today : ";
        //Initialize Views
        initViews();

        //String myId = UUID.randomUUID().toString();

        getInvoiceList();
        populateList2();
        //showPieChart();
        drawPieGraph();
        filterText = filterText + getSystemDate();
        tvTodayDAte.setText(filterText);

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

    @Override
    protected void onResume() {
        super.onResume();
        tvTodayDAte.setText(filterText);
    }

    *//*Initialize views*//*
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
        progreeBar = findViewById(R.id.progress_bar);
    }

    private void populateList2(){

    }

    *//*Populate invoice list*//*
    private void populateList(final InvoiceModelNew response) {
        InvoiceList iv;
        tranHistoryNewList = new ArrayList<>();
        ArrayList<BillProduct> billProducts = new ArrayList<>();
        for(int i=0;i<response.getData().size();i++){
            iv = response.getData().get(i);
            tranHistoryNewList.add(new TranHistoryNew(iv.getNote(), iv.getInvoiceDate(), iv.getAmountPaid()));
        }
        adapter = new TransactionHistoryAdapterNew(TransactionHistoryActivityNew.this, tranHistoryNewList, new TransactionHistoryAdapterNew.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                isLastProductGet = false;
                int ci = response.getData().get(position).getCustomerId();
                List<InvoiceLineIdList> myList = response.getData().get(position).getInvoiceLineIdList();
                if(ci!=0 && myList!=null) {
                    getCustomerDetails(response.getData().get(position).getCustomerId(),
                            response.getData().get(position).getInvoiceLineIdList());
                }else
                    Toast.makeText(getApplicationContext(),"Details not available",
                            Toast.LENGTH_LONG).show();
            }
        });
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
    *//**
     * Draw Pie chart
     **//*
    public void drawPieGraph(){
        try{
            final PieChart pieChart = findViewById(R.id.chart);
            List<PieEntry> entries = new ArrayList<>();
            entries.add(new PieEntry(52000, " Card  \u20B9 52000"));
            entries.add(new PieEntry(38000, " Cash  \u20B9 38000"));

            pieChart.setEntryLabelColor(Color.RED);
            //PieDataSet set = new PieDataSet(entries, "History Evaluation");
            PieDataSet set = new PieDataSet(entries,"");
            set.setDrawValues(false);
            pieChart.setDrawEntryLabels(false);
            set.setColors(new int[] { R.color.color_ffac2a, R.color.color_33a4a0}, this);
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
            *//**//*
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

    *//**
     * Custom Dialog for Download Options
     **//*
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



       *//* ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDownloadOrMail.setVisibility(View.GONE);
                btnDownloadOrMailHistory.setVisibility(View.VISIBLE);
            }
        });*//*
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

    *//*Download PDF*//*
    public void downloadPDF(){

    }
    *//*Send Mail*//*
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

    *//*Satish code- Get customer details*//*

    //Get invoice list from server
    public void getInvoiceList(){
        showProgress(TransactionHistoryActivityNew.this, "Getting invoices...");
        HashMap<String,String> headerValues= new HashMap<>();
        headerValues.put("Content-Type", "application/json");
        headerValues.put("Accept", "application/json");
        headerValues.put(Constants.SESSION_ID,mAppPreferences.getJsessionId());

        new ProductApiHelper().getInvoiceList(headerValues, new IApiRequestComplete<InvoiceModelNew>() {
            @Override
            public void onSuccess(final InvoiceModelNew response) {
                stopProgress();
                if (response!=null){
                    if (response.getData().size()!=0){
                        populateList(response);
                    }else {
                        //Utility.showToast(getApplicationContext(),getResources().getString(R.string.no_data));
                    }
                }
            }

            @Override
            public void onFailure(String message) {
                stopProgress();
                Utility.showToast(getApplicationContext(),getResources().getString(R.string.no_data));
            }
        });
    }

    //Get customer details from server
    public void getCustomerDetails(int customerId, final List<InvoiceLineIdList> invoiceLineIdList){
        showProgress(TransactionHistoryActivityNew.this, "Fetching details...");
        HashMap<String,String> headerValues= new HashMap<>();
        headerValues.put("Content-Type", "application/json");
        headerValues.put("Accept", "application/json");
        headerValues.put(Constants.SESSION_ID,mAppPreferences.getJsessionId());

        new ProductApiHelper().getCustomerDetails(headerValues,String.valueOf(customerId), new IApiRequestComplete<CustomerModel>() {
            @Override
            public void onSuccess(final CustomerModel response) {
                if (response!=null){
                    if (response.getData().size()!=0){
                        response.getData().get(0).getAddress();
                        int status = response.getStatus();
                        Log.v("Status", status+"");

                        KeyValue.setString(TransactionHistoryActivityNew.this, KeyValue.NAME,response.getData().get(0).getName());
                        KeyValue.setString(TransactionHistoryActivityNew.this, KeyValue.PHONE,response.getData().get(0).getPhone());
                        KeyValue.setString(TransactionHistoryActivityNew.this, KeyValue.EMAIL,response.getData().get(0).getEmail());
                        KeyValue.setString(TransactionHistoryActivityNew.this, KeyValue.ADDRESS,response.getData().get(0).getAddress());
                        KeyValue.setString(TransactionHistoryActivityNew.this, KeyValue.DOB,"01/03/1990");
                        KeyValue.setString(TransactionHistoryActivityNew.this, KeyValue.NOTE,"Test Note");

                        getBillProducts(invoiceLineIdList);
                        productListCount = invoiceLineIdList.size();
                    }else {
                        Toast.makeText(getApplicationContext(),"Details not available",
                                Toast.LENGTH_SHORT).show();
                        stopProgress();
                    }
                }
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(getApplicationContext(),"Details not available",
                        Toast.LENGTH_SHORT).show();
                stopProgress();
            }
        });
    }

    *//**
     *Get all products for invoice
     **//*
    public void getBillProducts(List<InvoiceLineIdList> invoiceLineIdList){
        billProductsList = new ArrayList<>();

        if(invoiceLineIdList.size()>0) {
            getProduct(0, invoiceLineIdList.get(0).getId(), invoiceLineIdList.get(0).getQuantity(),invoiceLineIdList);
        }else{
            Toast.makeText(getApplicationContext(),"Details not available",
                    Toast.LENGTH_SHORT).show();
            stopProgress();
        }
    }

    public void getProduct(final int loopCount, int productID, final int quantity, final List<InvoiceLineIdList> invoiceLineIdList){
        HashMap<String,String> headerValues= new HashMap<>();
        headerValues.put("Content-Type", "application/json");
        headerValues.put("Accept", "application/json");
        headerValues.put(Constants.SESSION_ID,mAppPreferences.getJsessionId());

        new ProductApiHelper().getBillProduct(headerValues, productID, new IApiRequestComplete<BillProductInvoice>() {
            @Override
            public void onSuccess(final BillProductInvoice response) {
                if (response!=null && response.getData()!=null && response.getData().size() > 0){
                    billProductsList.add(new BillProduct(response.getData().get(0).getId(),response.getData().get(0).getName(),
                            quantity,response.getData().get(0).getSalePrice(),18, response.getData().get(0).getSalePrice()));

                    invoiceLineIdList.remove(invoiceLineIdList.get(0));
                    if(invoiceLineIdList.size()>0) {
                        getProduct(0, invoiceLineIdList.get(0).getId(), invoiceLineIdList.get(0).getQuantity(), invoiceLineIdList);
                    }else{
                        stopProgress();
                        Intent intent = new Intent(TransactionHistoryActivityNew.this, BillSummaryActivityNew.class);
                        intent.putExtra("billProductsList", billProductsList);
                        intent.putExtra("discount",0);
                        intent.putExtra("paymentMode","Cash");
                        startActivity(intent);
                    }
                }
            }
            @Override
            public void onFailure(String message) {
                Toast.makeText(getApplicationContext(),"Details not available",
                        Toast.LENGTH_SHORT).show();
                stopProgress();
            }
        });
    }
    *//*End*//*
}
*/