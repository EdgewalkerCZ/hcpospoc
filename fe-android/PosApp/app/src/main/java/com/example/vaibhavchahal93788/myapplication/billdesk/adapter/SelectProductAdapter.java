package com.example.vaibhavchahal93788.myapplication.billdesk.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.AllProduct;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.allproduct.DataItem;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Utility;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SelectProductAdapter extends RecyclerView.Adapter<SelectProductAdapter.ViewHolder> {

    private Context context;
    private List<DataItem> productList;
    private OnItemClickListener onItemClickListener;

    public SelectProductAdapter(Context context, List<DataItem> products, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.productList = products;
        this.onItemClickListener = onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llMain;
        CardView cardProductImg;
        ImageView imvProduct;
        TextView txtProductName;
        TextView txtDescription;
        TextView txtPrice;
        TextView txtQuantity;
        TextView txtProductSymbol;
        TextView txtIncGST;

        ViewHolder(View itemView) {
            super(itemView);
            llMain = itemView.findViewById(R.id.ll_main);
            cardProductImg = itemView.findViewById(R.id.card_product_img);
            imvProduct = itemView.findViewById(R.id.imv_product);
            txtProductName = itemView.findViewById(R.id.txt_product_name);
            txtDescription = itemView.findViewById(R.id.txt_description);
            txtPrice = itemView.findViewById(R.id.txt_price);
            txtQuantity = itemView.findViewById(R.id.txt_quantity);
            txtProductSymbol = itemView.findViewById(R.id.txt_product_symbol);
            txtIncGST = itemView.findViewById(R.id.txt_inc_gst);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_products, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final DataItem product = productList.get(position);
        holder.txtProductName.setText(product.getName());

        DecimalFormat dec = new DecimalFormat("#0.00");
        String price = dec.format(convertDouble(product.getSalePrice()));

        holder.txtPrice.setText("₹"+price);
        holder.txtDescription.setText(product.getDescription());
        holder.txtQuantity.setText("QTY: "+product.getQuantity());

        if(product.isSelected()) {
            holder.cardProductImg.setCardBackgroundColor(Color.parseColor("#666666"));
            holder.llMain.setBackgroundColor(Color.parseColor("#90c0c0c0"));
            holder.imvProduct.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_checked));
            holder.imvProduct.setVisibility(View.VISIBLE);
            holder.txtProductSymbol.setVisibility(View.GONE);
        } else {
            holder.llMain.setBackgroundColor(Color.parseColor("#ffffff"));
            holder.cardProductImg.setCardBackgroundColor(Color.parseColor(Utility.getColorForIndex(position)));
            if(TextUtils.isEmpty(/*product.getImg()*/"")) {
                holder.imvProduct.setVisibility(View.GONE);
                holder.txtProductSymbol.setVisibility(View.VISIBLE);
                if(!TextUtils.isEmpty(product.getName())) {
                    String initial = product.getName().subSequence(0, 1).toString().toUpperCase();
                    holder.txtProductSymbol.setText(initial);
                }
            } else {
                //ToDo: set image from web url
                holder.imvProduct.setVisibility(View.VISIBLE);
                holder.txtProductSymbol.setVisibility(View.GONE);
                holder.txtProductSymbol.setText("");
            }
        }

        if(product.isIsGst()) {
            holder.txtIncGST.setVisibility(View.VISIBLE);
        } else {
            holder.txtIncGST.setVisibility(View.GONE);
        }

        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<DataItem> getData() {
        return productList;
    }

    public void addData(List<DataItem> list) {
        productList.clear();
        if(list != null) {
            productList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private double convertDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}