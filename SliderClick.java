package com.ealpha.homeclick;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ealpha.R;
import com.ealpha.main.MainActivity;
import com.ps.DTO.CartsDTO;
import com.ps.DTO.ProductDTO;
import com.ps.utility.ConnectionDetector;
import com.ps.utility.JSONParser;
import com.ps.utility.SessionManager;

public class SliderClick extends Activity {
    GridView grid_view;
    ImageView image;
    TextView name;
    String product_link;
    public static int pdt_position = 0;
    public static ArrayList<ProductDTO> product_list;
    AdapterClickBannerSlider pAdapter;
    private ConnectionDetector connectionDetector;
    private String vProduct_id = "";
    private SessionManager sessionManager;

    private ProgressDialog progressDialog;
    private ProductDTO productDTO;
    private String vProduct_ID = "";
    public static SliderClick sliderClick;
    private int page_counter = 0;
    private boolean is_more_items = true;
    private String link = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdt_main);
        sliderClick = this;
        //SliderClick.sliderClick = null;
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // imageLoader = new ImageLoader(this);
        Intent intent = getIntent();

        link = intent.getStringExtra("slider_link");
        sessionManager = new SessionManager(this);
        is_more_items = true;
        page_counter = 0;
        grid_view = (GridView) findViewById(R.id.grid_view);
        product_list = new ArrayList<ProductDTO>();
        pAdapter = new AdapterClickBannerSlider(this, product_list);
        grid_view.setAdapter(pAdapter);
        if (!isInternet()) {
            Toast.makeText(
                    SliderClick.this,
                    "No internet connection, please try with internet connection.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        getProducts();
        grid_view.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                int threshold = 1;
                int count = grid_view.getCount();

                if (scrollState == SCROLL_STATE_IDLE) {
                    if (grid_view.getLastVisiblePosition() >= count - threshold) {
                        // Execute LoadMoreDataTask AsyncTask
                        link = link.split("&start_limit=")[0] + "&start_limit="
                                + page_counter + "&end_limit=20";
                        System.out.println("link......" + link);
                        if (is_more_items) {
                            getProducts();
                        } else {
                            Toast.makeText(SliderClick.this, "No more items.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub

            }
        });
        grid_view
                .setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        pdt_position = position;

                        MainActivity.cartsDTO = new CartsDTO();
                        System.out.println("id_product...."
                                + product_list.get(position).getProduct_id());
                        System.out.println("product_name...."
                                + product_list.get(position).getProduct_name());
                        System.out
                                .println("unit_price...."
                                        + product_list.get(position)
                                        .getProduct_price());
                        System.out.println("product_img...."
                                + product_list.get(position).getProduct_img());
                        System.out.println("product_link...."
                                + product_list.get(position).getProduct_link());
                        System.out.println("availability...."
                                + product_list.get(position).getAvailability());

                        MainActivity.cartsDTO.setId_product(product_list.get(
                                position).getProduct_id());
                        MainActivity.cartsDTO.setProduct_name(product_list.get(
                                position).getProduct_name());
                        MainActivity.cartsDTO.setUnit_price(product_list.get(
                                position).getProduct_price());
                        MainActivity.cartsDTO.setTotal_price(product_list.get(
                                position).getProduct_price());
                        MainActivity.cartsDTO.setProduct_img(product_list.get(
                                position).getProduct_img());
                        MainActivity.cartsDTO.setProduct_link(product_list.get(
                                position).getProduct_link());
                        MainActivity.cartsDTO.setQuantity(1);
                        MainActivity.cartsDTO.setAvailability(product_list.get(
                                position).getAvailability());

                        Intent intent_popular_grid = new Intent(
                                getApplicationContext(),
                                ProductDetailActivitySlider.class);

                        intent_popular_grid.putExtra("link",
                                product_list.get(position).getProduct_link());
                        startActivity(intent_popular_grid);
                    }

                });

        // product_list = new ArrayList<ProductDTO>();

		/*
         * new JSONAsynTask().execute(link);
		 *
		 * progressDialog = new ProgressDialog(this);
		 * progressDialog.setIndeterminate(true);
		 * progressDialog.setTitle("Please wait");
		 * progressDialog.setCancelable(true);
		 * progressDialog.setMessage("Please wait"); progressDialog.show();
		 */
    }

    public void getProducts() {
        new JSONAsynTask().execute(link);
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait");
        progressDialog.show();
    }

    class JSONAsynTask extends AsyncTask<String, Void, JSONObject> {
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
            try {
                System.out.println("jsonObjectKrishna..." + link + "......"
                        + jsonObject.toString());
                try {
                    if (jsonObject.getJSONObject("category_data")
                            .getString("product_id").equals("null")) {
                        Toast.makeText(SliderClick.this, "No more items.",
                                Toast.LENGTH_SHORT).show();
                        is_more_items = false;
                        return;
                    }
                } catch (Exception e) {

                }
                // ----------for slider--------------

                JSONArray productNameArray = jsonObject.getJSONObject(
                        "category_data").getJSONArray("product_name");

                JSONArray productImageArray = jsonObject.getJSONObject(
                        "category_data").getJSONArray("image");

                JSONArray productPriceArray = jsonObject.getJSONObject(
                        "category_data").getJSONArray("price");
                JSONArray productDescriptionArray = jsonObject.getJSONObject(
                        "category_data").getJSONArray("product_description");

                JSONArray productLinkArray = jsonObject.getJSONObject(
                        "category_data").getJSONArray("link");

                // product_list = new ArrayList<ProductDTO>();
                for (int i = 0; i < productNameArray.length(); i++) {
                    productDTO = new ProductDTO();
                    productDTO
                            .setProduct_id(productLinkArray
                                    .getString(i)
                                    .replace(
                                            "http://ealpha.com//mob/customers.php?get_data=product_data&product_id=",
                                            ""));
                    productDTO.setProduct_name(productNameArray.getString(i));
                    productDTO.setProduct_img(productImageArray.getString(i));
                    productDTO.setProduct_price(productPriceArray.getString(i));
                    productDTO.setProduct_description(productDescriptionArray
                            .getString(i));
                    productDTO.setProduct_link(productLinkArray.getString(i));
                    if (sessionManager.isLogin()) {
                        if (MainActivity.wish_p_ids.contains(productDTO
                                .getProduct_id())) {
                            productDTO.setWish_to_list(true);
                        } else {
                            productDTO.setWish_to_list(false);
                        }
                    } else {
                        productDTO.setWish_to_list(false);
                    }
                    product_list.add(productDTO);
                }
                pAdapter.notifyDataSetChanged();
                // setGridData();
            } catch (Exception e) {
                // TODO: handle exception
            }

        }

    }

    public void setGridData() {
        //pAdapter = new AdapterClickBannerSlider(this, product_list);
        //grid_view.setAdapter(pAdapter);
    }

    public @interface Nullable {

    }

    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub

    }

    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub

            view.loadUrl(url);
            return true;

        }
    }

    public boolean isInternet() {
        connectionDetector = new ConnectionDetector(SliderClick.this);
        // Check if Internet present
        return connectionDetector.isConnectingToInternet();
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

    public void wistToList(int position) {
        if (!sessionManager.isLogin()) {
            Toast.makeText(SliderClick.this, "User not logged in.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        vProduct_ID = product_list.get(position).getProduct_id();
        if (product_list.get(position).isWish_to_list()) {
            product_list.get(position).setWish_to_list(false);
            MainActivity.wish_p_ids.remove(vProduct_ID);
            MainActivity.wish_p_ids = new ArrayList<String>(
                    new LinkedHashSet<String>(MainActivity.wish_p_ids));
            pAdapter.notifyDataSetChanged();
            new addAndRemoveWishListAsyncTask()
                    .execute("http://www.ealpha.com/mob/customers.php?customers=wishlist_remove");
        } else {
            product_list.get(position).setWish_to_list(true);
            MainActivity.wish_p_ids.add(vProduct_ID);
            MainActivity.wish_p_ids = new ArrayList<String>(
                    new LinkedHashSet<String>(MainActivity.wish_p_ids));
            pAdapter.notifyDataSetChanged();
            new addAndRemoveWishListAsyncTask()
                    .execute("http://www.ealpha.com/mob/customers.php?customers=wishlist_add");
        }
    }

    class addAndRemoveWishListAsyncTask extends
            AsyncTask<String, Void, JSONObject> {

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
            System.out.println("params..." + params.toString());
            JSONObject json = new JSONParser().makeHttpRequest2(args[0],
                    "POST", params);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            String vMessage = "";
            String vStatus = "";
            try {
                // {"wishlist_added_data":{"status":"Success","message":"The product was successfully added to your wishlist","wishlist_id":14}}
                // {"wishlist_remove":{"status":"Success","message":"Wishlist Removed "}}
                System.out.println("wish list..." + jsonObject.toString());
                JSONObject wishlist_added_data = null;
                if (jsonObject.has("wishlist_added_data")) {
                    wishlist_added_data = jsonObject
                            .getJSONObject("wishlist_added_data");
                    vStatus = wishlist_added_data.getString("status");
                    // vMessage = wishlist_added_data.getString("message");
                    vMessage = "item added into wishlist.";
                } else if (jsonObject.has("wishlist_remove")) {
                    wishlist_added_data = jsonObject
                            .getJSONObject("wishlist_remove");
                    vStatus = wishlist_added_data.getString("status");
                    vMessage = "item removed from wishlist.";
                }
                if (vStatus.trim().equals("Success")) {
                    Toast.makeText(SliderClick.this, "" + vMessage,
                            Toast.LENGTH_SHORT).show();
                } else if (vStatus.trim().equals("Error")) {
                    Toast.makeText(SliderClick.this, "" + vMessage,
                            Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {

            }
        }
    }
}
