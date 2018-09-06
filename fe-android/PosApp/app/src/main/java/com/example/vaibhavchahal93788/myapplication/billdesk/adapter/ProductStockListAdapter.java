package com.example.vaibhavchahal93788.myapplication.billdesk.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;

import java.util.ArrayList;
import java.util.List;

public class ProductStockListAdapter extends RecyclerView.Adapter<ProductStockListAdapter.ViewHolder> {

    private List<ProductListModel> productList;

    public ProductStockListAdapter(List<ProductListModel> names) {
        this.productList = names;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_stock_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ProductListModel model = productList.get(position);
        holder.textViewName.setText(model.getLabel());
        holder.textViewPrice.setText(holder.textViewName.getContext().getString(R.string.rupee_symbol) + Math.round(Float.valueOf(model.getPrice())) + ".00");
        holder.textViewTag.setText(model.getLabel().toString().substring(0, 1));
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName, textViewPrice, textViewTag;

        ViewHolder(View itemView) {
            super(itemView);
            textViewName = (TextView) itemView.findViewById(R.id.tv_product_name);
            textViewPrice = (TextView) itemView.findViewById(R.id.product_price);
            textViewTag = (TextView) itemView.findViewById(R.id.tv_product_tag);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    /*public void filterList(ArrayList<String> filterdNames) {
        this.names = filterdNames;
        notifyDataSetChanged();
    }*/
}