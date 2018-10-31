package com.example.vaibhavchahal93788.myapplication.billdesk.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.activity.BillDetailHistory;
import com.example.vaibhavchahal93788.myapplication.billdesk.activity.TransactionHistoryActivityNew;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.TranHistoryNew;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.customer.DataItem;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryAdapterNew extends RecyclerView.Adapter<TransactionHistoryAdapterNew.ViewHolder> {

    private List<TranHistoryNew> tranHistoryNewList;
    Context ctx;
    private OnItemClickListener onItemClickListener;

    public TransactionHistoryAdapterNew(Context ctx, List<TranHistoryNew> tranHistoryNewList, OnItemClickListener onItemClickListener) {
        this.tranHistoryNewList = tranHistoryNewList;
        this.ctx = ctx;
        this.onItemClickListener = onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvProductName, tvDate, tvPrice;
        ImageView ivProductImage;

        ViewHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
        }
//        @Override
//        public void onClick(View v) {
//            ctx.startActivity(new Intent(ctx, BillDetailHistory.class));
//        }
    public void bind(final TranHistoryNew model,
                     final OnItemClickListener listener) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(getLayoutPosition());

            }
        });
    }

    }
    @Override
    public int getItemCount() {
        //return names.size();
        return tranHistoryNewList.size();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_history, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvProductName.setText(tranHistoryNewList.get(position).getpName());
        TranHistoryNew item = tranHistoryNewList.get(position);
        holder.bind(item, onItemClickListener);
        holder.tvDate.setText(tranHistoryNewList.get(position).getDate());
        holder.tvPrice.setText("\u20B9 "+tranHistoryNewList.get(position).getPrice());
        if(position%2==0)
            holder.ivProductImage.setBackground(ctx.getResources().getDrawable(R.drawable.cash_back));

    }

    public void filterList(List<TranHistoryNew> tranHistoryNewList) {
        this.tranHistoryNewList = tranHistoryNewList;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}