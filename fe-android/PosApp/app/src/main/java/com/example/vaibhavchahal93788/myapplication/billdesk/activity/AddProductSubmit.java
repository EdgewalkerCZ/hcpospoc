package com.example.vaibhavchahal93788.myapplication.billdesk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.api.ProductApiHelper;
import com.example.vaibhavchahal93788.myapplication.billdesk.model.productsuccess.AddProductResponse;
import com.example.vaibhavchahal93788.myapplication.billdesk.network.IApiRequestComplete;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Constants;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Utility;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;


public class AddProductSubmit extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener, TextWatcher {

    private Spinner sp_product_type, sp_category, sp_sub_category, sp_type, sp_color, sp_variant, sp_refregerator_warranty, sp_refregerator_capacity,
            sp_refregerator_color, sp_star_refregerator, sp_television_warranty, sp_size, sp_television_color,
            sp_television_star, sp_connectivity, sp_wire_phone_color, sp_cable_type, sp_power_compatible, sp_power_mah, sp_camera_pixel,
            sp_mobile_warranty, sp_compatible;
    private LinearLayout ll_mobile, ll_power_bank, ll_wired_ear_phones, ll_television, ll_refregerator, ll_ac_tv;
    private TextInputLayout til_model, til_brand_name, til_quantity, til_gst, til_excl_gst, til_description, til_serial_number, til_refri_special,
            til_special, til_weight_battery, til_include_components, til_product_dimention, til_mobile_weight;
    private TextInputEditText et_model, et_brand_name, et_quantity, et_gst, et_excl_gst, et_description, et_serial_number,
            et_special_feature_refri, et_special_feature, et_weight_battery, et_include_components, et_product_dimention, et_mobile_weight;
    private Button bt_add_submit;
    private TextView tv_price_lebel, tv_est_price;
    private ImageView im_scan;

