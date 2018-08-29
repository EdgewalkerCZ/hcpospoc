package com.example.vaibhavchahal93788.myapplication.billdesk.adapter;

import android.content.Context;
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
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;

import java.util.ArrayList;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    private ArrayList<ProductListModel> productList;

    public ProductListAdapter(ArrayList<ProductListModel> names) {
        this.productList = names;
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
            }
        });

        holder.imgBtnIncreaseCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.setSelected(true);
                model.setQuantity(model.getQuantity() + 1);
                holder.btnCount.setText(String.valueOf(model.getQuantity()));
                holder.itemView.setBackgroundColor(model.isSelected() ? selectionColor : Color.WHITE);
            }
        });

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
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        ImageButton imgBtnIncreaseCount, imgBtnDecreaseCount;
        Button btnCount;
        EditText etPrice;

        ViewHolder(View itemView) {
            super(itemView);
            textViewName = (TextView) itemView.findViewById(R.id.tv_product_name);
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
}