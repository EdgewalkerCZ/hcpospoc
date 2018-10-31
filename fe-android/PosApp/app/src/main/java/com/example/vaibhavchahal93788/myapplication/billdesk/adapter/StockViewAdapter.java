package com.example.vaibhavchahal93788.myapplication.billdesk.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.activity.StockViewProductActivity;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.AllProductModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.allproduct.AllProductResponse;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.allproduct.DataItem;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.IApiRequestComplete;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Utility;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class StockViewAdapter extends RecyclerView.Adapter<StockViewAdapter.ViewHolder> {

    private List<DataItem> productList;
    private String[] arrColors = new String[]{"#FFE4C4",
            "#0000FF",
            "#A52A2A",
            "#FF7F50"};
    private OnItemClickListener onItemClickListener;
    private Context mContext;

    public StockViewAdapter(Context mContext, List<DataItem> data, OnItemClickListener onItemClickListener) {
        this.productList = data;
        this.onItemClickListener = onItemClickListener;
        this.mContext=mContext;
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_row_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        DataItem product = productList.get(position);
        holder.txtProductName.setText(product.getName());
        DecimalFormat dec = new DecimalFormat("#0.00");
        String price = dec.format(Utility.convertDouble(mContext,product.getSalePrice()));

        holder.txtPrice.setText("₹" +price);
        holder.txtDescription.setText(product.getDescription());
        holder.txtQuantity.setText("QTY: " + product.getQuantity());

       /* if (product.isSelected()) {
          //  holder.cardProductImg.setCardBackgroundColor(Color.parseColor("#FF0000"));
        } else {*/
            int randomNum = 0 + (int) (Math.random() * ((3 - 0) + 1));
            Log.e("random", "" + randomNum);
            holder.cardProductImg.setCardBackgroundColor(Color.parseColor(arrColors[randomNum]));
           // if (TextUtils.isEmpty(product.getImg())) {
                holder.imvProduct.setVisibility(View.GONE);
                holder.txtProductSymbol.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(product.getName())) {
                    String initial = product.getName().subSequence(0, 1).toString().toUpperCase();
                    holder.txtProductSymbol.setText(initial);
                }
          //  }
            /*else {
                //ToDo: set image from web url
                holder.imvProduct.setVisibility(View.VISIBLE);
                holder.txtProductSymbol.setVisibility(View.GONE);
                holder.txtProductSymbol.setText("");
            }*/
       // }

        holder.cardViewMain.setOnClickListener(new View.OnClickListener() {
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

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void filterList(ArrayList<DataItem> filterdNames) {
        this.productList = filterdNames;
        notifyDataSetChanged();
    }
}
