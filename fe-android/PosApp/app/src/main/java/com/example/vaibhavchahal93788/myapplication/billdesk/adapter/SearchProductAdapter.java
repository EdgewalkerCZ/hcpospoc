package com.example.vaibhavchahal93788.myapplication.billdesk.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.payment.SelectProductActivity;

import java.util.ArrayList;
import java.util.List;

public class SearchProductAdapter extends RecyclerView.Adapter<SearchProductAdapter.ViewHolder> {

    private List<String> mDataKeywords;
    private OnItemClickListener mOnItemClickListener;

    public SearchProductAdapter(OnItemClickListener onItemClickListener) {
        mDataKeywords = new ArrayList<>();
        mOnItemClickListener = onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtKeyword;

        public ViewHolder(View itemView) {
            super(itemView);
            txtKeyword = itemView.findViewById(R.id.txt_keyword);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_search_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.txtKeyword.setText(mDataKeywords.get(position));
        holder.txtKeyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataKeywords.size();
    }


    public void addData(List<String> list) {
        mDataKeywords.clear();
        if(list != null) {
            mDataKeywords.addAll(list);
        }
        notifyDataSetChanged();
    }

    public List<String> getData() {
        return mDataKeywords;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
