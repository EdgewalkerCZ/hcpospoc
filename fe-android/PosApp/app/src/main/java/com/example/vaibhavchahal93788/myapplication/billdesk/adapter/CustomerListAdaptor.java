package com.example.vaibhavchahal93788.myapplication.billdesk.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.JsonCustomer;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.JsonCustomerSet;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.ProductListModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.customer.DataItem;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.customer.JSONCustomerResponse;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Utility;

import java.util.ArrayList;
import java.util.List;



public class CustomerListAdaptor extends
        RecyclerView.Adapter<CustomerListAdaptor.ViewHolder> {

    private static final String TAG = CustomerListAdaptor.class.getSimpleName();

    private Context context;
    private JSONCustomerResponse list;
    private OnItemClickListener onItemClickListener;

    public CustomerListAdaptor(Context context,JSONCustomerResponse list,
                               OnItemClickListener onItemClickListener) {
        this.context = context;
        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Todo Butterknife bindings
        TextView mCustomerName,mCustomeremail,mCustomerphone,mCustomerNameSymbol;
        ImageView mCustomerImage;
        CardView mCustomerNameHeader;

        public ViewHolder(View itemView) {
            super(itemView);
            mCustomerName=itemView.findViewById(R.id.customer_name_txt);
            mCustomeremail=itemView.findViewById(R.id.customer_email_txt);
            mCustomerphone=itemView.findViewById(R.id.customer_phone_number);
            mCustomerNameSymbol=itemView.findViewById(R.id.txt_product_symbol);
            mCustomerImage=itemView.findViewById(R.id.customer_image);
            mCustomerNameHeader=itemView.findViewById(R.id.customer_image_layout);
//            ButterKnife.bind(this, itemView);

        }

        public void bind(final DataItem model,
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.list_items_customers, parent, false);
//        ButterKnife.bind(this, view);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }
//    public void filterList(ArrayList<JsonCustomer> filterdNames) {
//
//        list.setCustomers(filterdNames);
//
//        notifyDataSetChanged();
//    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DataItem item = list.getData().get(position);
        holder.bind(item, onItemClickListener);
        holder.mCustomerName.setText(item.getName());
        holder.mCustomeremail.setText(item.getName());
        holder.mCustomerphone.setText(item.getPhone());

        if(!TextUtils.isEmpty(item.getName())) {
            holder.mCustomerImage.setVisibility(View.GONE);
            holder.mCustomerNameSymbol.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(item.getName())) {
                String initial = item.getName().subSequence(0, 1).toString().toUpperCase();
                holder.mCustomerNameSymbol.setText(initial);
                holder.mCustomerNameHeader.setCardBackgroundColor(Color.parseColor(Utility.getColorForIndex(position)));
            }
        } else {
            //ToDo: set image from web url
//            holder.mCustomerImage.setVisibility(View.VISIBLE);
//            holder.mCustomerNameSymbol.setVisibility(View.GONE);
//            holder.mCustomerNameSymbol.setText("");
        }
    }


    @Override
    public int getItemCount() {
        return list.getData() == null ? 0 : list.getData().size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}