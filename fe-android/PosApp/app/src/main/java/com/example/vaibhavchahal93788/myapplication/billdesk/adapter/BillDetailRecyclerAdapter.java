package com.example.vaibhavchahal93788.myapplication.billdesk.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.BillProduct;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.HeadingBillSummary;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.HeadingPaymentMode;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.PaymentMode;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.SelectedProduct;

import java.util.List;

public class BillDetailRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_ITEM_SELECTED = 0;
    public static final int TYPE_ITEM_HEADING_BILL_SUMMARY = 1;
    public static final int TYPE_ITEM_HEADING_PAYMENT_MODE = 2;
    public static final int TYPE_ITEM_BILL_PRODUCT = 3;
    public static final int TYPE_ITEM_PAYMENT_MODE = 4;

    private final List<Object> itemsList;

    private Context context;

    public BillDetailRecyclerAdapter(List<Object> list) {
        itemsList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == TYPE_ITEM_SELECTED) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item, parent, false);
            return new ViewHolderSeletedItem(v);
        } else if (viewType == TYPE_ITEM_HEADING_BILL_SUMMARY) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_item_heading, parent, false);
            return new ViewHolderHeadingBillSummary(v);
        } else if (viewType == TYPE_ITEM_HEADING_PAYMENT_MODE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_item_heading, parent, false);
            return new ViewHolderHeadingPaymentMode(v);
        } else if (viewType == TYPE_ITEM_BILL_PRODUCT) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_bill_item, parent, false);
            return new ViewHolderBillProduct(v);
        } else if (viewType == TYPE_ITEM_PAYMENT_MODE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_mode_item, parent, false);
            return new ViewHolderPaymentMode(v);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_ITEM_SELECTED:
                ViewHolderSeletedItem holderSeletedItem = (ViewHolderSeletedItem) holder;
                SelectedProduct selectedProduct = (SelectedProduct) itemsList.get(position);
                holderSeletedItem.name.setText(selectedProduct.getName());
                holderSeletedItem.price.setText(selectedProduct.getPrice());
                holderSeletedItem.quantity.setText(selectedProduct.getQuantity());
                break;
            case TYPE_ITEM_HEADING_BILL_SUMMARY:
                ViewHolderHeadingBillSummary holderHeadingBillSummary = (ViewHolderHeadingBillSummary) holder;
                HeadingBillSummary headingBillSummary = (HeadingBillSummary) itemsList.get(position);
                holderHeadingBillSummary.heading.setText(headingBillSummary.getHeading());
                break;
            case TYPE_ITEM_HEADING_PAYMENT_MODE:
                ViewHolderHeadingPaymentMode holderHeadingPaymentMode = (ViewHolderHeadingPaymentMode) holder;
                HeadingPaymentMode headingPaymentMode = (HeadingPaymentMode) itemsList.get(position);
                holderHeadingPaymentMode.heading.setText(headingPaymentMode.getHeading());
                break;
            case TYPE_ITEM_BILL_PRODUCT:
                ViewHolderBillProduct holderBillProduct = (ViewHolderBillProduct) holder;
                BillProduct billProduct = (BillProduct) itemsList.get(position);
                holderBillProduct.name.setText(billProduct.getName());
                holderBillProduct.price.setText(billProduct.getPrice());
                break;
            case TYPE_ITEM_PAYMENT_MODE:
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object object = itemsList.get(position);
        if (object instanceof SelectedProduct) {
            return TYPE_ITEM_SELECTED;
        } else if (object instanceof BillProduct) {
            return TYPE_ITEM_BILL_PRODUCT;
        } else if (object instanceof HeadingBillSummary) {
            return TYPE_ITEM_HEADING_BILL_SUMMARY;
        } else if (object instanceof HeadingPaymentMode) {
            return TYPE_ITEM_HEADING_PAYMENT_MODE;
        } else if (object instanceof PaymentMode) {
            return TYPE_ITEM_PAYMENT_MODE;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }


    public class ViewHolderSeletedItem extends RecyclerView.ViewHolder {

        public TextView name, quantity, price;

        public ViewHolderSeletedItem(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_product_name);
            quantity = (TextView) itemView.findViewById(R.id.btn_count);
            price = (TextView) itemView.findViewById(R.id.product_price);
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


    public class ViewHolderPaymentMode extends RecyclerView.ViewHolder {
        public ViewHolderPaymentMode(View itemView) {
            super(itemView);
        }
    }

    public class ViewHolderBillProduct extends RecyclerView.ViewHolder {

        public TextView name, price;

        public ViewHolderBillProduct(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.product_name);
            price = (TextView) itemView.findViewById(R.id.product_price);

        }
    }

}

