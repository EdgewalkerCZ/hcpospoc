package com.example.vaibhavchahal93788.myapplication.billdesk.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.BillDetailRecyclerAdapter;
import com.example.vaibhavchahal93788.myapplication.billdesk.api.ProductApiHelper;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.BillProduct;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.DiscountModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.InvoiceIdModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.LoginBodyModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.PaymentMode;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.SaveHistorySuccessModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.SaveInvoiceModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.SelectedProduct;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.TotalBillDetail;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.userlogin.LoginSuccessResponse;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.IApiRequestComplete;
import com.example.vaibhavchahal93788.myapplication.billdesk.preferences.AppPreferences;
import com.example.vaibhavchahal93788.myapplication.billdesk.printing.DeviceListActivity;
import com.example.vaibhavchahal93788.myapplication.billdesk.printing.PrinterCommands;
import com.example.vaibhavchahal93788.myapplication.billdesk.printing.Utils;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Constants;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.KeyValue;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Utility;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import static com.example.vaibhavchahal93788.myapplication.billdesk.model.DiscountModel.getInstance;
import static com.example.vaibhavchahal93788.myapplication.billdesk.preferences.AppPreferences.mAppPreferences;


public class BillDetailActivity extends AppCompatActivity implements BillDetailRecyclerAdapter.OnDataChangeListener, View.OnClickListener, Runnable {
    private List<Object> list = new ArrayList();
    private RecyclerView recyclerView;
    private BillDetailRecyclerAdapter adapter;
    private ArrayList<ProductListModel> selectedItemList;
    private Button btnConnectPrinter, btnPrint, btnViewBill, btnEmail;
    private TextView textBillingPrice;
    private String mSessionId;

    private DiscountModel discountModelIs = DiscountModel.getInstance();


    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    Button mScan, mPrint, mDisc;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    private OutputStream os;
    byte FONT_TYPE;
    private EditText message;
    private boolean isBluetoothConnected = false;
    private int totalItems;
    private int totalPrice;
    private float totalPriceIs;
    private String seletedPaymentMode;
    private String userPhone, userName, userEmail;
    private AppPreferences mAppPreferences;
    String uniqueID;
    //int totalItems = 0, totalPrice = 0;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail);

        setTitle("Payment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        populateList();
        initViews();
        //Get preference values of user
        userPhone = KeyValue.getString(this, KeyValue.PHONE);
        userName = KeyValue.getString(this, KeyValue.NAME);
        userEmail = KeyValue.getString(this, KeyValue.EMAIL);

//Get Session Id
        mAppPreferences = AppPreferences.getInstance(this);
        mSessionId = mAppPreferences.getJsessionId();

    }

