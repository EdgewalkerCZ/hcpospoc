package com.example.vaibhavchahal93788.myapplication.billdesk.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.BillProduct;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.BillSummaryHeaderModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.HeadingBillSummary;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.HeadingPaymentMode;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.PaymentMode;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.SelectedProduct;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.SponceredModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.TotalBillDetail;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.discountModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static com.example.vaibhavchahal93788.myapplication.billdesk.model.discountModel.getInstance;

public class BillSummaryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_ITEM_SHOP_DETAIL = 0;
    public static final int TYPE_ITEM_BILL_PRODUCT = 1;
    public static final int TYPE_ITEM_TOTAL_DETAIL = 2;
    public static final int TYPE_ITEM_HEADING_PAYMENT_MODE = 3;
    public static final int TYPE_ITEM_HEADING_SPONCERED_BY = 4;

    private final List<Object> itemsList;

    private Context context;

    public BillSummaryRecyclerAdapter(List<Object> list) {
        itemsList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == TYPE_ITEM_SHOP_DETAIL) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_bill_summary_item1, parent, false);
            return new ViewHolderItem1(v);
        } else if (viewType == TYPE_ITEM_BILL_PRODUCT) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_summary_bill_item, parent, false);
            return new ViewHolderBillProduct(v);
        } else if (viewType == TYPE_ITEM_TOTAL_DETAIL) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_bill_summary_total_bill, parent, false);
            return new ViewHolderTotalBill(v);
        } else if (viewType == TYPE_ITEM_HEADING_PAYMENT_MODE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_item_heading, parent, false);
            return new ViewHolderHeadingPaymentMode(v);
        } else if (viewType == TYPE_ITEM_HEADING_PAYMENT_MODE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_item_heading, parent, false);
          v.setVisibility(View.GONE);
            return new ViewHolderHeadingPaymentMode(v);
        } else if (viewType == TYPE_ITEM_HEADING_SPONCERED_BY) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_summary_sponcered_heading, parent, false);
            return new ViewHolderHeadingSponceredBy(v);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_ITEM_SHOP_DETAIL:
                ViewHolderItem1 holderSeletedItem = (ViewHolderItem1) holder;
                String uniqueID = UUID.randomUUID().toString();
                holderSeletedItem.tvInVoiceNumber.setText("Invoice No - " + uniqueID.substring(0, 11));
                String currentDate = new SimpleDateFormat("dd MMM, yyyy HH:mm", Locale.getDefault()).format(new Date());
                holderSeletedItem.tvDate.setText("Date -" + currentDate);
                break;
            case TYPE_ITEM_BILL_PRODUCT:
                ViewHolderBillProduct holderBillProduct = (ViewHolderBillProduct) holder;
                BillProduct billProduct = (BillProduct) itemsList.get(position);
                holderBillProduct.tvName.setText(billProduct.getName());
                holderBillProduct.tvBasePrice.setText("" + billProduct.getPrice());
                holderBillProduct.tvGst.setText(billProduct.getGstTax() + "%");
                holderBillProduct.tvQty.setText("" + billProduct.getQuantity());
                holderBillProduct.tvTotalPrice.setText("" + billProduct.getFinalPrice());

                Log.i("CHK Name==>",billProduct.getName()+"");
                Log.i("CHK Price==>",billProduct.getPrice()+"");
                Log.i("CHK GST==>",billProduct.getGstTax()+"");
                Log.i("CHK Quantity==>",billProduct.getQuantity()+"");
                Log.i("CHK Final Price==>",billProduct.getFinalPrice()+"");
                Log.i("CHK Total Price==>",billProduct.getFinalPrice()* billProduct.getQuantity()+"");



                break;
            case TYPE_ITEM_TOTAL_DETAIL:
                ViewHolderTotalBill holderTotalBill = (ViewHolderTotalBill) holder;
                TotalBillDetail totalBillDetail = (TotalBillDetail) itemsList.get(position);
                discountModel discountModelIs=getInstance();
                holderTotalBill.name.setText(totalBillDetail.getTitle());
                if (totalBillDetail.getTitle().equalsIgnoreCase("Cash")) {
                    holderTotalBill.divider.setVisibility(View.GONE);
                    holderTotalBill.name.setTypeface(null, Typeface.NORMAL);
                    holderTotalBill.price.setTypeface(null, Typeface.NORMAL);
                    holderTotalBill.itemView.setPadding(30, 0, 30, 0);
                } else {
                    holderTotalBill.divider.setVisibility(View.VISIBLE);
                    holderTotalBill.name.setTypeface(null, Typeface.BOLD);
                    holderTotalBill.price.setTypeface(null, Typeface.BOLD);
                    holderTotalBill.itemView.setPadding(30, 30, 30, 0);
                }
                holderTotalBill.price.setText(holderTotalBill.name.getContext().getString(R.string.rupee_symbol) + discountModelIs.getDiscountedPrice());
              //Set Discount
               // Log.e("DYY ==>",discountModelIs.getDiscountedPrice()+"");
                holderTotalBill.discount.setText(holderTotalBill.name.getContext().getString(R.string.rupee_symbol) + discountModelIs.getDiscount());
                break;
            case TYPE_ITEM_HEADING_PAYMENT_MODE:
                //CHK
                ViewHolderHeadingPaymentMode holderHeadingPaymentMode = (ViewHolderHeadingPaymentMode) holder;
                holderHeadingPaymentMode.itemView.setPadding(30, 50, 30, 0);
                holderHeadingPaymentMode.heading.setTextSize(16);
                holderHeadingPaymentMode.heading.setTypeface(null, Typeface.BOLD);
                holderHeadingPaymentMode.heading.setTextColor(holderHeadingPaymentMode.heading.getContext().getResources().getColor(R.color.black_color));
                holderHeadingPaymentMode.heading.setText("Payment Summary");
                holderHeadingPaymentMode.heading.setVisibility(View.GONE);
                break;
            case TYPE_ITEM_HEADING_SPONCERED_BY:
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object object = itemsList.get(position);
        if (object instanceof BillSummaryHeaderModel) {
            return TYPE_ITEM_SHOP_DETAIL;
        } else if (object instanceof BillProduct) {
            return TYPE_ITEM_BILL_PRODUCT;
        } else if (object instanceof TotalBillDetail) {
            return TYPE_ITEM_TOTAL_DETAIL;
        } else if (object instanceof HeadingPaymentMode) {
            return TYPE_ITEM_HEADING_PAYMENT_MODE;
        } else if (object instanceof SponceredModel) {
            return TYPE_ITEM_HEADING_SPONCERED_BY;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }


    public class ViewHolderItem1 extends RecyclerView.ViewHolder {

        public TextView tvDate, tvInVoiceNumber;

        public ViewHolderItem1(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            tvInVoiceNumber = (TextView) itemView.findViewById(R.id.tv_invoice_number);
        }
    }


    public class ViewHolderHeadingBillSummary extends RecyclerView.ViewHolder {
        public TextView heading;

        public ViewHolderHeadingBillSummary(View itemView) {
            super(itemView);
            heading = (TextView) itemView.findViewById(R.id.tv_heading);
        }
    }

    public class ViewHolderHeadingPaymentMode extends RecyclerView.ViewHolder {
        public TextView heading;

        public ViewHolderHeadingPaymentMode(View itemView) {
            super(itemView);
            heading = (TextView) itemView.findViewById(R.id.tv_heading);
        }
    }

    public class ViewHolderHeadingSponceredBy extends RecyclerView.ViewHolder {
        public TextView heading;

        public ViewHolderHeadingSponceredBy(View itemView) {
            super(itemView);
            heading = (TextView) itemView.findViewById(R.id.tv_heading);
        }
    }


    public class ViewHolderBillProduct extends RecyclerView.ViewHolder {

        public TextView tvName, tvTotalPrice, tvQty, tvBasePrice, tvGst;

        public ViewHolderBillProduct(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_item_name);
            tvTotalPrice = (TextView) itemView.findViewById(R.id.tv_item_total);
            tvQty = (TextView) itemView.findViewById(R.id.tv_item_qty);
            tvBasePrice = (TextView) itemView.findViewById(R.id.tv_item_price);
            tvGst = (TextView) itemView.findViewById(R.id.tv_item_gst);

        }
    }

    public class ViewHolderTotalBill extends RecyclerView.ViewHolder {

        public TextView name, price,discount;
        public View divider;

        public ViewHolderTotalBill(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.product_name);
            price = (TextView) itemView.findViewById(R.id.product_price);
            divider = (View) itemView.findViewById(R.id.upper_divider);
            discount=(TextView)itemView.findViewById(R.id.discount_id);

        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}

