package com.ealpha.homeclick;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ealpha.R;
import com.ealpha.drawer.SignInActivity;
import com.ealpha.main.MainActivity;
import com.ealpha.support.ImageGalleryProductAdapter;
import com.ps.DTO.CartColorSizeDTO;
import com.ps.DTO.CartsDTO;
import com.ps.DTO.SliderDTO;
import com.ps.utility.JSONParser;
import com.ps.utility.SessionManager;

public class ProductDetailActivityPP extends Activity {
    private TextView btn_back, txt_headline, txt_price, txt_description,
            txt_color, txt_size, txt_id, txt_availability;
    private ViewPager viewpager;
    private ProgressDialog progressDialog;
    private ArrayList<SliderDTO> sliderDTOs;
    private SliderDTO sliderDTO;
    private String product_link_popular = "";
    private Button btn_wish_list;
    private SessionManager sessionManager;
    private String vProduct_ID = "";
    private LinearLayout show_size_view, show_color_view;
    private ArrayList<String> sizes, color_codes, color_names;
    private int id_size = -1;
    private boolean is_same_size;
    String default_color_code = "#000000";
    private TextView mCounter;
    private String vKeyValue = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popular_product_deatail_activity);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        initialize();
    }

    public void initialize() {
        // TODO Auto-generated method stub
        viewpager = (ViewPager) findViewById(R.id.viewpager_pp);
        btn_back = (TextView) findViewById(R.id.btn_back);
        txt_headline = (TextView) findViewById(R.id.txt_headline);
        txt_price = (TextView) findViewById(R.id.txt_rs);
        txt_description = (TextView) findViewById(R.id.txt_product_description);

        txt_color = (TextView) findViewById(R.id.tv_color1);
        txt_size = (TextView) findViewById(R.id.tv_size1);
        btn_wish_list = (Button) findViewById(R.id.btn_to_cart_list);
        show_size_view = (LinearLayout) findViewById(R.id.show_size_view);
        show_color_view = (LinearLayout) findViewById(R.id.show_color_view);
        sessionManager = new SessionManager(this);
        sizes = new ArrayList<String>();
        color_codes = new ArrayList<String>();
        color_names = new ArrayList<String>();
        try {
            product_link_popular = getIntent().getStringExtra(
                    "product_link_popular");
            System.out
                    .println("product_link_popular..." + product_link_popular);
        } catch (Exception e) {
            // TODO: handle exception
        }
        btn_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait");
        progressDialog.show();
        btn_wish_list.setText("Add to Cart");
        try {
            vProduct_ID = product_link_popular
                    .replace(
                            "http://ealpha.com//mob/customers.php?get_data=product_data&product_id=",
                            "");
            if (isProductAddedIntoCart(vProduct_ID)) {
                btn_wish_list.setText("Remove from Cart");
            } else {
                btn_wish_list.setText("Add to Cart");
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        btn_wish_list.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!sessionManager.isLogin()) {
                    Toast.makeText(ProductDetailActivityPP.this,
                            "User not logged in.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ProductDetailActivityPP.this, SignInActivity.class));
                    return;
                }
                vProduct_ID = product_link_popular
                        .replace(
                                "http://ealpha.com//mob/customers.php?get_data=product_data&product_id=",
                                "");
                if (btn_wish_list.getText().toString().trim()
                        .equals("Add to Cart")) {
                    btn_wish_list.setText("Remove from Cart");
                    new addToCartAsyncTask()
                            .execute("http://www.ealpha.com/mob/customers.php?customers=add_to_cart");
                } else if (btn_wish_list.getText().toString().trim()
                        .equals("Remove from Cart")) {
                    btn_wish_list.setText("Add to Cart");
                    new addToCartAsyncTask()
                            .execute("http://www.ealpha.com/mob/customers.php?customers=remove_add_to_cart");
                }
            }
        });
        // new popular_p_AsynchTask().execute("http://ealpha.com//mob/customers.php?get_data=product_data&product_id=8323");
        new popular_p_AsynchTask().execute(product_link_popular);
    }

    public boolean isProductAddedIntoCart(String vProduct_ID) {
        boolean isAddedInoCart = false;
        ArrayList<CartsDTO> cartsDTOs = new ArrayList<CartsDTO>();
        try {
            cartsDTOs = sessionManager.getCartsDTOs();
        } catch (Exception e) {
            // TODO: handle exception
        }
        for (int i = 0; i < cartsDTOs.size(); i++) {
            if (cartsDTOs.get(i).getId_product().equals(vProduct_ID)) {
                isAddedInoCart = true;
                break;
            }
        }
        return isAddedInoCart;
    }

    private void removeProductFromCart(String vProduct_id) {
        // TODO Auto-generated method stub
        ArrayList<CartsDTO> cartsDTOs = new ArrayList<CartsDTO>();
        try {
            cartsDTOs = sessionManager.getCartsDTOs();
        } catch (Exception e) {
            // TODO: handle exception
        }
        for (int i = 0; i < cartsDTOs.size(); i++) {
            if (cartsDTOs.get(i).getId_product().equals(vProduct_id)) {
                cartsDTOs.remove(i);
                break;
            }
        }
        sessionManager.setCartsDTOs(cartsDTOs);
    }

    private void addCartcolorSize(String vCart_id, String vColor, String vSize) {
        // TODO Auto-generated method stub
        ArrayList<CartColorSizeDTO> cartColorSizeDTOs = new ArrayList<CartColorSizeDTO>();
        try {
            cartColorSizeDTOs = sessionManager.getCartColorSizeListIds();
        } catch (Exception e) {
            // TODO: handle exception
        }
        CartColorSizeDTO cartColorSizeDTO = new CartColorSizeDTO();
        cartColorSizeDTO.setCart_id(vCart_id);
        cartColorSizeDTO.setColor(vColor);
        cartColorSizeDTO.setSize(vSize);
        cartColorSizeDTOs.add(cartColorSizeDTO);
        sessionManager.setCartColorSizeListIds(cartColorSizeDTOs);
        // Toast.makeText(ProductDetailActivityPP.this,
        // "" + sessionManager.getCartColorSizeListIds().size(),
        // Toast.LENGTH_SHORT).show();
    }

    private void removeCartcolorSize(String vCart_id, String vColor,
                                     String vSize) {
        // TODO Auto-generated method stub
        ArrayList<CartColorSizeDTO> cartColorSizeDTOs = new ArrayList<CartColorSizeDTO>();
        try {
            cartColorSizeDTOs = sessionManager.getCartColorSizeListIds();
        } catch (Exception e) {
            // TODO: handle exception
        }
        if (cartColorSizeDTOs.size() > 0) {
            for (int i = 0; i < cartColorSizeDTOs.size(); i++) {
                if (cartColorSizeDTOs.get(i).getCart_id().equals(vCart_id)) {
                    cartColorSizeDTOs.remove(i);
                    break;
                }
            }
            sessionManager.setCartColorSizeListIds(cartColorSizeDTOs);
            // Toast.makeText(ProductDetailActivityPP.this, "RRRR",
            // Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isAddedInoCart(String vCart_id) {
        boolean isAddedInoCart = false;
        ArrayList<CartColorSizeDTO> cartColorSizeDTOs = new ArrayList<CartColorSizeDTO>();
        try {
            cartColorSizeDTOs = sessionManager.getCartColorSizeListIds();
        } catch (Exception e) {
            // TODO: handle exception
        }
        for (int i = 0; i < cartColorSizeDTOs.size(); i++) {
            if (cartColorSizeDTOs.get(i).getCart_id().equals(vCart_id)) {
                isAddedInoCart = true;
                break;
            }
        }
        return isAddedInoCart;
    }

    class popular_p_AsynchTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            JSONObject json = new JSONParser().makeHttpRequest2(args[0],
                    "POST", params);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            progressDialog.dismiss();
            // ----------for slider--------------
            try {
                System.out.println("product_link_popular..."
                        + jsonObject.toString());
                JSONObject productDataObject = jsonObject
                        .getJSONObject("product_data");
                try {
                    txt_headline.setText(productDataObject.getString("name"));
                } catch (Exception e) {
                    // TODO: handle exception
                }
                try {
                    txt_price.setText(productDataObject
                            .getString("final_price").replaceAll("\\.0*$", ""));
                    // tum is try catch me jo bhi value nikalna hai set kr sakte
                    // ho okay
                } catch (Exception e) {
                    // TODO: handle exception
                }
                try {
                    txt_description.setText(productDataObject
                            .getString("description"));
                    // tum is try catch me jo bhi value nikalna hai set kr sakte
                    // ho okay
                } catch (Exception e) {
                    // TODO: handle exception
                }
                try {
                    txt_availability.setText(productDataObject
                            .getString("product_quantity"));
                    // tum is try catch me jo bhi value nikalna hai set kr sakte
                    // ho okay
                } catch (Exception e) {
                    // TODO: handle exception
                }
                try {
                    JSONObject product_attribute = productDataObject.getJSONObject("product_attribute");
                    Iterator<String> iterator = product_attribute.keys();
                    if (iterator != null) {
                        if (iterator.hasNext()) {
                            // is key value ko add kar lena jab add to cart kro to okay
                            vKeyValue = iterator.next();
                            System.out.println("vKeyValue..." + vKeyValue);
                        }
                    }
                    if (vKeyValue != null) {
                        if (vKeyValue.length() > 0) {
                            JSONObject attributeObject = product_attribute.getJSONArray(vKeyValue).getJSONObject(0);
                            if (attributeObject.getString("attribute_name").equals("Color")) {
                                System.out.println("Value..." + attributeObject.getString("value"));
                                System.out.println("Color..." + attributeObject.getString("color"));
                                color_names.add(attributeObject.getString("value"));
                                color_codes.add(attributeObject.getString("color"));
                            }
                        }
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }

                try {
                    JSONArray size = productDataObject.getJSONArray("size");
                    for (int i = 0; i < size.length(); i++) {
                        sizes.add(size.getString(i));
                    }
                } catch (Exception e) {

                }
                try {
                    JSONArray color_name = productDataObject
                            .getJSONArray("color");
                    for (int i = 0; i < color_name.length(); i++) {
                        color_names.add(color_name.getString(i));
                    }
                } catch (Exception e) {

                }
                try {
                    JSONArray color_code = productDataObject
                            .getJSONArray("color_code");
                    for (int i = 0; i < color_code.length(); i++) {
                        color_codes.add(color_code.getString(i));
                    }
                } catch (Exception e) {

                }
                try {
                    MainActivity.cartsDTO.setSize(sizes.get(0));
                    MainActivity.cartsDTO.setColor_code(color_codes.get(0));
                    MainActivity.cartsDTO.setColor_name(color_names.get(0));
                } catch (Exception e) {

                }
                // for slider on top
                JSONObject sliderImageObject = productDataObject.getJSONArray(
                        "product_image").getJSONObject(0);
                sliderDTOs = new ArrayList<SliderDTO>();
                try {
                    JSONArray product_img_default = sliderImageObject
                            .getJSONArray("product_img_default");
                    for (int i = 0; i < product_img_default.length(); i++) {
                        sliderDTO = new SliderDTO();
                        sliderDTO.setSlider_image(product_img_default
                                .getString(i));
                        sliderDTOs.add(sliderDTO);
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
                setSlider();
            } catch (Exception e) {
                // TODO: handle exception
            }
            try {
                setSize();
                setColors();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public void setSlider() {
        viewpager.setAdapter(new ImageGalleryProductAdapter(this, sliderDTOs));
        viewpager.postDelayed(new Runnable() {
            int i = 0;

            @Override
            public void run() {
                if (i == sliderDTOs.size()) {
                    i = 0;
                }
                viewpager.setCurrentItem(i);
                i++;
                // viewpager.postDelayed(this, 2000);
            }
        }, 5000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.product_detail, menu);
        RelativeLayout badgeLayout = (RelativeLayout) menu.findItem(
                R.id.action_cart).getActionView();
        mCounter = (TextView) badgeLayout.findViewById(R.id.counter);
        setBadgeOnCartTest();
        badgeLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                MainActivity.mainActivity.selectCart(13);
                finish();
            }
        });
        return true;
    }

    public void setBadgeOnCart(int cart_counter) {
        try {
            if (cart_counter > 0) {
                mCounter.setText(cart_counter + "");
            } else {
                mCounter.setText("");
            }
        } catch (Exception e) {

        }
    }

    public void setBadgeOnCartTest() {
        int menu_cart_counter = 0;
        try {
            menu_cart_counter = sessionManager.getCartsDTOs().size();
        } catch (Exception e) {

        }
        if (sessionManager.isLogin()) {
            setBadgeOnCart(menu_cart_counter);
        } else {
            setBadgeOnCart(0);
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

    class addToCartAsyncTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_customer", sessionManager
                    .getUserDetail().getCustomer_id()));
            params.add(new BasicNameValuePair("id_product", vProduct_ID));
            params.add(new BasicNameValuePair("quantity", "1"));
            if (vKeyValue == null) {
                vKeyValue = "";
            }
            System.out.println("vKeyValue...." + vKeyValue);
            // "yeha par parameter name likh lena sir"
            // params.add(new BasicNameValuePair("yeha par parameter name likh lena sir", vKeyValue));
            System.out.println("params..." + params.toString());
            // [id_customer=8057, id_product=9572, quantity=1]
            JSONObject json = new JSONParser().makeHttpRequest2(args[0],
                    "POST", params);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            String vStatus = "";
            String vMessage = "";
            try {
                // {"add_to_cart":{"message":"Product successfully added to your shopping cart","status":"Success"}}
                // {"customer_remove_add_to_cart":{"status":"Success","message":"Product Remove successfully"}}
                System.out.println("add_to_cart..." + jsonObject.toString());
                JSONObject add_to_cart = null;
                if (jsonObject.has("add_to_cart")) {
                    add_to_cart = jsonObject.getJSONObject("add_to_cart");
                    vStatus = add_to_cart.getString("status");
                    String vCartId = add_to_cart
                            .getString("message")
                            .replace(
                                    "Product successfully added to your shopping cart, Your Cart Id Is : ",
                                    "");
                    if (vCartId != null) {
                        if (vCartId.trim().length() > 0) {
                            MainActivity.cartsDTO.setId_cart(vCartId);
                            ArrayList<CartsDTO> cartsDTOs = new ArrayList<>();
                            try {
                                cartsDTOs = sessionManager.getCartsDTOs();
                            } catch (Exception e) {

                            }
                            if (cartsDTOs == null) {
                                cartsDTOs = new ArrayList<>();
                            }
                            if (cartsDTOs.size() > 0) {
                                cartsDTOs = sessionManager.getCartsDTOs();
                                cartsDTOs.add(MainActivity.cartsDTO);
                                sessionManager.setCartsDTOs(cartsDTOs);
                            } else {
                                cartsDTOs.add(MainActivity.cartsDTO);
                                sessionManager.setCartsDTOs(cartsDTOs);
                            }
                        } else {
                            MainActivity.cartsDTO.setId_cart(vCartId);
                            ArrayList<CartsDTO> cartsDTOs = new ArrayList<>();
                            try {
                                cartsDTOs = sessionManager.getCartsDTOs();
                            } catch (Exception e) {

                            }
                            if (cartsDTOs == null) {
                                cartsDTOs = new ArrayList<>();
                            }
                            if (cartsDTOs.size() > 0) {
                                cartsDTOs = sessionManager.getCartsDTOs();
                                cartsDTOs.add(MainActivity.cartsDTO);
                                sessionManager.setCartsDTOs(cartsDTOs);
                            } else {
                                cartsDTOs.add(MainActivity.cartsDTO);
                                sessionManager.setCartsDTOs(cartsDTOs);
                            }
                        }
                    }
                    vMessage = "item added into cart.";
                } else if (jsonObject.has("customer_remove_add_to_cart")) {
                    add_to_cart = jsonObject
                            .getJSONObject("customer_remove_add_to_cart");
                    vMessage = "item removed from cart.";
                    vStatus = add_to_cart.getString("status");
                    removeProductFromCart(vProduct_ID);
                }
                if (vStatus.trim().equals("Success")) {
                    Toast.makeText(ProductDetailActivityPP.this, vMessage,
                            Toast.LENGTH_SHORT).show();
                } else if (vStatus.trim().equals("Error")) {
                    Toast.makeText(ProductDetailActivityPP.this, vMessage,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductDetailActivityPP.this,
                            "item not added into cart.", Toast.LENGTH_SHORT)
                            .show();
                }
            } catch (Exception e) {

            }
            setBadgeOnCartTest();
        }

    }

    public void setSize() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        params.setMargins(10, 10, 10, 10);
        for (int i = 0; i < sizes.size(); i++) {

            final TextView size_text = new TextView(this);
            size_text.setLayoutParams(params);
            size_text.setPadding(10, 10, 10, 10);
            try {
                size_text.setText(Integer.parseInt(sizes.get(i)) + "");
            } catch (Exception e) {
                if (sizes.get(i).contains("Short")) {
                    size_text.setText("S");
                } else if (sizes.get(i).contains("Medium")) {
                    size_text.setText("M");
                } else if (sizes.get(i).contains("Long")) {
                    size_text.setText("L");
                } else if (sizes.get(i).contains("XL : Extra large")) {
                    size_text.setText("XL");
                } else if (sizes.get(i).contains("XXL : Extra Extra Large")) {
                    size_text.setText("XXL");
                } else if (sizes.get(i).contains(
                        "XXXL : Extra Extra Extra Large")) {
                    size_text.setText("XXXL");
                } else {
                    size_text.setText(sizes.get(i) + "");
                }
            }

            size_text.setAllCaps(true);
            size_text.setGravity(Gravity.CENTER);
            size_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            size_text.setBackgroundColor(Color.parseColor("#D3D3D3"));
            size_text.setId(i);
            size_text.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        MainActivity.cartsDTO.setSize(sizes.get(v.getId()));
                    } catch (Exception e) {

                    }
                    if (id_size == -1) {
                        id_size = v.getId();
                        size_text.setBackgroundColor(Color
                                .parseColor("#E3E3E3"));
                    } else {
                        TextView textView = (TextView) show_size_view
                                .findViewById(id_size);
                        textView.setBackgroundColor(Color.parseColor("#D3D3D3"));
                        if (id_size == v.getId()) {
                            if (is_same_size) {
                                is_same_size = false;
                            } else {
                                is_same_size = true;
                                return;
                            }
                        }
                        id_size = v.getId();
                        size_text.setBackgroundColor(Color
                                .parseColor("#E3E3E3"));
                    }
                }
            });
            show_size_view.addView(size_text);
        }
    }

    public void setColors() {
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                new ViewGroup.LayoutParams(50, 50));
        params.setMargins(10, 10, 10, 10);
        for (int i = 0; i < color_codes.size(); i++) {
            final TextView size_text = new TextView(this);
            size_text.setLayoutParams(params);
            size_text.setPadding(10, 10, 10, 10);
            size_text.setGravity(Gravity.CENTER);
            size_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            try {
                default_color_code = color_codes.get(i);
            } catch (Exception e) {

            }
            size_text.setBackgroundColor(Color.parseColor(default_color_code));
            size_text.setId(i);
            size_text.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewpager.setCurrentItem(v.getId());
                    Toast.makeText(ProductDetailActivityPP.this,
                            "Color Selected.", Toast.LENGTH_SHORT).show();
                }
            });
            show_color_view.addView(size_text);
        }
    }

}
