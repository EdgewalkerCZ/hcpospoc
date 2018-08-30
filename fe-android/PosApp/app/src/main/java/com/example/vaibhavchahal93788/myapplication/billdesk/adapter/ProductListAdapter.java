package com.example.vaibhavchahal93788.myapplication.billdesk.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.OnDataChangeListener;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;

import java.util.ArrayList;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    private ArrayList<ProductListModel> productList;
    private OnDataChangeListener mOnDataChangeListener;

    public ProductListAdapter(ArrayList<ProductListModel> names, OnDataChangeListener onDataChangeListener) {
        this.productList = names;
        this.mOnDataChangeListener = onDataChangeListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ProductListModel model = productList.get(position);
        holder.textViewName.setText(model.getText());
        holder.etPrice.setText(holder.textViewName.getContext().getString(R.string.rupee_symbol) + String.valueOf(model.getPrice() + ".00"));
        final int selectionColor = Color.parseColor("#B8FFBE");

        holder.itemView.setBackgroundColor(model.isSelected() ? selectionColor : Color.WHITE);

        clickEventListItem(holder, model, selectionColor);
        clickEventPlusBtn(holder, model, selectionColor);
        clickEventMinusBtn(holder, model, selectionColor);
    }

    private void clickEventMinusBtn(final ViewHolder holder, final ProductListModel model, final int selectionColor) {
        holder.imgBtnDecreaseCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model.getQuantity() > 0) {
                    model.setQuantity(model.getQuantity() - 1);
                }
                if (model.getQuantity() <= 0) {
                    model.setSelected(false);

                }
                holder.btnCount.setText(String.valueOf(model.getQuantity()));
                holder.itemView.setBackgroundColor(model.isSelected() ? selectionColor : Color.WHITE);
                updateEstPriceDetails();
            }
        });
    }

    private void clickEventPlusBtn(final ViewHolder holder, final ProductListModel model, final int selectionColor) {
        holder.imgBtnIncreaseCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.setSelected(true);
                model.setQuantity(model.getQuantity() + 1);
                holder.btnCount.setText(String.valueOf(model.getQuantity()));
                holder.itemView.setBackgroundColor(model.isSelected() ? selectionColor : Color.WHITE);
                updateEstPriceDetails();
            }
        });
    }

    private void clickEventListItem(final ViewHolder holder, final ProductListModel model, final int selectionColor) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.setSelected(!model.isSelected());
                if (model.isSelected()) {
                    model.setQuantity(model.getQuantity() + 1);
                } else {
                    model.setQuantity(0);
                }
                holder.btnCount.setText(String.valueOf(model.getQuantity()));
                holder.itemView.setBackgroundColor(model.isSelected() ? selectionColor : Color.WHITE);
                updateEstPriceDetails();
            }
        });
    }

    private void updateEstPriceDetails() {
        int totalPrice = 0, totalItems = 0;
        for (ProductListModel listModel : productList) {
            if (listModel.isSelected()) {
                totalPrice = totalPrice + listModel.getQuantity() * listModel.getPrice();
                totalItems = totalItems + listModel.getQuantity();
            }
        }
        mOnDataChangeListener.onDataChanged(totalItems, totalPrice);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName, textViewQtyAvble;
        ImageButton imgBtnIncreaseCount, imgBtnDecreaseCount;
        Button btnCount;
        EditText etPrice;

        ViewHolder(View itemView) {
            super(itemView);
            textViewName = (TextView) itemView.findViewById(R.id.tv_product_name);
            textViewQtyAvble = (TextView) itemView.findViewById(R.id.tv_quantity_available);
            imgBtnIncreaseCount = (ImageButton) itemView.findViewById(R.id.btn_add);
            imgBtnDecreaseCount = (ImageButton) itemView.findViewById(R.id.btn_remove);
            btnCount = (Button) itemView.findViewById(R.id.btn_count);
            etPrice = (EditText) itemView.findViewById(R.id.product_price);
        }
    }

    public void filterList(ArrayList<ProductListModel> filterdNames) {
        this.productList = filterdNames;
        notifyDataSetChanged();
    }

    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener) {
        mOnDataChangeListener = onDataChangeListener;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}