    private void populateList() {
        selectedItemList = (ArrayList<ProductListModel>) getIntent().getSerializableExtra("selectedItemList");
        totalItems = getIntent().getIntExtra("totalItems", 1);
        totalPriceIs = getIntent().getFloatExtra("totalPrice", 0);
        totalPrice = Math.round(totalPriceIs);

        list = new ArrayList<>();

        for (ProductListModel listModel : selectedItemList) {
            SelectedProduct selectedProduct = new SelectedProduct(listModel.getLabel(), listModel.getQuantity(), Math.round(Float.valueOf(listModel.getPrice())), Math.round(Float.valueOf(listModel.getFinalPrice())));
            list.add(selectedProduct);
        }
        list.add(new PaymentMode());
        //  list.add(new HeadingBillSummary("Bill Summary"));

        for (ProductListModel listModel : selectedItemList) {
            BillProduct billProduct = new BillProduct(Integer.parseInt(listModel.getId()),listModel.getLabel(), listModel.getQuantity(), Math.round(Float.valueOf(listModel.getPrice())), Math.round(Float.valueOf(listModel.getTaxPercentage())), Math.round(Float.valueOf(listModel.getFinalPrice())));
            list.add(billProduct);
        }

        TotalBillDetail totalBillDetail = new TotalBillDetail("Total Amount" + " (" + totalItems + " items)", totalPrice);
        list.add(totalBillDetail);

        //list.add(new HeadingPaymentMode("Payment Mode"));
        uniqueID = UUID.randomUUID().toString();
        uniqueID = uniqueID.substring(0, 11);


    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BillDetailRecyclerAdapter(list, this);

        recyclerView.setAdapter(adapter);
        recyclerView.setItemViewCacheSize(list.size());

        btnConnectPrinter = findViewById(R.id.btn_find_printer);
        btnConnectPrinter.setOnClickListener(this);

        btnViewBill = findViewById(R.id.btn_view_bill);
        btnViewBill.setOnClickListener(this);

        btnPrint = findViewById(R.id.btn_print_bill);
        btnPrint.setOnClickListener(this);

        btnEmail = findViewById(R.id.btn_email);
        btnEmail.setOnClickListener(this);

        textBillingPrice = findViewById(R.id.tv_billing_est_price);
        //Set Total Price and Item

        if (totalItems != 0 && totalPrice != 0) {
            textBillingPrice.setText(String.format(getString(R.string.text_billing_estimated_price), totalItems, totalPrice));

            // discountModelIs = getInstance();
            discountModelIs.setFinalPrice(totalPrice);
            discountModelIs.setQuantity(totalItems);
            discountModelIs.setDiscountedPrice(totalPrice);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        View view = menu.findItem(R.id.action_home).getActionView();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                Intent intent = new Intent(BillDetailActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home) {
//            Intent intent = new Intent(BillDetailActivity.this, HomeActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDataChanged(int quantity, int position, int price) {
        totalItems = 0;
        totalPrice = 0;
        int index = selectedItemList.size() + 1 + position;
        BillProduct billProduct = (BillProduct) list.get(index);
        billProduct.setQuantity(quantity);
        if (price >= 0) {
            billProduct.setFinalPrice(price);
        }

        int totalBillIndex = (selectedItemList.size() * 2) + 1;

        TotalBillDetail totalBillDetail = (TotalBillDetail) list.get(totalBillIndex);

        int startIndex = (selectedItemList.size() + 1);
        int endIndex = (selectedItemList.size() * 2) + 1;


        for (int i = startIndex; i < endIndex; i++) {
            totalItems = totalItems + ((BillProduct) list.get(i)).getQuantity();
            // totalPrice = totalPrice + ((BillProduct) list.get(i)).getFinalPrice() * ((BillProduct) list.get(i)).getQuantity();
            totalPrice = totalPrice + ((BillProduct) list.get(i)).getFinalPrice();
        }
        totalBillDetail.setTitle("Total Amount" + " (" + totalItems + " items)");

        discountModelIs = DiscountModel.getInstance();
        discountModelIs.setFinalPrice(totalPrice);
        discountModelIs.setQuantity(totalItems);
        totalBillDetail.setTotalPrice(totalPrice);

        textBillingPrice.setText(String.format(getString(R.string.text_billing_estimated_price), totalItems, totalPrice));


        adapter.notifyItemChanged(index);
        adapter.notifyItemChanged(totalBillIndex);
    }

    @Override
    public void onDiscount(int discount) {
        int updatedPrice = 0;
        int totalItems = 0;
        discountModelIs = DiscountModel.getInstance();

        if (discountModelIs.getFinalPrice() > 0) {
            if(discountModelIs.getFinalPrice() < discount){
                Toast.makeText(BillDetailActivity.this, "Discount can't be more than total price.", Toast.LENGTH_SHORT).show();
            }else{
                updatedPrice = discountModelIs.getFinalPrice() - discount;
                totalItems = discountModelIs.getQuantity();
                textBillingPrice.setText(String.format(getString(R.string.text_billing_estimated_price), totalItems, updatedPrice));
                discountModelIs.setDiscountedPrice(updatedPrice);
            }

        }
        //
    }


    @Override
    public void seletedPaymentMode(String mode) {
        if (!mode.equals(""))
            seletedPaymentMode = mode;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_view_bill:
                if(discountModelIs.getFinalPrice() < discountModelIs.getDiscount()){
                    Toast.makeText(BillDetailActivity.this, "Discount can't be more than total price.", Toast.LENGTH_SHORT).show();
                }else{
                    discountModelIs = DiscountModel.getInstance();
                    Intent intent = new Intent(BillDetailActivity.this, BillSummaryActivity.class);
                    ArrayList<BillProduct> billProducts = getBillProductsList();
                    intent.putParcelableArrayListExtra("billProductsList", billProducts);

                    intent.putExtra("discount", discountModelIs.getDiscount());
                    intent.putExtra("paymentMode", seletedPaymentMode);
                    intent.putExtra("uniqueID", uniqueID);
                    startActivity(intent);
                    //Save bill history

                    saveBill();
                }

                break;

            case R.id.btn_print_bill:
                if(discountModelIs.getFinalPrice() < discountModelIs.getDiscount()){
                    Toast.makeText(BillDetailActivity.this, "Discount can't be more than total price.", Toast.LENGTH_SHORT).show();
                }else{
                    connectPrinter();
                    saveBill();
                }
                break;
            case R.id.btn_email:
                //Send Email
                sendEmail();
            default:
                break;
        }
    }


    protected void sendEmail() {

        String TO = "xyz@gmail.com";

        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Invoice");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Invoice Is");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            // finish();
            Log.i("Finished sending email.", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    private ArrayList<BillProduct> getBillProductsList() {
        ArrayList<BillProduct> billProducts = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof BillProduct) {
                billProducts.add((BillProduct) list.get(i));
            }
        }
        return billProducts;
    }

