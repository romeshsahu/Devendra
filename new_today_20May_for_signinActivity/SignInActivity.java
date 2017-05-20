package com.ealpha.drawer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ealpha.R;
import com.ealpha.main.MainActivity;
import com.ps.DTO.AddressDTO;
import com.ps.DTO.CartsDTO;
import com.ps.DTO.UserDTO;
import com.ps.utility.JSONParser;
import com.ps.utility.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SignInActivity extends Activity {

    private SessionManager sessionManager;
    private UserDTO userDTO;
    // /
    EditText email1, password1;
    Button login;
    String email_mail, name, title, fname, lname, email, phone, city, state,
            country, password, client_id;
    TextView forgot_password, skip;
    JSONObject json;
    JsonParser jParser = new JsonParser();
    TextView text_btn_signup_now;

    CheckBox check_showpass_login;
    private ArrayList<String> wishListIds;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        initialize();
    }

    public void initialize() {
        MainActivity.view_pagination_index = 1;
        sessionManager = new SessionManager(this);
        login = (Button) findViewById(R.id.imd_login);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        skip = (TextView) findViewById(R.id.skip);
        skip.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });

        email1 = (EditText) findViewById(R.id.edit_text_email);
        email1.setHint(Html
                .fromHtml("<font color='#FFFAFA'>Email Address</font> "));

        password1 = (EditText) findViewById(R.id.edit_text_password);
        password1.setHint(Html
                .fromHtml("<font color='#FFFAFA'>Password</font> "));
        check_showpass_login = (CheckBox)
                findViewById(R.id.check_showpass_login);

        if (!check_showpass_login.isChecked()) {
            password1.setTransformationMethod(PasswordTransformationMethod
                    .getInstance());
        } else {
            password1.setTransformationMethod(HideReturnsTransformationMethod
                    .getInstance());

        }
        check_showpass_login
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        // TODO Auto-generated method stub
                        if (!isChecked) {
                            // show password
                            password1
                                    .setTransformationMethod(PasswordTransformationMethod
                                            .getInstance());
                        } else {
                            // hide password
                            password1
                                    .setTransformationMethod(HideReturnsTransformationMethod
                                            .getInstance());
                        }

                    }
                });

        text_btn_signup_now = (TextView) findViewById(R.id.tv_sign_up);
        text_btn_signup_now.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent_signup_now = new Intent(SignInActivity.this,
                        SignUp.class);
                startActivity(intent_signup_now);
            }

            private void finish() {
                // TODO Auto-generated method stub

            }
        });

        forgot_password = (TextView)
                findViewById(R.id.forgot_password);
        forgot_password.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent1 = new Intent(SignInActivity.this, ForgotPassword.class);
                startActivity(intent1);
                finish();
            }
        });

        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                email_mail = email1.getText().toString();
                password = password1.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    password1.setError(getResources().getString(
                            R.string.error_field_required));
                } else if (password.length() < 3) {
                    password1.setError(getString(R.string.error_invalid));
                } else if (password.length() > 12) {
                    password1
                            .setError(getString(R.string.error_invalid_password));
                }
                if (TextUtils.isEmpty(email1.getText().toString())) {
                    email1.setError(getResources().getString(
                            R.string.email_is_required));
                } else if (!Function.isEmailValid(email1.getText().toString())) {
                    email1.setError(getString(R.string.error_invalid_email));
                } else {
                    new SignInAsyncTask()
                            .execute("http://ealpha.com/mob/customers.php?customers=login");
                }
            }
        });
    }


    class SignInAsyncTask extends AsyncTask<String, Void, JSONObject> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(SignInActivity.this);
            dialog.setMessage("Please Wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", email_mail));
            params.add(new BasicNameValuePair("password", password));
            JSONObject json = new JSONParser().makeHttpRequest2(args[0],
                    "POST", params);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            String vStatus = "", vMessage = "In-correct Username or Passowrd, use right details.";
            dialog.dismiss();
            try {
                JSONObject customer_login_object = null;
                try {
                    System.out.println("response..." + jsonObject.toString());
                    customer_login_object = jsonObject
                            .getJSONObject("customer_login");
                    vStatus = customer_login_object.getString("status");
                    vMessage = customer_login_object.getString("message");
                } catch (Exception e) {
                    // TODO: handle exception
                }
                if (vStatus.trim().equals("Success")) {
                    try {
                        JSONObject customer_details_object = customer_login_object
                                .getJSONObject("customer_details");
                        userDTO = new UserDTO();
                        userDTO.setFirst_name(customer_details_object
                                .getString("firstname"));
                        userDTO.setLast_name(customer_details_object
                                .getString("lastname"));
                        userDTO.setEmail(customer_details_object
                                .getString("email"));
                        userDTO.setPassword(password);
                        userDTO.setCustomer_id(customer_details_object
                                .getString("id_customer"));
                        sessionManager.setUserDetail(userDTO);
                        sessionManager.login();
                        Toast.makeText(SignInActivity.this, "User logged in.",
                                Toast.LENGTH_SHORT).show();
                        MainActivity.drawer_update = 2;
                        finish();
                        new getCartAsyncTask()
                                .execute("http://www.ealpha.com/mob/customers.php?customers=view_cart&id_customer="
                                        + sessionManager.getUserDetail()
                                        .getCustomer_id());
                        new getUserAddressAsyncTask()
                                .execute("http://ealpha.com/webservice/webservice.php?action=addresses&module=get");
                        new getAllWishListAsyncTask()
                                .execute("http://www.ealpha.com/mob/customers.php?customers=wishlist_view");
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                } else {
                    Toast.makeText(SignInActivity.this, vMessage, Toast.LENGTH_SHORT)
                            .show();
                }
            } catch (Exception e) {

            }
        }
    }

    class getCartAsyncTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            JSONObject json = new JSONParser().makeHttpRequest2(args[0], "GET",
                    params);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            String vStatus = "", vCart_Id = "";
            try {
                System.out.println("get_all_cart..." + jsonObject.toString());
                JSONObject customer_view_cart = jsonObject
                        .getJSONObject("customer_view_cart");
                vStatus = customer_view_cart.getString("status");
                if (vStatus.trim().equals("Success")) {
                    JSONObject cart_dataObject = customer_view_cart
                            .getJSONObject("message")
                            .getJSONObject("cart_data");
                    vCart_Id = cart_dataObject.getString("id_cart");
                    JSONArray id_product = cart_dataObject
                            .getJSONArray("id_product");
                    JSONArray product_name = cart_dataObject
                            .getJSONArray("product_name");
                    JSONArray product_img = cart_dataObject
                            .getJSONArray("product_img");
                    JSONArray quantity = cart_dataObject
                            .getJSONArray("quantity");
                    JSONArray total_price = cart_dataObject
                            .getJSONArray("total_price");
                    JSONArray unit_price = cart_dataObject
                            .getJSONArray("unit_price");
                    JSONArray product_link = cart_dataObject
                            .getJSONArray("product_link");
                    JSONArray availability = cart_dataObject
                            .getJSONArray("availability");
                    if (id_product.length() > 0) {
                        ArrayList<CartsDTO> cartsDTOs = new ArrayList<CartsDTO>();
                        CartsDTO cartsDTO;
                        for (int i = 0; i < id_product.length(); i++) {
                            cartsDTO = new CartsDTO();
                            cartsDTO.setId_cart(vCart_Id);
                            cartsDTO.setId_product(id_product.getString(i));
                            cartsDTO.setProduct_name(product_name.getString(i));
                            cartsDTO.setUnit_price(""
                                    + (int) Float.parseFloat(unit_price
                                    .getString(i)));
                            cartsDTO.setTotal_price(total_price.getString(i));
                            cartsDTO.setProduct_img(product_img.getString(i));
                            cartsDTO.setProduct_link(product_link.getString(i));
                            try {
                                cartsDTO.setQuantity(Integer.parseInt(quantity
                                        .getString(i)));
                            } catch (Exception e) {

                            }
                            cartsDTO.setAvailability(availability.getString(i));
                            cartsDTOs.add(cartsDTO);
                        }
                        sessionManager.setCartsDTOs(cartsDTOs);
                    }
                }
            } catch (Exception e) {

            }
        }
    }

    class getUserAddressAsyncTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_customer", sessionManager
                    .getUserDetail().getCustomer_id()));
            System.out.println("params.." + params.toString());
            JSONObject json = new JSONParser().makeHttpRequest2(args[0],
                    "POST", params);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            String vStatus = "", vAddressId = "";
            try {
                JSONObject address_dataObjcet = jsonObject
                        .getJSONObject("address_data");
                vStatus = address_dataObjcet.getString("status");
                if (vStatus.equals("success")) {
                    JSONObject addressesObject = address_dataObjcet
                            .getJSONObject("details")
                            .getJSONObject("addresses");
                    try {
                        vAddressId = addressesObject
                                .getJSONObject("address").getJSONObject("@attributes").getString("id");
                        System.out.println("oooooooooooooo");
                        new getUserAddressDetailAsyncTask()
                                .execute("http://ealpha.com/webservice/webservice.php?action=addresses&module=get&id="
                                        + vAddressId);
                    } catch (Exception e) {

                    }
                    try {
                        JSONArray addressArray = addressesObject
                                .getJSONArray("address");
                        JSONObject addressIDObject = addressArray.getJSONObject(
                                addressArray.length() - 1).getJSONObject(
                                "@attributes");
                        vAddressId = addressIDObject.getString("id");
                        new getUserAddressDetailAsyncTask()
                                .execute("http://ealpha.com/webservice/webservice.php?action=addresses&module=get&id="
                                        + vAddressId);
                    } catch (Exception e) {

                    }
                }
            } catch (JSONException e) {
                // TODO: handle exception
            }
        }
    }

    class getUserAddressDetailAsyncTask extends
            AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            System.out.println("params.." + params.toString());
            JSONObject json = new JSONParser().makeHttpRequest2(args[0],
                    "POST", params);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            String vStatus = "";
            try {
                JSONObject address_dataObjcet = jsonObject
                        .getJSONObject("address_data");
                vStatus = address_dataObjcet.getString("status");
                if (vStatus.equals("success")) {
                    JSONObject addressObject = address_dataObjcet
                            .getJSONObject("details").getJSONObject("address");
                    AddressDTO addressDTO = new AddressDTO();
                    addressDTO.setCust_fname(addressObject
                            .getString("firstname"));
                    addressDTO.setCust_lname(addressObject
                            .getString("lastname"));
                    addressDTO.setCust_address(addressObject
                            .getString("address1"));
                    addressDTO.setCust_mobile_no(addressObject
                            .getString("phone"));
                    addressDTO.setPin_code(addressObject.getString("postcode"));
                    addressDTO.setLocality(addressObject.getString("alias"));
                    addressDTO.setCity(addressObject.getString("city"));
                    addressDTO.setState("1");
                    addressDTO.setCompany(addressObject.getString("company"));
                    addressDTO.setId_country("110");
                    addressDTO.setId_address_delivery(addressObject
                            .getString("id"));
                    addressDTO.setId_address_invoice(addressObject
                            .getString("id"));
                    sessionManager.setCustomerDetail(addressDTO);
                    ArrayList<AddressDTO> addressDTOs = new ArrayList<>();
                    addressDTOs.add(addressDTO);
                    sessionManager.setAddressList(addressDTOs);
                }
            } catch (JSONException e) {
                // TODO: handle exception
            }
        }
    }

    class getAllWishListAsyncTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_customer", sessionManager
                    .getUserDetail().getCustomer_id()));
            JSONObject json = new JSONParser().makeHttpRequest2(args[0],
                    "POST", params);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            String vStatus = "", vMessage = "No Wishlist Data Available.";
            JSONArray wishlist_data_array = null;
            try {
                MainActivity.wish_p_ids = new ArrayList<String>();
                try {
                    JSONObject customer_wishlist_object = jsonObject
                            .getJSONObject("view_wishlist_data");
                    vStatus = customer_wishlist_object.getString("status");
                    wishlist_data_array = customer_wishlist_object
                            .getJSONObject("message").getJSONArray(
                                    "wishlist_data");
                } catch (Exception e) {
                    // TODO: handle exception
                }
                if (vStatus.trim().equals("Success")) {
                    try {
                        int total_data = 0;
                        if (wishlist_data_array.length() == 0) {
                            total_data = 0;
                        } else {
                            total_data = wishlist_data_array.length() - 1;
                        }
                        JSONObject wishlist_data_object = wishlist_data_array
                                .getJSONObject(total_data);
                        try {
                            JSONArray product_link_array = wishlist_data_object
                                    .getJSONArray("product_link");
                            for (int j = 0; j < product_link_array.length(); j++) {
                                MainActivity.wish_p_ids
                                        .add(product_link_array
                                                .getString(j)
                                                .replace(
                                                        "http://ealpha.com//mob/customers.php?get_data=product_data&product_id=",
                                                        ""));
                            }
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            } catch (Exception e) {
            }
            if (MainActivity.mainActivity != null) {
                // MainActivity.mainActivity
                // .setBadgeOnCart(MainActivity.wish_p_ids.size());
            }
        }
    }

}