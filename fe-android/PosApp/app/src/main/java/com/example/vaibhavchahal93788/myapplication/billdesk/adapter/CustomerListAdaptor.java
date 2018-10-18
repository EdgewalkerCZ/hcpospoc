package com.example.vaibhavchahal93788.myapplication.billdesk.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.JsonCustomerSet;

import java.util.List;



public class CustomerListAdaptor extends
        RecyclerView.Adapter<CustomerListAdaptor.ViewHolder> {

    private static final String TAG = CustomerListAdaptor.class.getSimpleName();

    private Context context;
    private List<JsonCustomerSet> list;
    private OnItemClickListener onItemClickListener;

    public CustomerListAdaptor(Context context, List<JsonCustomerSet> list,
                               OnItemClickListener onItemClickListener) {
        this.context = context;
        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Todo Butterknife bindings

        public ViewHolder(View itemView) {
            super(itemView);
//            ButterKnife.bind(this, itemView);

        }

        public void bind(final JsonCustomerSet model,
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


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JsonCustomerSet item = list.get(position);

        //Todo: Setup viewholder for item 
        holder.bind(item, onItemClickListener);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}