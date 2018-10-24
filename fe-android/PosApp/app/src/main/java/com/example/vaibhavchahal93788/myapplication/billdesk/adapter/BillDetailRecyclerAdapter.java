package com.example.vaibhavchahal93788.myapplication.billdesk.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.BillProduct;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.DiscountModel;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.HeadingBillSummary;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.HeadingPaymentMode;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.PaymentMode;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.SelectedProduct;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.TotalBillDetail;

import java.util.List;

import butterknife.OnItemSelected;


public class BillDetailRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_ITEM_SELECTED = 0;
    public static final int TYPE_ITEM_HEADING_BILL_SUMMARY = 5;
    public static final int TYPE_ITEM_HEADING_PAYMENT_MODE = 2;
    public static final int TYPE_ITEM_BILL_PRODUCT = 3;
    public static final int TYPE_ITEM_TOTAL_DETAIL = 4;
    public static final int TYPE_ITEM_PAYMENT_MODE = 1;


    private final List<Object> itemsList;
    private OnDataChangeListener onDataChangeListener;
    private DiscountModel discountModelIs = DiscountModel.getInstance();

    private Context context;
    private String textBeforeChanged;

    public BillDetailRecyclerAdapter(List<Object> list, OnDataChangeListener onDataChangeListener) {
        itemsList = list;
        this.onDataChangeListener = onDataChangeListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == TYPE_ITEM_SELECTED) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_product_list_item, parent, false);
            return new ViewHolderSeletedItem(v);
        } else if (viewType == TYPE_ITEM_HEADING_BILL_SUMMARY) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_item_heading, parent, false);
            v.setVisibility(View.GONE);
            return new ViewHolderHeadingBillSummary(v);
        } else if (viewType == TYPE_ITEM_HEADING_PAYMENT_MODE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_item_heading, parent, false);
            return new ViewHolderHeadingPaymentMode(v);
        } else if (viewType == TYPE_ITEM_BILL_PRODUCT) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_bill_item, parent, false);
            v.setVisibility(View.GONE);
            return new ViewHolderBillProduct(v);
        } else if (viewType == TYPE_ITEM_TOTAL_DETAIL) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_bill_item, parent, false);
            v.setVisibility(View.GONE);
            return new ViewHolderTotalBill(v);
        } else if (viewType == TYPE_ITEM_PAYMENT_MODE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_mode_item, parent, false);
            return new ViewHolderPaymentMode(v);
        }
