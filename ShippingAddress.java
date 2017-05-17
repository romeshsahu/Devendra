package com.ealpha.cart;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ealpha.R;
import com.ps.DTO.AddressDTO;
import com.ps.utility.JSONParser;
import com.ps.utility.SessionManager;

public class ShippingAddress extends Activity {
    private AddressDTO addressDTO;
    private TextView tv_customer_fname, tv_customer_lname, tv_customer_address,
            tv_mobile_no, tv_post_code, tv_state, tv_city_district;
    private EditText tvv_customer_fname, tvv_customer_lname,
            tvv_customer_address, tvv_mobile_no, tvv_post_code, tvv_state,
            tvv_city_district;
    private Button btn_edit, btn_checkout;
    private static TextView tv_sub_total_rs, tv_total_order_rs,
            tv_shipping_charge, tv_apply_code_get;
    private SessionManager sessionManager;
    private LinearLayout add_new_address;
    final Context context = this;
    private Button button, add_new_add, btn_add_new_address;
    private EditText result;
    private String vVoucher = "";
    private boolean isCheck = false;
    private TextView show_all_address;
    public static ShippingAddress shippingAddress;
    int shipping_charge = 0;
    private boolean is_voucher_applied = false;
    private LinearLayout address_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_shipping_detail);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        is_voucher_applied = false;
        shippingAddress = this;
        initialize();
    }

    public void initialize() {
        tv_customer_fname = (TextView) findViewById(R.id.tv_customer_fname);
        tv_customer_lname = (TextView) findViewById(R.id.tv_customer_lname);
        tv_customer_address = (TextView) findViewById(R.id.tv_customer_address);
        tv_mobile_no = (TextView) findViewById(R.id.tv_mobile_no);
        tv_post_code = (TextView) findViewById(R.id.tv_post_code);
        tv_state = (TextView) findViewById(R.id.tv_state);
        tv_city_district = (TextView) findViewById(R.id.tv_city_district);
        address_main = (LinearLayout) findViewById(R.id.address_main);
        tvv_customer_fname = (EditText) findViewById(R.id.tvv_customer_fname);
        tvv_customer_lname = (EditText) findViewById(R.id.tvv_customer_lname);
        tvv_customer_address = (EditText) findViewById(R.id.tvv_customer_address);
        tvv_mobile_no = (EditText) findViewById(R.id.tvv_mobile_no);
        tvv_post_code = (EditText) findViewById(R.id.tvv_post_code);
        tvv_state = (EditText) findViewById(R.id.tvv_state);
        tvv_city_district = (EditText) findViewById(R.id.tvv_city_district);

        tv_sub_total_rs = (TextView) findViewById(R.id.tv_sub_total_rs);
        tv_total_order_rs = (TextView) findViewById(R.id.tv_total_order_rs);
        tv_shipping_charge = (TextView) findViewById(R.id.tv_shipping_chage_rs);
        btn_edit = (Button) findViewById(R.id.btn_edit_address);
        add_new_add = (Button) findViewById(R.id.add_new_add);
        btn_add_new_address = (Button) findViewById(R.id.btn_add_new_address);
        add_new_address = (LinearLayout) findViewById(R.id.add_new_address);
        show_all_address = (TextView) findViewById(R.id.show_all_address);
        add_new_address.setVisibility(View.GONE);
        show_all_address.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startActivity(new Intent(ShippingAddress.this,
                        ShippingAddressListActivity.class));
            }
        });
        add_new_add.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (isCheck) {
                    add_new_address.setVisibility(View.GONE);
                    isCheck = false;
                } else {
                    add_new_address.setVisibility(View.VISIBLE);
                    isCheck = true;
                }
            }
        });

        tv_apply_code_get = (TextView) findViewById(R.id.tv_apply_code_get);
        tv_apply_code_get.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (is_voucher_applied) {
                    Toast.makeText(context, "Voucher Already Applied.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li
                        .inflate(R.layout.apply_voucher_code, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        // get user input and set it to result
                                        // edit text
                                        if (userInput.getText().toString()
                                                .trim().length() == 0) {

                                        } else {
                                            vVoucher = userInput.getText()
                                                    .toString().trim();
                                            // http://www.ealpha.com/mob/customers.php?customers=check_voucher&id_customer=8150&voucher=BAG05&amount=949&products=10736
                                            new ApplyCodeAsyncTask()
                                                    .execute("http://www.ealpha.com/mob/customers.php?customers=check_voucher");
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });
        btn_checkout = (Button) findViewById(R.id.btn_checkout);
        sessionManager = new SessionManager(this);
        setCustomeDetail();
        try {
            ArrayList<AddressDTO> addressDTOs = new ArrayList<AddressDTO>();
            try {
                addressDTOs = sessionManager.getAddressList();
            } catch (Exception e) {
                // TODO: handle exception
            }
            show_all_address.setText(addressDTOs.size() + ",Saved");
        } catch (Exception e) {
            // TODO: handle exception

        }
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_add_list = new Intent(ShippingAddress.this,
                        ShippingAddressListActivity.class);
                startActivity(intent_add_list);
                // saveCustomerDetail();
            }
        });
        btn_add_new_address.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                saveMoreCustomerDetail();
            }
        });
        btn_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean is_address_added = false;
                try {
                    if (sessionManager.getAddressList().size() == 0) {
                        is_address_added = false;
                        Toast.makeText(ShippingAddress.this,
                                "Please enter address detail.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        is_address_added = true;
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
                try {
                    if (is_address_added) {
//                        Toast.makeText(context, "" + CartFragment.total_amount,
//                                Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ShippingAddress.this,
                                PaymentOptions.class));
                    } else {
                        Toast.makeText(ShippingAddress.this,
                                "Please enter address detail.",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        });
        tv_sub_total_rs.setText(CartFragment.total_amount + "");
        tv_total_order_rs.setText(CartFragment.total_amount + "");
        new ShippingChargeAsyncTask()
                .execute("http://ealpha.com/mob/customers.php");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCustomeDetail();
    }

    public void saveCustomerDetail() {
        addressDTO = new AddressDTO();
        addressDTO.setCust_fname(tv_customer_fname.getText().toString().trim());
        addressDTO.setCust_lname(tv_customer_lname.getText().toString().trim());
        addressDTO.setCust_address(tv_customer_address.getText().toString()
                .trim());
        addressDTO.setCust_mobile_no(tv_mobile_no.getText().toString().trim());
        addressDTO.setPin_code(tv_post_code.getText().toString().trim());
        addressDTO.setLocality(tv_state.getText().toString().trim());
        addressDTO.setCity(tv_city_district.getText().toString().trim());
        sessionManager.setCustomerDetail(addressDTO);
        ArrayList<AddressDTO> addressDTOs = new ArrayList<AddressDTO>();
        try {
            addressDTOs = sessionManager.getAddressList();
        } catch (Exception e) {
            // TODO: handle exception
        }
        if (addressDTOs == null) {
            addressDTOs = new ArrayList<>();
        }
        addressDTOs.add(addressDTO);
        sessionManager.setAddressList(addressDTOs);
        show_all_address.setText(addressDTOs.size() + ",Saved");
        new AddAddressAsyncTask()
                .execute("http://ealpha.com/webservice/webservice.php");
        // new AddAddressAsyncTask()
        // .execute("http://ealpha.com/webservice/webservice.php?address1=Indore&address1=Indore&module=add&module=add&firstname=Krishna&firstname=Krishna&company=PS&company=PS&id_state=1&id_state=1&id_country=1&id_country=1&lastname=Chouhan&lastname=Chouhan&postcode=452003&postcode=452003&alias=indore&alias=indore&id_customer=8150&id_customer=8150&action=addresses&action=addresses&city=Indore&city=Indore&phone=1234567896&phone=1234567896");
    }

    public void saveMoreCustomerDetail() {
        if (tvv_customer_fname.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Enter Customer first Name.",
                    Toast.LENGTH_SHORT).show();
        } else if (tvv_customer_lname.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Enter Customer last name.",
                    Toast.LENGTH_SHORT).show();

        } else if (tvv_customer_address.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Enter Customer Address.", Toast.LENGTH_SHORT)
                    .show();
        } else if (tvv_mobile_no.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Enter Mobile No.", Toast.LENGTH_SHORT).show();
        } else if (tvv_post_code.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Enter Pin Code.", Toast.LENGTH_SHORT).show();
        } else if (tvv_state.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Enter Address.", Toast.LENGTH_SHORT).show();
        } else if (tvv_city_district.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Enter City.", Toast.LENGTH_SHORT).show();
        } else {
            addressDTO = new AddressDTO();
            addressDTO.setCust_fname(tvv_customer_fname.getText().toString()
                    .trim());
            addressDTO.setCust_lname(tvv_customer_lname.getText().toString()
                    .trim());
            addressDTO.setCust_address(tvv_customer_address.getText()
                    .toString().trim());
            addressDTO.setCust_mobile_no(tvv_mobile_no.getText().toString()
                    .trim());
            addressDTO.setPin_code(tvv_post_code.getText().toString().trim());
            addressDTO.setLocality(tvv_state.getText().toString().trim());
            addressDTO.setCity(tvv_city_district.getText().toString().trim());
            sessionManager.setCustomerDetail(addressDTO);
            ArrayList<AddressDTO> addressDTOs = new ArrayList<AddressDTO>();
            try {
                addressDTOs = sessionManager.getAddressList();
            } catch (Exception e) {
                // TODO: handle exception
            }
            if (addressDTOs == null) {
                addressDTOs = new ArrayList<AddressDTO>();
            }
            addressDTOs.add(addressDTO);
            sessionManager.setAddressList(addressDTOs);
            show_all_address.setText(addressDTOs.size() + ",Saved");
            Toast.makeText(ShippingAddress.this, "New Address Added.",
                    Toast.LENGTH_SHORT).show();
            setCustomeDetail();
            add_new_add.performClick();
            if (add_new_address.getVisibility() == View.VISIBLE) {
                add_new_address.setVisibility(View.GONE);
            }
            new AddAddressAsyncTask()
                    .execute("http://ealpha.com/webservice/webservice.php");
        }
    }

    public void setCustomeDetail() {
        try {
            if (sessionManager.getCustomeDetail() != null) {
                address_main.setVisibility(View.VISIBLE);
                add_new_address.setVisibility(View.GONE);
                tv_customer_fname.setText(sessionManager.getCustomeDetail()
                        .getCust_fname());
                tv_customer_lname.setText(sessionManager.getCustomeDetail()
                        .getCust_lname());
                tv_customer_address.setText(sessionManager.getCustomeDetail()
                        .getCust_address());
                tv_mobile_no.setText(sessionManager.getCustomeDetail()
                        .getCust_mobile_no());
                tv_post_code.setText(sessionManager.getCustomeDetail()
                        .getPin_code());
                tv_state.setText(sessionManager.getCustomeDetail()
                        .getLocality());
                tv_city_district.setText(sessionManager.getCustomeDetail()
                        .getCity());
                CartFragment.id_address_delivery = sessionManager
                        .getCustomeDetail().getId_address_delivery();
                CartFragment.id_address_invoice = sessionManager
                        .getCustomeDetail().getId_address_invoice();
                try {
                    show_all_address.setText(sessionManager.getAddressList()
                            .size() + ",Saved");
                } catch (Exception e) {
                    // TODO: handle exception
                }
            } else {
                address_main.setVisibility(View.GONE);
                add_new_address.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            address_main.setVisibility(View.GONE);
            add_new_address.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class AddAddressAsyncTask extends AsyncTask<String, Void, JSONObject> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ShippingAddress.this);
            dialog.setMessage("Please Wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // http://ealpha.com/webservice/webservice.php?module=add&address1=Indore&id_country=1&firstname=Krishna&company=PS&id_state=1&lastname=Chouhan&postcode=452003&alias=indore&phone=1234567896&action=addresses&city=Indore&id_customer=8150
            params.add(new BasicNameValuePair("action", "addresses"));
            params.add(new BasicNameValuePair("module", "add"));
            params.add(new BasicNameValuePair("id_customer", sessionManager
                    .getUserDetail().getCustomer_id()));
            params.add(new BasicNameValuePair("firstname", sessionManager
                    .getCustomeDetail().getCust_fname()));
            params.add(new BasicNameValuePair("lastname", sessionManager
                    .getCustomeDetail().getCust_lname()));
            params.add(new BasicNameValuePair("address1", sessionManager
                    .getCustomeDetail().getCust_address()));
            params.add(new BasicNameValuePair("city", sessionManager
                    .getCustomeDetail().getCity()));
            params.add(new BasicNameValuePair("id_country", "110"));
            params.add(new BasicNameValuePair("alias", sessionManager
                    .getCustomeDetail().getLocality()));
            params.add(new BasicNameValuePair("postcode", sessionManager
                    .getCustomeDetail().getPin_code()));
            params.add(new BasicNameValuePair("phone", sessionManager
                    .getCustomeDetail().getCust_mobile_no()));
            params.add(new BasicNameValuePair("id_state", "1"));
            params.add(new BasicNameValuePair("company", "PS"));
            Log.d("mTitel", "params=" + params);
            JSONObject json = new JSONParser().makeHttpRequest2(args[0], "GET",
                    params);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            String vStatus = "", vMessage = "not removed.";
            dialog.dismiss();
            try {
                try {
                    System.out.println("address responce..."
                            + jsonObject.toString());
                    // {"address_data":{"status":"success","message":"Address Added Successfully","Address_Id":{"0":"11389"}}}
                    if (jsonObject.has("address_data")) {
                        JSONObject address_dataObject = jsonObject
                                .getJSONObject("address_data");

                        if (address_dataObject.has("status")) {
                            if (address_dataObject.getString("status").equals(
                                    "success")) {
                                if (address_dataObject.has("message")) {
                                    Toast.makeText(
                                            ShippingAddress.this,
                                            ""
                                                    + address_dataObject
                                                    .getString("message"),
                                            Toast.LENGTH_SHORT).show();
                                    if (address_dataObject.has("Address_Id")) {
                                        JSONObject addressObject = address_dataObject
                                                .getJSONObject("Address_Id");
                                        if (addressObject != null) {
                                            if (addressObject.length() > 0) {
                                                CartFragment.id_address_delivery = addressObject
                                                        .getString("0");
                                                CartFragment.id_address_invoice = addressObject
                                                        .getString("0");
                                                Toast.makeText(
                                                        ShippingAddress.this,
                                                        "Address_ID:="
                                                                + CartFragment.id_address_delivery,
                                                        Toast.LENGTH_SHORT)
                                                        .show();
                                                AddressDTO addressDTO = sessionManager
                                                        .getCustomeDetail();
                                                addressDTO
                                                        .setId_address_delivery(addressObject
                                                                .getString("0"));
                                                addressDTO
                                                        .setId_address_invoice(addressObject
                                                                .getString("0"));
                                                sessionManager
                                                        .setCustomerDetail(addressDTO);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    // TODO: handle exception
                }
            } catch (Exception e) {

            }
        }
    }

    // http://www.ealpha.com/mob/customers.php?customers=check_voucher&id_customer=8084&voucher=Sairandhri10&amount=20000&products=10250
    class ApplyCodeAsyncTask extends AsyncTask<String, Void, JSONObject> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ShippingAddress.this);
            dialog.setMessage("Please Wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // params.add(new BasicNameValuePair("id_customer", "8150"));
            // params.add(new BasicNameValuePair("voucher", "BAG05"));
            // params.add(new BasicNameValuePair("amount", "949"));
            // params.add(new BasicNameValuePair("products", "10736"));
            params.add(new BasicNameValuePair("id_customer", sessionManager
                    .getUserDetail().getCustomer_id()));
            params.add(new BasicNameValuePair("voucher", vVoucher));
            params.add(new BasicNameValuePair("amount",
                    CartFragment.total_amount + ""));
            params.add(new BasicNameValuePair("id_cart",
                    CartFragment.id_cart + ""));
            params.add(new BasicNameValuePair("products",
                    CartFragment.vProduct_ids.toString().trim()));
            Log.d("mTitel", "params=" + params);
            JSONObject json = new JSONParser().makeHttpRequest2(args[0],
                    "POST", params);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            String vStatus = "", vMessage = "";
            dialog.dismiss();
            try {
                try {
                    try {
                        System.out.println("apply code responce..."
                                + jsonObject.toString());
                    } catch (Exception e) {

                    }
                    // {"voucher":{"status":"Success","message":"Voucher Applied Successfully.","data":{"cart_rule_id":"44","description":"5 % off on Bag","free_shipping":"0","reduction_percent":"5.00","reduction_amount":"0.00"}}}
                    JSONObject voucherObject = jsonObject
                            .getJSONObject("voucher");
                    vStatus = voucherObject.getString("status");
                    if (vStatus.trim().equals("Success")) {
                        vMessage = voucherObject.getString("message");
                        if (vMessage.equals("Voucher Applied Successfully.")) {
                            JSONObject dataObject = voucherObject
                                    .getJSONObject("data");
                            String reduction_percent = dataObject
                                    .getString("reduction_percent");
                            String free_shipping = dataObject
                                    .getString("free_shipping");
                            float reduction_per = Float
                                    .parseFloat(reduction_percent);
                            float per = CartFragment.total_amount
                                    * (reduction_per / 100);
                            try {
                                CartFragment.total_discount = (int) per + "";
                            } catch (Exception e) {

                            }
                            try {
                                CartFragment.free_shipping = free_shipping;
                            } catch (Exception e) {

                            }
                            CartFragment.total_amount = CartFragment.total_amount
                                    - per;
                            tv_total_order_rs.setText(CartFragment.total_amount + "");
                            is_voucher_applied = true;
                            // + CartFragment.shipping_charge);
                            Toast.makeText(ShippingAddress.this,
                                    "Voucher Applied Successfully.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else if (vStatus.trim().equals("Error")) {
                        vMessage = voucherObject.getString("message");
                        Toast.makeText(ShippingAddress.this, vMessage,
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
            } catch (Exception e) {

            }
        }
    }

    public class ShippingChargeAsyncTask extends
            AsyncTask<String, Void, JSONObject> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = new ProgressDialog(ShippingAddress.this);
            dialog.setMessage("Please Wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            // TODO Auto-generated method stub
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("customers", "cod"));
            params.add(new BasicNameValuePair("cod_amount",
                    CartFragment.total_amount + ""));
            JSONObject json = new JSONParser().makeHttpRequest2(args[0],
                    "POST", params);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            // TODO Auto-generated method stub
            super.onPostExecute(jsonObject);
            dialog.dismiss();
            // {"cod_result":{"status":"Success","message":0}}
            String vStatus = "";
            String vMessage = "";

            try {
                System.out.println("shipping_charge_response..."
                        + jsonObject.toString());
                JSONObject cod_resultObject = jsonObject
                        .getJSONObject("cod_result");
                vStatus = cod_resultObject.getString("status");
                if (vStatus.trim().equals("Success")) {
                    vMessage = cod_resultObject.getString("message");
                    try {
                        shipping_charge = Integer.parseInt(vMessage);
                    } catch (Exception e) {

                    }
                    if (shipping_charge > 0) {
                        try {
                            CartFragment.shipping_charge = shipping_charge + "";
                        } catch (Exception e) {

                        }
                        tv_total_order_rs.setText(CartFragment.total_amount
                                + "");
                    } else {
                        shipping_charge = 0;
                        try {
                            CartFragment.shipping_charge = shipping_charge + "";
                        } catch (Exception e) {

                        }
                        tv_total_order_rs.setText(CartFragment.total_amount
                                + "");
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }
}
