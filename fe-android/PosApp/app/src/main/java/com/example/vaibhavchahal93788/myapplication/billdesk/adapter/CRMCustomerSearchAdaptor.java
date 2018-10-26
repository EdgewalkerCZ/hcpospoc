package com.example.vaibhavchahal93788.myapplication.billdesk.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.JSONCRMCustomerSet;

import java.util.List;

public class CRMCustomerSearchAdaptor extends
        RecyclerView.Adapter<CRMCustomerSearchAdaptor.ViewHolder> {

    private static final String TAG = CRMCustomerSearchAdaptor.class.getSimpleName();

    private Context context;
    private List<JSONCRMCustomerSet> list;
    private OnItemClickListener onItemClickListener;

    public CRMCustomerSearchAdaptor(Context context, List<JSONCRMCustomerSet> list,
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
//            ButterKnife.bind(this, itemView);

            mCustomerName=itemView.findViewById(R.id.crm_customer_name_txt);
            mCustomeremail=itemView.findViewById(R.id.crm_customer_email_txt);
            mCustomerphone=itemView.findViewById(R.id.crm_customer_phone_number);
            mCustomerNameSymbol=itemView.findViewById(R.id.crm_txt_product_symbol);
            mCustomerImage=itemView.findViewById(R.id.crm_customer_image);
            mCustomerNameHeader=itemView.findViewById(R.id.crm_customer_image_layout);

        }

        public void bind(final JSONCRMCustomerSet model,
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

        View view = inflater.inflate(R.layout.list_items_crm_customer, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JSONCRMCustomerSet item = list.get(position);
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