package com.example.vaibhavchahal93788.myapplication.billdesk.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.BillProduct;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.HeadingBillSummary;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.HeadingPaymentMode;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.PaymentMode;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.SelectedProduct;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.TotalBillDetail;

import java.util.List;

public class BillDetailRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_ITEM_SELECTED = 0;
    public static final int TYPE_ITEM_HEADING_BILL_SUMMARY = 1;
    public static final int TYPE_ITEM_HEADING_PAYMENT_MODE = 2;
    public static final int TYPE_ITEM_BILL_PRODUCT = 3;
    public static final int TYPE_ITEM_TOTAL_DETAIL = 4;
    public static final int TYPE_ITEM_PAYMENT_MODE = 5;

    private final List<Object> itemsList;
    private OnDataChangeListener onDataChangeListener;

    private Context context;

    public BillDetailRecyclerAdapter(List<Object> list, OnDataChangeListener onDataChangeListener) {
        itemsList = list;
        this.onDataChangeListener = onDataChangeListener;
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
        } else if (viewType == TYPE_ITEM_TOTAL_DETAIL) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_bill_item, parent, false);
            return new ViewHolderTotalBill(v);
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
                holderSeletedItem.price.setEnabled(true);
                holderSeletedItem.price.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rupee_icon, 0, 0, 0);
                holderSeletedItem.price.setText(String.valueOf(selectedProduct.getPrice()));
                holderSeletedItem.quantity.setText(String.valueOf(selectedProduct.getQuantity()));

                clickEventEditPrice(selectedProduct, position, holderSeletedItem);

                clickEventPlusBtn(holderSeletedItem, selectedProduct, position);
                clickEventMinusBtn(holderSeletedItem, selectedProduct, position);

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
                holderBillProduct.name.setText(billProduct.getName() + " x " + billProduct.getQuantity());
                holderBillProduct.price.setText(holderBillProduct.name.getContext().getString(R.string.rupee_symbol) + String.valueOf(billProduct.getPrice() * billProduct.getQuantity()));
                break;
            case TYPE_ITEM_TOTAL_DETAIL:
                ViewHolderTotalBill holderTotalBill = (ViewHolderTotalBill) holder;
                TotalBillDetail totalBillDetail = (TotalBillDetail) itemsList.get(position);
                holderTotalBill.name.setTypeface(null, Typeface.BOLD);
                holderTotalBill.price.setTypeface(null, Typeface.BOLD);
                holderTotalBill.name.setText(totalBillDetail.getTitle());
                holderTotalBill.price.setText(holderTotalBill.name.getContext().getString(R.string.rupee_symbol) + String.valueOf(totalBillDetail.getTotalPrice()));
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
        } else if (object instanceof TotalBillDetail) {
            return TYPE_ITEM_TOTAL_DETAIL;
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

        public TextView name, quantity;
        public EditText price;
        ImageButton imageBtnIncrease, imageBtnDecrease;

        public ViewHolderSeletedItem(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_product_name);
            quantity = (TextView) itemView.findViewById(R.id.btn_count);
            imageBtnIncrease = (ImageButton) itemView.findViewById(R.id.btn_add);
            imageBtnDecrease = (ImageButton) itemView.findViewById(R.id.btn_remove);
            price = (EditText) itemView.findViewById(R.id.product_price);
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

    public class ViewHolderTotalBill extends RecyclerView.ViewHolder {

        public TextView name, price;

        public ViewHolderTotalBill(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.product_name);
            price = (TextView) itemView.findViewById(R.id.product_price);

        }
    }

    private void clickEventEditPrice(final SelectedProduct model, final int position, final ViewHolderSeletedItem holderSeletedItem) {
        holderSeletedItem.price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                onDataChangeListener.onDataChangedWithPrice(model.getPrice(), position);
                if (charSequence.toString().isEmpty()) {
                    model.setPrice(0);
                } else {
                    model.setPrice(Math.round(Float.parseFloat(charSequence.toString())));
                }
                onDataChangeListener.onDataChanged(model.getQuantity(), position, model.getPrice());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void clickEventMinusBtn(final ViewHolderSeletedItem holder, final SelectedProduct model, final int position) {
        holder.imageBtnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model.getQuantity() > 0) {
                    model.setQuantity(model.getQuantity() - 1);
                }
                holder.quantity.setText(String.valueOf(model.getQuantity()));
                onDataChangeListener.onDataChanged(model.getQuantity(), position, -1);
            }
        });
    }

    private void clickEventPlusBtn(final ViewHolderSeletedItem holder, final SelectedProduct model, final int position) {
        holder.imageBtnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.setQuantity(model.getQuantity() + 1);
                holder.quantity.setText(String.valueOf(model.getQuantity()));
                onDataChangeListener.onDataChanged(model.getQuantity(), position, -1);
            }
        });
    }

    public interface OnDataChangeListener {
        void onDataChanged(int quantity, int position, int price);
    }
}

