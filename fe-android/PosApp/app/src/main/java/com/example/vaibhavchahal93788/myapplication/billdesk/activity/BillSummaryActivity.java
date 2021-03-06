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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.BillSummaryRecyclerAdapter;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.BillProduct;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.BillSummaryHeaderModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.DiscountModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.HeadingPaymentMode;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.SponceredModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.TotalBillDetail;
import com.example.vaibhavchahal93788.myapplication.billdesk.printing.DeviceListActivity;
import com.example.vaibhavchahal93788.myapplication.billdesk.printing.PrinterCommands;
import com.example.vaibhavchahal93788.myapplication.billdesk.printing.Utils;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.KeyValue;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;


public class BillSummaryActivity extends AppCompatActivity implements Runnable{

    private RecyclerView recyclerView;
    private ArrayList<BillProduct> billProductsList;
    private ArrayList<Object> list;
    private BillSummaryRecyclerAdapter adapter;
    private Button btnPrint;
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    private OutputStream os;
    byte FONT_TYPE;
    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;

    private EditText message;
    private boolean isBluetoothConnected = false;
    BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 2;

    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ArrayList<ProductListModel> selectedItemList;
    int billDiscount;
    String paymentMode;
    private String userPhone,userName,userEmail;
    private DiscountModel discountModelIs= DiscountModel.getInstance();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_summary);
        setTitle("Bill Summary");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        populateList();
        initViews();

        //Get preference values of user
        userPhone = KeyValue.getString(this,KeyValue.PHONE);
        userName = KeyValue.getString(this,KeyValue.NAME);
        userEmail = KeyValue.getString(this,KeyValue.EMAIL);
    }

    private void populateList() {
        billProductsList = getIntent().getParcelableArrayListExtra("billProductsList");


        Bundle extras = getIntent().getExtras();
        //Log.e("DY extras==>",extras+"");
        if(extras == null) {
            billDiscount= 0;
        } else {
            billDiscount= extras.getInt("discount");
            paymentMode = extras.getString("paymentMode");
        }


        list = new ArrayList<>();

        list.add(new BillSummaryHeaderModel());

        for (BillProduct listModel : billProductsList) {
            list.add(listModel);
        }

        int totalPrice = 0;
        for (int i = 0; i < billProductsList.size(); i++) {
            BillProduct billProduct = ((BillProduct) billProductsList.get(i));
//            int priceAfterGst = billProduct.getFinalPrice() * billProduct.getQuantity();
//            totalPrice = totalPrice + priceAfterGst;
            totalPrice = billProduct.getFinalPrice();



        }

        TotalBillDetail totalBillDetail = new TotalBillDetail("Net Amount", totalPrice);
        list.add(totalBillDetail);

        //list.add(new HeadingPaymentMode("Payment Mode"));
//        TotalBillDetail paymentSummary = new TotalBillDetail(paymentMode, 0);
//        list.add(paymentSummary);

        //list.add(new SponceredModel());

    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BillSummaryRecyclerAdapter(list,paymentMode);

        recyclerView.setAdapter(adapter);

        btnPrint=findViewById(R.id.btn_print_bill);
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectPrinter();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_home) {
            Intent intent = new Intent(BillSummaryActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Function for print
     */

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
        String uniqueID = UUID.randomUUID().toString();
        String date_n = new SimpleDateFormat("dd MMM, yyyy HH:mm", Locale.getDefault()).format(new Date());

        Log.e("=userName=>",userName+"==>"+userPhone+"=="+userEmail);
        //print normal text
       // printNewLine();
        printPhoto(R.drawable.alphanew);
        printCustom("Alpha Store", 1, 1);
        printNewLine();
        printCustom("Dlf Phase 3,Gurgaon - 122002", 0, 1);
        printNewLine();
        printCustom("Billing To", 0, 0);
        printNewLine();
//        printCustom(userName, 0, 0);
//        printCustom("Invoice No:"+uniqueID.substring(0, 11), 0, 1);
//        printNewLine();
        printTextNormal(userName+"        Invoice No:"+uniqueID.substring(0, 11));
        makTextNormal();
        printNewLine();
        printTextNormal(userPhone+"        "+date_n);
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
        printTextNormal("Item Name  Gst%   Price  Qty  Total");
        makTextNormal();
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
        printNewLine();

        int netAmount = discountModelIs.getDiscountedPrice();
        printCustom("--------------------------------", 1, 0);
        makTextNormal();
        printTextNormal("Net Amount  :   " + "Rs" + netAmount);
        printNewLine();
        printNewLine();

        printTextNormal("Payment Mode  :   " + paymentMode);
        printNewLine();
        printNewLine();
        printCustom("--------------------------------", 1, 0);
        makTextNormal();
        //resetPrint(); //reset printer
        printNewLine();
        printNewLine();
       // printCustom("  Powered by. Home Credit India.   1800 121 6660", 1, 1);
        printCustom("      Powered by   ", 1, 1);
        printNewLine();
        printCustom("      HOMECREDIT INDIA   ", 1, 1);
        printNewLine();
        printNewLine();
    }

    private void connectPrinter() {
        Log.e("connect printer",":sddfd");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(BillSummaryActivity.this, "Message1", Toast.LENGTH_SHORT).show();
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
                Log.e("initPrinting",":init");
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
            Intent connectIntent = new Intent(BillSummaryActivity.this,
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
                    Toast.makeText(BillSummaryActivity.this, "Message", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(BillSummaryActivity.this, "DeviceConnected", Toast.LENGTH_SHORT).show();
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
    private void makTextNormal() {
        byte[] cc = new byte[]{0x1B, 0x21, 0x03};  // 0- normal size text
        try {
            os.write(cc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getIndividualBill() {

        //Daya
        // int startIndex = (selectedItemList.size() + 1);
        // int endIndex = (selectedItemList.size() * 2) + 1;

        int maxLengthFinalPrice = 4;//getFinalPriceMaxLength(startIndex, endIndex);
        int maxLengthBasePrice = 4; //getBasePriceMaxLength(startIndex, endIndex);
        int maxLengthQty = 4;//getQtyMaxLength(startIndex, endIndex);

        int totalPrice = 0;
        Log.e("=billProductsList==>",billProductsList.size()+"");

        for (int i = 0; i < billProductsList.size(); i++) {
            BillProduct billProduct = ((BillProduct) billProductsList.get(i));

            Log.e("=Summery totalPrice==>",totalPrice+"");
            Log.e("=Summery Price==>",billProduct.getPrice()+"");
            Log.e("=Summery Quantity==>",billProduct.getQuantity()+"");

            int priceAfterGst = billProduct.getPrice() * billProduct.getQuantity();
            printTextNormal(billProduct.getName() + " : " + billProduct.getGstTax() + "%" + "    " + spacingRequired(maxLengthBasePrice, billProduct.getPrice()) + billProduct.getPrice() + "    " + spacingRequired(maxLengthQty, billProduct.getQuantity()) + billProduct.getQuantity() + "   " + spacingRequired(maxLengthFinalPrice, priceAfterGst) + (priceAfterGst));

            printNewLine();
            totalPrice = totalPrice + priceAfterGst;
            Log.e("=Summery total N==>",totalPrice+"");

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

}
