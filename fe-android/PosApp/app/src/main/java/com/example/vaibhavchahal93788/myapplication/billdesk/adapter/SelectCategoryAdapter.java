package com.example.vaibhavchahal93788.myapplication.billdesk.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.AllCategory;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.AllProduct;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Utility;

import java.util.List;

public class SelectCategoryAdapter extends RecyclerView.Adapter<SelectCategoryAdapter.ViewHolder> {

    private Context context;
    private List<AllCategory> productList;
    private OnItemClickListener onItemClickListener;

    public SelectCategoryAdapter(Context context, List<AllCategory> products, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.productList = products;
        this.onItemClickListener = onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardProductImg;
        ImageView imvProduct;
        TextView txtProductName;
        TextView txtProductSymbol;
        ImageView imvPlus;
        LinearLayout llSubCategory;

        ViewHolder(View itemView) {
            super(itemView);
            cardProductImg = itemView.findViewById(R.id.card_product_img);
            imvProduct = itemView.findViewById(R.id.imv_product);
            txtProductName = itemView.findViewById(R.id.txt_product_name);
            txtProductSymbol = itemView.findViewById(R.id.txt_product_symbol);
            imvPlus = itemView.findViewById(R.id.imv_plus);
            llSubCategory = itemView.findViewById(R.id.ll_sub_category);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_categories, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final AllCategory category = productList.get(position);
        holder.txtProductName.setText(category.getCategory());

        holder.cardProductImg.setCardBackgroundColor(Color.parseColor(Utility.getColorForIndex(position)));
        if(TextUtils.isEmpty(category.getImage())) {
            holder.imvProduct.setVisibility(View.GONE);
            holder.txtProductSymbol.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(category.getCategory())) {
                String initial = category.getCategory().subSequence(0, 1).toString().toUpperCase();
                holder.txtProductSymbol.setText(initial);
            }
        } else {
            //ToDo: set image from web url
            holder.imvProduct.setVisibility(View.VISIBLE);
            holder.txtProductSymbol.setVisibility(View.GONE);
            holder.txtProductSymbol.setText("");
        }

        if(category.isSelected()) {
            holder.llSubCategory.setVisibility(View.VISIBLE);
            holder.imvPlus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_minus_gray));
        } else {
            holder.llSubCategory.setVisibility(View.GONE);
            holder.imvPlus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_plus_gray));
        }

        holder.imvPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onExpandCollapse(position);
            }
        });

        addSubcategories(holder.llSubCategory, category.getSubCategory());
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

    public List<AllCategory> getData() {
        return productList;
    }

    public void addData(List<AllCategory> list) {
        productList.clear();
        if(list != null) {
            productList.addAll(list);
        }
        notifyDataSetChanged();
    }


    private void addSubcategories(LinearLayout linearLayout, String[] items) {
        linearLayout.removeAllViews();
        if(items == null) return;
        LayoutInflater inflater = LayoutInflater.from(context);
        for(final String item : items) {
            View view = inflater.inflate(R.layout.list_item_sub_category, null);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(item);
                }
            });
            TextView title = view.findViewById(R.id.txt_sub_category);
            title.setText(item);
            linearLayout.addView(view);
        }
    }

    public interface OnItemClickListener {
        void onExpandCollapse(int position);
        void onItemClick(String subCategory);
    }
}