    private void printBill() {
        Thread t = new Thread() {
            public void run() {
                try {
                    os = mBluetoothSocket
                            .getOutputStream();
                    OutputStream opstream = null;
                    try {
                        opstream = mBluetoothSocket.getOutputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    os = opstream;

                    //print command
                    try {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        os = mBluetoothSocket.getOutputStream();

                        byte[] printformat = {0x1B, 0 * 21, FONT_TYPE};
                        //outputStream.write(printformat);

                        //print title
                        printContent();
                        printNewLine();

                        os.flush();
                        Intent intent = new Intent(BillDetailActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    Log.e("MainActivity", "Exe ", e);
                }
            }
        };
        t.start();
    }


    private void printContent() {

        //Add Space
        StringBuilder sb =new StringBuilder();
        String date_n = new SimpleDateFormat("dd MMM, yyyy HH:mm", Locale.getDefault()).format(new Date());
        Log.e("HC len====>",userName.length()+"");
//        if(!userName.equals(""))
//        {
//            if(userName.length()<12)
//            {
//                int len = 8-userName.length();
//                Log.e("HC len 2====>",len+"");
//                sb.append(userName);
//                for(int i=0;i<1;i++)
//                {
//                    sb.append(" : ");
//                    Log.e("HC Add space====>",sb.toString());
//                }
//
//
//
//            }else{
//                sb.append(userName);
//            }
//        }
        sb.append(userName);
        sb.append(" : ");

    Log.e("HC New Name====>",sb.toString());
        printPhoto(R.drawable.alpha_print);
        //print normal text
        // printNewLine();
        printNewLine();
        printCustom("Alpha Store", 1, 1);
        printNewLine();
        printCustom("Dlf Phase 3,Gurgaon - 122002", 0, 1);
        printNewLine();
        printCustom("Billing To", 0, 0);
        printNewLine();
       // printTextNormal(userName + "               Invoice No:" + uniqueID);
        printTextNormal(sb + " Invoice No:" + uniqueID);
        makTextNormal();
        printNewLine();
        printTextNormal(userPhone + "          " + date_n);
        makTextNormal();
        printNewLine();

        printCustom(userEmail, 0, 0);
        printNewLine();

        printNewLine();
        printCustom("--------------------------------", 1, 0);
        makTextNormal();

        printCustom("Invoice", 1, 1);
        printNewLine();
        printNewLine();
        printTextNormal("ItemName  Gst% Price  Qty  Total");
        //makTextNormal();
        printNewLine();
        printCustom("--------------------------------", 1, 0);
        makTextNormal();
        printNewLine();

        int totalPrice = getIndividualBill();
        printCustom("--------------------------------", 1, 0);
        makTextNormal();
        printTextNormal("Total  : " + "Rs " + totalPrice);
        printNewLine();
        printNewLine();

        int discount = discountModelIs.getDiscount();
        printCustom("--------------------------------", 1, 0);
        makTextNormal();
        printTextNormal("Discount  :   " + "Rs " + discount);
        printNewLine();
      //  printTextNormal("12345678901234567890123456789012345678901234");
        printNewLine();

        int netAmount = discountModelIs.getDiscountedPrice();
        printCustom("--------------------------------", 1, 0);
        makTextNormal();
        printTextNormal("Net Amount  :   " + "Rs" + netAmount);
        printNewLine();
        printNewLine();

        printTextNormal("Payment Mode  :   " + seletedPaymentMode);
        printNewLine();
        printNewLine();
        printCustom("--------------------------------", 1, 0);
        makTextNormal();
        //resetPrint(); //reset printer
        printNewLine();
        printNewLine();
//        printCustom("  Powered by. Home Credit India.   1800 121 6660", 1, 1);
//        printNewLine();
        printCustom("      Powered by   ", 1, 1);
        printNewLine();
        printCustom("      HOME CREDIT INDIA   ", 1, 1);
        printNewLine();
        printNewLine();
    }


//    private void printContent() {
//        //print normal text
//        printCustom("Invoice 3 Inch", 0, 0);
//        printNewLine();
//        printNewLine();
//        printCustom("Home credit Store", 1, 1);
//        printNewLine();
//        printCustom("10A, Dlf Phase 2,", 0, 1);
//        printNewLine();
//        printCustom("Gurgaon(Haryana) - 122001", 0, 1);
//        printNewLine();
//        printCustom("Email - care@homecredit.co.in", 0, 1);
//        printNewLine();
//        printCustom("Supply of Goods", 1, 1);
//        printNewLine();
//        printNewLine();
//
//        String uniqueID = UUID.randomUUID().toString();
//
//        printCustom("Invoice No - " + uniqueID.substring(0, 11), 0, 0);
//        printNewLine();
//        String date_n = new SimpleDateFormat("dd MMM, yyyy HH:mm", Locale.getDefault()).format(new Date());
//
//        printCustom("Date -" + date_n, 0, 0);
//        printNewLine();
//        printCustom("--------------------------------", 1, 0);
//        makTextNormal();
//        printTextNormal("Item Name : Gst   Price  Qty  Total");
//        makTextNormal();
//        printNewLine();
//        printCustom("--------------------------------", 1, 0);
//        makTextNormal();
//        printNewLine();
//        int totalPrice = getIndividualBill();
//        printCustom("--------------------------------", 1, 0);
//        makTextNormal();
//        printTextNormal("Net Amount : " + "Rs " + totalPrice);
//        printNewLine();
//        printNewLine();
//        printTextNormal("Payment Summary");
//        printNewLine();
//        printTextNormal("Cash : " + "Rs " + totalPrice + ".00");
//
//        //resetPrint(); //reset printer
//        printNewLine();
//        printNewLine();
//        printCustom("  Powered by Home Credit India.   1800 121 6660", 1, 1);
//        printNewLine();
//        printNewLine();
//    }

    private void makTextNormal() {
        byte[] cc = new byte[]{0x1B, 0x21, 0x03};  // 0- normal size text
        try {
            os.write(cc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getDiscount() {
        int discount = discountModelIs.getDiscount();
        return discount;
    }

    private int getIndividualBill() {

        int startIndex = (selectedItemList.size() + 1);
        int endIndex = (selectedItemList.size() * 2) + 1;

        int maxLengthFinalPrice = getFinalPriceMaxLength(startIndex, endIndex);
        int maxLengthBasePrice = getBasePriceMaxLength(startIndex, endIndex);
        int maxLengthQty = getQtyMaxLength(startIndex, endIndex);

        int totalPrice = 0;
        String productName = "";
        for (int i = startIndex; i < endIndex; i++) {

            BillProduct billProduct = ((BillProduct) list.get(i));
            // int priceAfterGst = billProduct.getFinalPrice() * billProduct.getQuantity();
            //
            int priceAfterGst = billProduct.getPrice() * billProduct.getQuantity();
            if (billProduct.getName().length() > 8) {
                productName = billProduct.getName().substring(0, 8);
                //productName = productName + "...";
            } else {
                productName = billProduct.getName();
            }
            Log.e("productName==>",productName);
            printTextNormal(productName + " : " + billProduct.getGstTax() + "%" + "    " + spacingRequired(maxLengthBasePrice, billProduct.getPrice()) + billProduct.getPrice() + "    " + spacingRequired(maxLengthQty, billProduct.getQuantity()) + billProduct.getQuantity() + "   " + spacingRequired(maxLengthFinalPrice, priceAfterGst) + (priceAfterGst));

            printNewLine();
            totalPrice = totalPrice + priceAfterGst;
        }
        return totalPrice;
    }

    private int getFinalPriceMaxLength(int startIndex, int endIndex) {
        int maxLength = 0;
        for (int i = startIndex; i < endIndex; i++) {
            BillProduct billProduct = ((BillProduct) list.get(i));
            int currentLength = String.valueOf(billProduct.getFinalPrice() * billProduct.getQuantity()).length();
            if (currentLength > maxLength) {
                maxLength = currentLength;
            }
        }
        return maxLength;
    }

    private int getBasePriceMaxLength(int startIndex, int endIndex) {
        int maxLength = 0;
        for (int i = startIndex; i < endIndex; i++) {
            BillProduct billProduct = ((BillProduct) list.get(i));
            int currentLength = String.valueOf(billProduct.getPrice()).length();
            if (currentLength > maxLength) {
                maxLength = currentLength;
            }
        }
        return maxLength;
    }

    private int getQtyMaxLength(int startIndex, int endIndex) {
        int maxLength = 0;
        for (int i = startIndex; i < endIndex; i++) {
            BillProduct billProduct = ((BillProduct) list.get(i));
            int currentLength = String.valueOf(billProduct.getQuantity()).length();
            if (currentLength > maxLength) {
                maxLength = currentLength;
            }
        }
        return maxLength;
    }

    private String spacingRequired(int maxLength, int priceAfterGst) {
        int spacingReq = maxLength - String.valueOf(priceAfterGst).length();
        String totalSpacing = "";
        for (int i = 0; i < spacingReq; i++) {
            totalSpacing = " " + totalSpacing;
        }
        return totalSpacing;
    }

    private void connectPrinter() {
        Log.e("connect printer", ":sddfd");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(BillDetailActivity.this, "Message1", Toast.LENGTH_SHORT).show();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                isBluetoothConnected = false;
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent,
                        REQUEST_ENABLE_BT);
            } else {
                if (!isBluetoothConnected)
                    connectBluetooth();
                Log.e("initPrinting", ":init");
                initPrinting();
                //               ListPairedDevices();
//                Intent connectIntent = new Intent(BillDetailActivity.this,
//                        DeviceListActivity.class);
//                startActivityForResult(connectIntent,
//                        REQUEST_CONNECT_DEVICE);
            }
        }
    }

    private void initPrinting() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                printBill();
            }
        }, 1000);
    }

    private void connectBluetooth() {
        if (getFirstConnectedDevice().isEmpty()) {
            Toast.makeText(this, "No device paired, please paired a device!", Toast.LENGTH_LONG).show();
            Intent connectIntent = new Intent(BillDetailActivity.this,
                    DeviceListActivity.class);
            startActivityForResult(connectIntent,
                    REQUEST_CONNECT_DEVICE);
        } else {
            mBluetoothDevice = mBluetoothAdapter
                    .getRemoteDevice(getFirstConnectedDevice());
            mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                    "Connecting...", mBluetoothDevice.getName() + " : "
                            + mBluetoothDevice.getAddress(), true, true);


            mBluetoothConnectProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mBluetoothAdapter.cancelDiscovery();
                    isBluetoothConnected = false;
                }
            });

            Thread mBlutoothConnectThread = new Thread(this);
            mBlutoothConnectThread.start();
            isBluetoothConnected = true;
        }
    }

    private String getFirstConnectedDevice() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        String deviceADD = "";
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
                deviceADD = mDevice.getAddress();
                break;
            }
        }
        return deviceADD;
    }

    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

        switch (mRequestCode) {
            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    if (!isBluetoothConnected)
                        connectBluetooth();
                    initPrinting();
                } else {
                    Toast.makeText(BillDetailActivity.this, "Message", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
            return;
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
    }


    @Override
    public void onBackPressed() {
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(BillDetailActivity.this, "DeviceConnected", Toast.LENGTH_SHORT).show();

        }
    };


    //print new line
    private void printNewLine() {
        try {
            os.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    void printTextNormal(String msg) {
        try {
            os.write(PrinterCommands.ESC_ALIGN_LEFT);
            String space = "   ";
            int l = msg.length();
            if (l < 41) {
                for (int x = 41 - l; x >= 0; x--) {
                    space = space + " ";
                }
            }
            msg = msg.replace(" : ", space);
            os.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //print byte[]
    private void printText(byte[] msg) {
        try {
            // Print normal text
            os.write(msg);
            printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //print custom
    private void printCustom(String msg, int size, int align) {
        //Print config "mode"
        byte[] cc = new byte[]{0x1B, 0x21, 0x03};  // 0- normal size text
        byte[] cc1 = new byte[]{0x1B, 0x21, 0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B, 0x21, 0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B, 0x21, 0x20}; // 2- bold with medium text
        byte[] bb4 = new byte[]{0x1B, 0x21, 0x12}; // 2- bold with medium text
        byte[] bb5 = new byte[]{0x1B, 0x21, 0x14}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B, 0x21, 0x10}; // 3- bold with large text
        try {
            switch (size) {
                case 0:
                    os.write(cc);
                    break;
                case 1:
                    os.write(bb);
                    break;
                case 2:
                    os.write(bb2);
                    break;
                case 3:
                    os.write(bb3);
                    break;
                case 4:
                    os.write(bb4);
                    break;
                case 5:
                    os.write(bb5);
                    break;
            }

            switch (align) {
                case 0:
                    //left align
                    os.write(PrinterCommands.ESC_ALIGN_LEFT);
                    break;
                case 1:
                    //center align
                    os.write(PrinterCommands.ESC_ALIGN_CENTER);
                    break;
                case 2:
                    //right align
                    os.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    break;
            }
            os.write(msg.getBytes());
            os.write(PrinterCommands.HT);
            //outputStream.write(cc);
            //printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //print photo
    public void printPhoto(int img) {
        try {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                    img);
            if (bmp != null) {
                byte[] command = Utils.decodeBitmap(bmp);
                Log.e("======>command", command + "");
                os.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText(command);
            } else {
                Log.e("Print Photo error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
    }

    //print photo
    public void printPhoto1(int img) {
        try {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                    img);
            if (bmp != null) {
                byte[] command = Utils.decodeBitmap(bmp);
                Log.e("======>command", command + "");
                os.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText(command);
            } else {
                Log.e("Print Photo error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
    }


    private void saveBill() {
        int customerId = 30;

        AppPreferences mAppPreferences = AppPreferences.getInstance(this);

        HashMap<String, String> headerValues = new HashMap<>();
        headerValues.put("Content-Type", "application/json");
        headerValues.put("Accept", "application/json");
        headerValues.put(Constants.SESSION_ID, mAppPreferences.getJsessionId());

        String productName = "";
        //String uniqueID = uniqueID = UUID.randomUUID().toString();


        String customerIdIs = KeyValue.getString(this, KeyValue.CUSTOMER_ID);
        if (!customerIdIs.equals("")) {
            customerId = Integer.parseInt(customerIdIs);
        }
        List<InvoiceIdModel> invoiceIs = new ArrayList<InvoiceIdModel>();
        //check
        ArrayList<BillProduct> billProducts = getBillProductsList();


        //foR Product Name

        if (billProducts.size() > 0 && billProducts.size()!=1) {
            int len = billProducts.size() - 1;
            productName = billProducts.get(0).getName() + " + " + len + " items";
            for (int i = 0; i < billProducts.size(); i++) {
                InvoiceIdModel mod = new InvoiceIdModel();
                mod.setId(billProducts.get(i).getId());
                mod.setQuantity(billProducts.get(i).getQuantity());
                invoiceIs.add(mod);
            }
        }else{
            productName = billProducts.get(0).getName();
        }

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String date_n = currentDate.toString();

        SaveInvoiceModel invoiceMode = new SaveInvoiceModel();
        invoiceMode.setInvoiceId(uniqueID);
        invoiceMode.setInvoiceDate(date_n);
        invoiceMode.setDueDate(date_n);
        invoiceMode.setCompanyId(1);
        invoiceMode.setCustomerId(customerId);
        invoiceMode.setPaymentModeId(15);
        invoiceMode.setInvoiceLineIdList(invoiceIs);
        invoiceMode.setCurrencyId(148);
        invoiceMode.setCompanyExTaxTotal(0);
        invoiceMode.setCompanyTaxTotal(0);
        invoiceMode.setAmountRemaining(0);
        invoiceMode.setAmountPaid(totalPrice);
        invoiceMode.setCompanyInTaxTotalRemaining(0);
        invoiceMode.setAmountRejected(0);
        invoiceMode.setExTaxTotal(3782);
        invoiceMode.setDirectDebitAmount(0);
        invoiceMode.setNote(productName);
        invoiceMode.setPaymentConditionId(5);

        new ProductApiHelper().saveHistory(headerValues, invoiceMode, new IApiRequestComplete<SaveHistorySuccessModel>() {
            @Override
            public void onSuccess(SaveHistorySuccessModel response) {
                if (response != null) {
                    Log.e("DY Success", "===" + response);

                }
            }

            @Override
            public void onFailure(String message) {
                Log.e("DY Failure", "===");
            }
        });


    }

}