    private double gst_percent;
    private int REQUEST_CODE = 11;
    private PrettyDialog dialog;
    private ProgressBar pb_dialogue;


    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, AddProductSubmit.class);
        intent.putExtra(activity.getResources().getString(R.string.parent_class_name), activity.getClass().getSimpleName());
        activity.overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_submit);
        dialog = new PrettyDialog(this);
        getToolbar();
        iniView();
        iniListner();
    }

    private void iniListner() {

        sp_category.setOnItemSelectedListener(this);
        sp_product_type.setOnItemSelectedListener(this);
        sp_sub_category.setOnItemSelectedListener(this);
        sp_type.setOnItemSelectedListener(this);
        bt_add_submit.setOnClickListener(this);
        im_scan.setOnClickListener(this);

        et_model.addTextChangedListener(this);
        et_brand_name.addTextChangedListener(this);
        et_quantity.addTextChangedListener(this);
        et_description.addTextChangedListener(this);
        et_excl_gst.addTextChangedListener(this);
        et_weight_battery.addTextChangedListener(this);
    }

    private void iniView() {


        sp_product_type = findViewById(R.id.sp_product_type);
        sp_category = findViewById(R.id.sp_category);
        sp_sub_category = findViewById(R.id.sp_sub_category);
        sp_type = findViewById(R.id.sp_type);

        sp_color = findViewById(R.id.sp_color);
        sp_variant = findViewById(R.id.sp_variant);

        ll_mobile = findViewById(R.id.ll_mobile);
        ll_power_bank = findViewById(R.id.ll_power_bank);
        ll_wired_ear_phones = findViewById(R.id.ll_wired_ear_phones);
        ll_television = findViewById(R.id.ll_television);
        ll_refregerator = findViewById(R.id.ll_refregerator);
        ll_ac_tv = findViewById(R.id.ll_ac_tv);
        til_model = findViewById(R.id.til_model);

        sp_color = findViewById(R.id.sp_color);
        sp_variant = findViewById(R.id.sp_variant);
        sp_refregerator_warranty = findViewById(R.id.sp_refregerator_warranty);
        sp_refregerator_capacity = findViewById(R.id.sp_refregerator_capacity);
        sp_refregerator_color = findViewById(R.id.sp_refregerator_color);
        sp_star_refregerator = findViewById(R.id.sp_star_refregerator);
        sp_television_warranty = findViewById(R.id.sp_television_warranty);
        sp_size = findViewById(R.id.sp_size);
        sp_television_color = findViewById(R.id.sp_television_color);
        sp_television_star = findViewById(R.id.sp_television_star);
        sp_connectivity = findViewById(R.id.sp_connectivity);
        sp_wire_phone_color = findViewById(R.id.sp_wire_phone_color);
        sp_cable_type = findViewById(R.id.sp_cable_type);
        sp_power_compatible = findViewById(R.id.sp_power_compatible);
        sp_power_mah = findViewById(R.id.sp_power_mah);
        sp_camera_pixel = findViewById(R.id.sp_camera_pixel);
        sp_mobile_warranty = findViewById(R.id.sp_mobile_warranty);
        sp_compatible = findViewById(R.id.sp_compatible);


        til_model = findViewById(R.id.til_model);
        til_brand_name = findViewById(R.id.til_brand_name);
        til_quantity = findViewById(R.id.til_quantity);
        til_gst = findViewById(R.id.til_gst);
        til_excl_gst = findViewById(R.id.til_excl_gst);
        til_description = findViewById(R.id.til_description);
        til_serial_number = findViewById(R.id.til_serial_number);
        til_refri_special = findViewById(R.id.til_refri_special);
        til_special = findViewById(R.id.til_special);
        til_weight_battery = findViewById(R.id.til_weight_battery);
        til_include_components = findViewById(R.id.til_include_components);
        til_product_dimention = findViewById(R.id.til_product_dimention);
        til_mobile_weight = findViewById(R.id.til_mobile_weight);


        et_model = findViewById(R.id.et_model);
        et_brand_name = findViewById(R.id.et_brand_name);
        et_quantity = findViewById(R.id.et_quantity);
        et_gst = findViewById(R.id.et_gst);
        et_excl_gst = findViewById(R.id.et_excl_gst);
        et_description = findViewById(R.id.et_description);
        et_serial_number = findViewById(R.id.et_serial_number);
        et_special_feature_refri = findViewById(R.id.et_special_feature_refri);
        et_special_feature = findViewById(R.id.et_special_feature);
        et_weight_battery = findViewById(R.id.et_weight_battery);
        et_include_components = findViewById(R.id.et_include_components);
        et_product_dimention = findViewById(R.id.et_product_dimention);
        et_mobile_weight = findViewById(R.id.et_mobile_weight);

        bt_add_submit = findViewById(R.id.bt_add_submit);
        tv_price_lebel = findViewById(R.id.tv_price_lebel);
        tv_est_price = findViewById(R.id.tv_est_price);

        im_scan = findViewById(R.id.im_scan);
        pb_dialogue = findViewById(R.id.pb_dialogue);


    }

    private void getToolbar() {

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.add_product_new));
    }

    @Override
    public Intent getSupportParentActivityIntent() {
        Intent parentIntent = getIntent();
        String className = parentIntent.getStringExtra(getResources().getString(R.string.parent_class_name)); //getting the parent class name
        Intent newIntent = null;
        try {
            //you need to define the class with package name
            newIntent = new Intent(AddProductSubmit.this, Class.forName(Constants.PACKAGE + className));
            newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            overridePendingTransition(R.anim.animation_enter_backward, R.anim.animation_leave_backward);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return newIntent;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {


            case R.id.sp_product_type:
                clearAllEditField();
                if (position == 1) {

                    hideAndResetAllVariantValues();

                    ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(this,
                            R.array.product_categories, android.R.layout.simple_spinner_item);
                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // attaching data adapter to spinner
                    sp_category.setAdapter(dataAdapter);


                } else if (position == 2) {
                    hideAndResetAllVariantValues();
                    ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(this,
                            R.array.product_categories, android.R.layout.simple_spinner_item);
                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // attaching data adapter to spinner
                    sp_category.setAdapter(dataAdapter);
                }

                break;
            case R.id.sp_category:
                clearAllEditField();
                if (position == 1) {
                    hideAndResetAllVariantValues();

                    ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(this,
                            R.array.product_sub_categories_mobile, android.R.layout.simple_spinner_item);
                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // attaching data adapter to spinner
                    sp_sub_category.setAdapter(dataAdapter);

                } else if (position == 2) {

                    hideAndResetAllVariantValues();
                    ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(this,
                            R.array.product_sub_categories_mobile_accessories, android.R.layout.simple_spinner_item);
                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // attaching data adapter to spinner
                    sp_sub_category.setAdapter(dataAdapter);


                } else if (position == 3) {

                    hideAndResetAllVariantValues();
                    ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(this,
                            R.array.product_sub_categories_home_appliances, android.R.layout.simple_spinner_item);
                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // attaching data adapter to spinner
                    sp_sub_category.setAdapter(dataAdapter);
                }
                break;
            case R.id.sp_sub_category:
                clearAllEditField();
                if (position == 1 && sp_sub_category.getSelectedItem().equals("Power Bank")) {
                    /* Mobile Accessories*/
                    ll_mobile.setVisibility(View.GONE);
                    ll_power_bank.setVisibility(View.VISIBLE);
                    ll_wired_ear_phones.setVisibility(View.GONE);
                    ll_television.setVisibility(View.GONE);
                    ll_refregerator.setVisibility(View.GONE);
                    ll_ac_tv.setVisibility(View.VISIBLE);
                    ll_ac_tv.setWeightSum(1);
                    sp_type.setVisibility(View.GONE);
                    gst_percent = 18;
                    et_gst.setText(gst_percent + "");

                } else if (position == 1 && sp_sub_category.getSelectedItem().equals("Mobile Phones")) {
                    ll_mobile.setVisibility(View.VISIBLE);
                    ll_power_bank.setVisibility(View.GONE);
                    ll_wired_ear_phones.setVisibility(View.GONE);
                    ll_television.setVisibility(View.GONE);
                    ll_refregerator.setVisibility(View.GONE);

                    ll_ac_tv.setVisibility(View.VISIBLE);
                    ll_ac_tv.setWeightSum(1);
                    sp_type.setVisibility(View.GONE);
                    gst_percent = 12;
                    et_gst.setText(gst_percent + "");

                } else if (position == 2 && sp_sub_category.getSelectedItem().equals("Wired Ear Phones")) {
                    ll_mobile.setVisibility(View.GONE);
                    ll_power_bank.setVisibility(View.GONE);
                    ll_wired_ear_phones.setVisibility(View.VISIBLE);
                    ll_television.setVisibility(View.GONE);
                    ll_refregerator.setVisibility(View.GONE);
                    ll_ac_tv.setVisibility(View.GONE);

                    ll_ac_tv.setVisibility(View.VISIBLE);
                    ll_ac_tv.setWeightSum(1);
                    sp_type.setVisibility(View.GONE);

                    gst_percent = 18;
                    et_gst.setText(gst_percent + "");

                } else if (position == 1 && sp_sub_category.getSelectedItem().equals("Televisions")) {
                    ll_mobile.setVisibility(View.GONE);
                    ll_power_bank.setVisibility(View.GONE);
                    ll_wired_ear_phones.setVisibility(View.GONE);
                    ll_television.setVisibility(View.VISIBLE);
                    ll_refregerator.setVisibility(View.GONE);
                    ll_ac_tv.setVisibility(View.VISIBLE);
                    ll_ac_tv.setWeightSum(2);
                    sp_type.setVisibility(View.VISIBLE);
                    gst_percent = 18;
                    et_gst.setText(gst_percent + "");

                    ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(this,
                            R.array.tele_type, android.R.layout.simple_spinner_item);
                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // attaching data adapter to spinner
                    sp_type.setAdapter(dataAdapter);


                } else if (position == 2 && sp_sub_category.getSelectedItem().equals("Refrigerator")) {
                    ll_mobile.setVisibility(View.GONE);
                    ll_power_bank.setVisibility(View.GONE);
                    ll_wired_ear_phones.setVisibility(View.GONE);
                    ll_television.setVisibility(View.GONE);
                    ll_refregerator.setVisibility(View.VISIBLE);
                    ll_ac_tv.setVisibility(View.VISIBLE);
                    ll_ac_tv.setWeightSum(1);
                    sp_type.setVisibility(View.GONE);

                    ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(this,
                            R.array.refre_type, android.R.layout.simple_spinner_item);
                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // attaching data adapter to spinner
                    sp_type.setAdapter(dataAdapter);
                    gst_percent = 18;
                    et_gst.setText(gst_percent + "");

                } else {
                    hideAndResetAllVariantValues();
                }
                break;

        }
    }


    private void hideAndResetAllVariantValues() {

        ll_mobile.setVisibility(View.GONE);
        ll_power_bank.setVisibility(View.GONE);
        ll_wired_ear_phones.setVisibility(View.GONE);
        ll_television.setVisibility(View.GONE);
        ll_refregerator.setVisibility(View.GONE);
        ll_ac_tv.setVisibility(View.GONE);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_add_submit:

                if (validation()) {

                    if (ll_mobile.isShown()) {
                        if (mobileValidation()) {
                            addProductList(postValues());
                        }

                    } else if (ll_power_bank.isShown()) {
                        if (powerBankValidation()) {
                            addProductList(postValues());
                        }

                    } else if (ll_refregerator.isShown()) {
                        if (refregeratorValidation()) {
                            addProductList(postValues());

                        }

                    } else if (ll_wired_ear_phones.isShown()) {
                        if (wiredEarPhoneValidation()) {
                            addProductList(postValues());
                        }

                    } else if (ll_television.isShown()) {
                        if (televisionValidation()) {
                            addProductList(postValues());
                        }

                    }

                }

                break;
            case R.id.im_scan:
                Intent scanIntent = new Intent(AddProductSubmit.this, ScanActivity.class);
                startActivityForResult(scanIntent, REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String requiredValue = data.getStringExtra("Key");
                et_serial_number.setText(requiredValue);
            }
        }
    }

    private void addProductList(String data) {
        pb_dialogue.setVisibility(View.VISIBLE);
        new ProductApiHelper().addProduct(data, new IApiRequestComplete<AddProductResponse>() {
            @Override
            public void onSuccess(AddProductResponse response) {
                if (response != null) {
                    clearAllEditField();
                    hideAndResetAllVariantValues();
                    pb_dialogue.setVisibility(View.GONE);
                    // Utility.showToast(getApplicationContext(),response.getMessage());

                    dialog.setIcon(R.drawable.pdlg_icon_success, R.color.pdlg_color_green,null)   // Icon resource
                            .setTitle(getResources().getString(R.string.success))
                            .setMessage(response.getMessage())
                            .addButton(getResources().getString(R.string.ok), R.color.pdlg_color_white, R.color.pdlg_color_green, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialog.dismiss();
                                }
                            });
                    dialog.show();
                }
            }

            @Override
            public void onFailure(String message) {
                pb_dialogue.setVisibility(View.GONE);
                dialog.setIcon(R.drawable.pdlg_icon_info, R.color.pdlg_color_green,null)
                        .setTitle(getResources().getString(R.string.error))
                        .setMessage(message)
                        .addButton(getResources().getString(R.string.ok), R.color.pdlg_color_white, R.color.pdlg_color_green, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                dialog.dismiss();
                            }
                        });
                dialog.show();

            }
        });

    }

    private boolean televisionValidation() {
        boolean status = false;
        if (sp_type.getSelectedItemPosition() != 0) {
            if (sp_television_star.getSelectedItemPosition() != 0) {
                return true;

            } else {
                Utility.showToast(getApplicationContext(), getResources().getString(R.string.error_star_rating));
            }

        } else {
            Utility.showToast(getApplicationContext(), getResources().getString(R.string.error_type));
        }

        return status;
    }

    private boolean wiredEarPhoneValidation() {
        boolean status = false;
        if (sp_wire_phone_color.getSelectedItemPosition() != 0) {
            return true;
        } else {
            Utility.showToast(getApplicationContext(), getResources().getString(R.string.error_color));
        }
        return status;
    }

    private boolean refregeratorValidation() {
        boolean status = false;
        if (sp_star_refregerator.getSelectedItemPosition() != 0) {
            if (sp_refregerator_color.getSelectedItemPosition() != 0) {
                if (sp_refregerator_capacity.getSelectedItemPosition() != 0) {
                    return true;

                } else {
                    Utility.showToast(getApplicationContext(), getResources().getString(R.string.error_capacity));
                }

            } else {
                Utility.showToast(getApplicationContext(), getResources().getString(R.string.error_color));
            }

        } else {
            Utility.showToast(getApplicationContext(), getResources().getString(R.string.error_star_rating));
        }

        return status;
    }

    private boolean powerBankValidation() {
        boolean status = false;
        if (sp_power_mah.getSelectedItemPosition() != 0) {
            if (sp_power_compatible.getSelectedItemPosition() != 0) {
                if (!Validation.isTextEmpty(et_weight_battery.getText().toString().trim())) {

                    return true;

                } else {
                    til_weight_battery.setError(getResources().getString(R.string.error_weight));
                }

            } else {
                Utility.showToast(getApplicationContext(), getResources().getString(R.string.error_compatible));
            }

        } else {
            Utility.showToast(getApplicationContext(), getResources().getString(R.string.error_battery_mah));
        }

        return status;
    }

    private boolean mobileValidation() {
        boolean status = false;

        if (sp_variant.getSelectedItemPosition() != 0) {
            if (sp_color.getSelectedItemPosition() != 0) {
                if (sp_camera_pixel.getSelectedItemPosition() != 0) {

                    return true;

                } else {
                    Utility.showToast(getApplicationContext(), getResources().getString(R.string.error_camera_pixel));
                }

            } else {
                Utility.showToast(getApplicationContext(), getResources().getString(R.string.error_color));
            }

        } else {
            Utility.showToast(getApplicationContext(), getResources().getString(R.string.error_variant));
        }

        return status;
    }

    private boolean validation() {
        boolean status = false;
        if (sp_product_type.getSelectedItemPosition() != 0) {
            if (sp_category.getSelectedItemPosition() != 0) {
                if (sp_sub_category.getSelectedItemPosition() != 0) {
                    if (!Validation.isTextEmpty(et_brand_name.getText().toString().trim())) {
                        if (!Validation.isTextEmpty(et_model.getText().toString().trim())) {
                            if (!Validation.isTextEmpty(et_quantity.getText().toString().trim())) {
                                if (!Validation.isTextEmpty(et_description.getText().toString().trim())) {
                                    return true;
                                } else {
                                    til_description.setError(getResources().getString(R.string.error_description));
                                }
                            } else {
                                til_quantity.setError(getResources().getString(R.string.error_quantity));
                            }

                        } else {
                            til_model.setError(getResources().getString(R.string.error_model));
                        }
                    } else {
                        til_brand_name.setError(getResources().getString(R.string.error_brand_name));
                    }

                } else {
                    Utility.showToast(getApplicationContext(), getResources().getString(R.string.error_sub_categories));
                }

            } else {
                Utility.showToast(getApplicationContext(), getResources().getString(R.string.error_categories));
            }

        } else {
            Utility.showToast(getApplicationContext(), getResources().getString(R.string.error_product_type));
        }


        return status;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {


    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (et_model.getText().hashCode() == s.hashCode() && !Validation.isTextEmpty(et_model.getText().toString())) {
            til_model.setError(null);
        } else if (et_brand_name.getText().hashCode() == s.hashCode() && !Validation.isTextEmpty(et_brand_name.getText().toString().trim())) {
            til_brand_name.setError(null);
        } else if (et_quantity.getText().hashCode() == s.hashCode() && !Validation.isTextEmpty(et_quantity.getText().toString().trim())) {
            til_quantity.setError(null);
        } else if (et_description.getText().hashCode() == s.hashCode() && !Validation.isTextEmpty(et_description.getText().toString().trim())) {
            til_description.setError(null);
        } else if (et_excl_gst.getText().hashCode() == s.hashCode() && !Validation.isTextEmpty(et_excl_gst.getText().toString().trim())) {

            double gst_price, base_price, total_price = 0.0;
            base_price = Double.parseDouble(et_excl_gst.getText().toString());
            gst_price = base_price * gst_percent / 100;
            total_price = base_price + gst_price;
            tv_est_price.setText(total_price + " " + "Rs.");

        }else if (et_weight_battery.getText().hashCode()==s.hashCode()&&Validation.isTextEmpty(et_weight_battery.getText().toString().trim())){
            til_weight_battery.setError(null);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void clearAllEditField() {
        et_brand_name.setText("");
        et_model.setText("");
        et_quantity.setText("");
        et_description.setText("");
        et_gst.setText("");
        et_excl_gst.setText("");
        et_serial_number.setText("");
        et_special_feature_refri.setText("");
        et_special_feature.setText("");
        et_weight_battery.setText("");
        et_include_components.setText("");
        et_product_dimention.setText("");
        et_mobile_weight.setText("");
        tv_est_price.setText("-");
    }

    private String postValues() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("product_name", "Samsung");
            jsonObject.put("product_description", "Smart Inverter AC 1.5");
            jsonObject.put("product_quantity", 20);
            jsonObject.put("product_price", 29048);
            jsonObject.put("product_category", "Home Appliances");
            jsonObject.put("product_sub_category", "AC");
            jsonObject.put("product_category_id", 12);
            jsonObject.put("product_sub_category_id", 2);
            jsonObject.put("product_image_url", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }
}