//        else if (viewType == TYPE_ITEM_DISCOUNT) {
//            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.discount_layout, parent, false);
//            return new ViewHolderPaymentMode(v);
//        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //
        switch (getItemViewType(position)) {
            case TYPE_ITEM_SELECTED:

                ViewHolderSeletedItem holderSeletedItem = (ViewHolderSeletedItem) holder;
                SelectedProduct selectedProduct = (SelectedProduct) itemsList.get(position);
                holderSeletedItem.name.setText(selectedProduct.getName());

//                holderSeletedItem.price.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rupee_icon, 0, 0, 0);
                holderSeletedItem.price.setText(String.valueOf(selectedProduct.getFinalPrice()));

                holderSeletedItem.quantity.setText(String.valueOf(selectedProduct.getQuantity()));
                clickEventEditPrice(selectedProduct, position, holderSeletedItem);
                clickEventPlusBtn(holderSeletedItem, selectedProduct, position);
                clickEventMinusBtn(holderSeletedItem, selectedProduct, position);
                holderSeletedItem.basePrice.setText(holderSeletedItem.name.getContext().getString(R.string.rupee_symbol) + String.valueOf(selectedProduct.getFinalPrice()) + "(Incl. GST)");

                break;
            case TYPE_ITEM_HEADING_BILL_SUMMARY:
                ViewHolderHeadingBillSummary holderHeadingBillSummary = (ViewHolderHeadingBillSummary) holder;
                HeadingBillSummary headingBillSummary = (HeadingBillSummary) itemsList.get(position);
                holderHeadingBillSummary.heading.setText(headingBillSummary.getHeading());
                break;
            case TYPE_ITEM_HEADING_PAYMENT_MODE:
                ViewHolderHeadingPaymentMode holderHeadingPaymentMode = (ViewHolderHeadingPaymentMode) holder;
                HeadingPaymentMode headingPaymentMode = (HeadingPaymentMode) itemsList.get(position);
                holderHeadingPaymentMode.heading.setText(headingPaymentMode.getHeading());
                break;
            case TYPE_ITEM_BILL_PRODUCT:
                ViewHolderBillProduct holderBillProduct = (ViewHolderBillProduct) holder;
                BillProduct billProduct = (BillProduct) itemsList.get(position);
                String productName = billProduct.getName();
                if (billProduct.getName().length() >= 15) {
                    productName = productName.substring(0, 15);
                }
                holderBillProduct.name.setText(productName + " x " + billProduct.getQuantity() + " (Gst Inc1212 " + billProduct.getGstTax() + "%)");
                holderBillProduct.price.setText(holderBillProduct.name.getContext().getString(R.string.rupee_symbol) + String.valueOf(billProduct.getFinalPrice() * billProduct.getQuantity()));
                break;
            case TYPE_ITEM_TOTAL_DETAIL:
                ViewHolderTotalBill holderTotalBill = (ViewHolderTotalBill) holder;
                TotalBillDetail totalBillDetail = (TotalBillDetail) itemsList.get(position);

                holderTotalBill.name.setTypeface(null, Typeface.BOLD);
                holderTotalBill.price.setTypeface(null, Typeface.BOLD);
                holderTotalBill.name.setText(totalBillDetail.getTitle());
                holderTotalBill.price.setText(holderTotalBill.name.getContext().getString(R.string.rupee_symbol) + String.valueOf(totalBillDetail.getTotalPrice()));
                // txtviewEstPrice.setText(String.format(getString(R.string.text_estimated_price), totalItems, totalPrice));
                break;
            case TYPE_ITEM_PAYMENT_MODE:

                //  BillProduct selectedProducts = (BillProduct) itemsList.get(position);
                // Log.e("selectedProducts==>",selectedProducts+"");
                ViewHolderPaymentMode holderPaymentMode = (ViewHolderPaymentMode) holder;
                clickEventDiscountBtn(holderPaymentMode, position);
                break;

            default:
                break;
        }
    }

    private void clickEventDiscountBtn(final ViewHolderPaymentMode holder, final int position) {
        holder.discount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //  itemsList.smsmoothScrollToPosition(0);
                if (s.toString().equals(" ") || s.toString().equals("")) {
                    s = "0";
                }
                int discount = Math.round(Float.parseFloat(s.toString()));
                discountModelIs.setDiscount(discount);
                onDataChangeListener.onDiscount(discount);


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        holder.paymentMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                String selected = parentView.getItemAtPosition(position).toString();
                Log.e("====>",position+""+"=Selected=>"+selected);
                onDataChangeListener.seletedPaymentMode(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });



    }

    @Override
    public int getItemViewType(int position) {
        Object object = itemsList.get(position);

        if (object instanceof SelectedProduct) {
            return TYPE_ITEM_SELECTED;
        } else if (object instanceof BillProduct) {
            return TYPE_ITEM_BILL_PRODUCT;
        } else if (object instanceof TotalBillDetail) {
            return TYPE_ITEM_TOTAL_DETAIL;
        } else if (object instanceof HeadingBillSummary) {
            return TYPE_ITEM_HEADING_BILL_SUMMARY;
        } else if (object instanceof HeadingPaymentMode) {
            return TYPE_ITEM_HEADING_PAYMENT_MODE;
        } else if (object instanceof PaymentMode) {
            return TYPE_ITEM_PAYMENT_MODE;
        } else if (object instanceof PaymentMode) {
            return TYPE_ITEM_PAYMENT_MODE;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }


    public class ViewHolderSeletedItem extends RecyclerView.ViewHolder {

        public TextView name, quantity, basePrice, price;
        // public EditText price;
        ImageButton imageBtnIncrease, imageBtnDecrease;

        public ViewHolderSeletedItem(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_product_name);
            quantity = (TextView) itemView.findViewById(R.id.btn_count);
            basePrice = (TextView) itemView.findViewById(R.id.tv_base_price);
            imageBtnIncrease = (ImageButton) itemView.findViewById(R.id.btn_add);
            imageBtnDecrease = (ImageButton) itemView.findViewById(R.id.btn_remove);
            price = (TextView) itemView.findViewById(R.id.product_price);
        }
    }


    public class ViewHolderHeadingBillSummary extends RecyclerView.ViewHolder {
        public TextView heading;

        public ViewHolderHeadingBillSummary(View itemView) {
            super(itemView);
            heading = (TextView) itemView.findViewById(R.id.tv_heading);
        }
    }

    public class ViewHolderHeadingPaymentMode extends RecyclerView.ViewHolder {
        public TextView heading;

        public ViewHolderHeadingPaymentMode(View itemView) {
            super(itemView);
            heading = (TextView) itemView.findViewById(R.id.tv_heading);
        }
    }


    public class ViewHolderPaymentMode extends RecyclerView.ViewHolder {
        public TextView heading;
        public EditText discount;
        public Spinner paymentMode;
        public ViewHolderPaymentMode(View itemView) {

            super(itemView);
            discount = (EditText) itemView.findViewById(R.id.product_discount);
            paymentMode=(Spinner)itemView.findViewById(R.id.payment_mode);
        }
    }

    public class ViewHolderBillProduct extends RecyclerView.ViewHolder {

        public TextView name, price;

        public ViewHolderBillProduct(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.product_name);
            price = (TextView) itemView.findViewById(R.id.product_price);

        }
    }

    public class ViewHolderTotalBill extends RecyclerView.ViewHolder {

        public TextView name, price, basePrice;

        public ViewHolderTotalBill(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.product_name);
            price = (TextView) itemView.findViewById(R.id.product_price);
            basePrice = (TextView) itemView.findViewById(R.id.tv_base_price);
        }
    }

    private void clickEventEditPrice(final SelectedProduct model, final int position, final ViewHolderSeletedItem holderSeletedItem) {

        textBeforeChanged = "";
        holderSeletedItem.price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textBeforeChanged = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                onDataChangeListener.onDataChangedWithPrice(model.getPrice(), position);
                if (textBeforeChanged.equals(String.valueOf(charSequence))) {

                } else {
                    if (charSequence.toString().isEmpty()) {
                        model.setFinalPrice(0);
                    } else {
                        model.setFinalPrice(Math.round(Float.parseFloat(charSequence.toString())));
                    }
                    Log.d("=Text final Price==>", Math.round(Float.parseFloat(charSequence.toString())) + "");
                    onDataChangeListener.onDataChanged(model.getQuantity(), position, model.getFinalPrice());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void clickEventMinusBtn(final ViewHolderSeletedItem holder, final SelectedProduct model, final int position) {
        holder.imageBtnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model.getQuantity() != 1) {
                    if (model.getQuantity() > 0) {
                        model.setFinalPrice(model.getFinalPrice() / model.getQuantity());
                        model.setQuantity(model.getQuantity() - 1);

                    }
                    Log.e("=Minus ISISIS=>", model.getFinalPrice() + "");
                    holder.quantity.setText(String.valueOf(model.getQuantity()));
                    holder.price.setText(String.valueOf(model.getFinalPrice()));
                    onDataChangeListener.onDataChanged(model.getQuantity(), position, -1);
                    //update final price with discount
                    onDataChangeListener.onDiscount(discountModelIs.getDiscount());
                }
            }
        });
    }

    private void clickEventPlusBtn(final ViewHolderSeletedItem holder, final SelectedProduct model, final int position) {
        holder.imageBtnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.setQuantity(model.getQuantity() + 1);
                model.setFinalPrice(model.getPrice() * model.getQuantity());
                holder.quantity.setText(String.valueOf(model.getQuantity()));
                Log.e("=Plus ISISIS=>", model.getFinalPrice() + "");
                holder.price.setText(String.valueOf(model.getFinalPrice()));
                onDataChangeListener.onDataChanged(model.getQuantity(), position, -1);
                //update final price with discount
                onDataChangeListener.onDiscount(discountModelIs.getDiscount());
            }
        });
    }

    public interface OnDataChangeListener {
        void onDataChanged(int quantity, int position, int price);

        void onDiscount(int discount);
        void seletedPaymentMode(String paymentMode);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
