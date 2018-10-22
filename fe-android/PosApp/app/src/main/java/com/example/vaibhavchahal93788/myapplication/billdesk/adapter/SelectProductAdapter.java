package com.example.vaibhavchahal93788.myapplication.billdesk.adapter;

import android.graphics.Color;
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
import android.widget.TextView;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.AllProduct;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SelectProductAdapter extends RecyclerView.Adapter<SelectProductAdapter.ViewHolder> {

    private List<AllProduct> productList;
    private String[] arrColors = new String[] {"#FFE4C4",
            "#0000FF",
            "#A52A2A",
            "#FF7F50"};
    private OnItemClickListener onItemClickListener;

    public SelectProductAdapter(List<AllProduct> products, OnItemClickListener onItemClickListener) {
        this.productList = products;
        this.onItemClickListener = onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardViewMain;
        CardView cardProductImg;
        ImageView imvProduct;
        TextView txtProductName;
        TextView txtDescription;
        TextView txtPrice;
        TextView txtQuantity;
        TextView txtProductSymbol;

        ViewHolder(View itemView) {
            super(itemView);
            cardViewMain = itemView.findViewById(R.id.card_view);
            cardProductImg = itemView.findViewById(R.id.card_product_img);
            imvProduct = itemView.findViewById(R.id.imv_product);
            txtProductName = itemView.findViewById(R.id.txt_product_name);
            txtDescription = itemView.findViewById(R.id.txt_description);
            txtPrice = itemView.findViewById(R.id.txt_price);
            txtQuantity = itemView.findViewById(R.id.txt_quantity);
            txtProductSymbol = itemView.findViewById(R.id.txt_product_symbol);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_products, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final AllProduct product = productList.get(position);
        holder.txtProductName.setText(product.getName());
        holder.txtPrice.setText("₹"+product.getPrice());
        holder.txtDescription.setText(product.getDesc());
        holder.txtQuantity.setText("QTY: "+product.getQuantity());

        if(product.isSelected()) {
            holder.cardProductImg.setCardBackgroundColor(Color.parseColor("#FF0000"));
        } else {
            int randomNum = 0 + (int)(Math.random() * ((3 - 0) + 1));
            Log.e("random", ""+randomNum);
            holder.cardProductImg.setCardBackgroundColor(Color.parseColor(arrColors[randomNum]));
            if(TextUtils.isEmpty(product.getImg())) {
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

        holder.cardViewMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onItemClickListener.onItemClick(position);
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

    public List<AllProduct> getAllProducts() {
        return productList;
    }

    public void addProducts(List<AllProduct> list) {
        productList.clear();
        if(list != null) {
            productList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}