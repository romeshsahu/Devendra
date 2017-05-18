package com.ealpha.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.ealpha.R;
import com.ealpha.homeclick.AdapterClickBannerSlider;
import com.ealpha.homeclick.DrawerItemsProductDetailActivity;
import com.ealpha.support.ProductAdapterGridChild;
import com.ps.DTO.CartsDTO;
import com.ps.DTO.ProductDTO;
import com.ps.utility.JSONParser;
import com.ps.utility.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class ChildItemsFragment extends Fragment {
    private SessionManager sessionManager;
    private String vProduct_ID = "";
    public static ChildItemsFragment childItemsFragment;
    private boolean is_more_items = true;
    private int page_counter = 0;

    public ChildItemsFragment(String vurl) {
        // Toast.makeText(getActivity(), "" + vurl, Toast.LENGTH_SHORT).show();
        this.vDefault_Child_Item_URL = vurl;
        if (vDefault_Child_Item_URL == null
                || vDefault_Child_Item_URL.trim().length() == 0) {
            vDefault_Child_Item_URL = "http://ealpha.com/mob/customers.php?get_data=category_data&category_id=207&start_limit=0&end_limit=12";
        } else {
            try {
                vDefault_Child_Item_URL = MainActivity.keyItems
                        .get(vDefault_Child_Item_URL);
                Toast.makeText(getActivity(), vDefault_Child_Item_URL,
                        Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                // TODO: handle exception
                if (vDefault_Child_Item_URL == null
                        || vDefault_Child_Item_URL.trim().length() == 0) {
                    vDefault_Child_Item_URL = "http://ealpha.com/mob/customers.php?get_data=category_data&category_id=207&start_limit=0&end_limit=12";
                }
            }
        }
    }

    ChildItemsFragment fragment;
    private static String TAG_FRAGMENT = "HOME_FRAGMENT";
    public static int pdt_positionDrawerItemsClick = 0;
    public static int fragment_position = 0;
    private GridView grid_product_type;
    public static ArrayList<ProductDTO> productArrayList;
    // public static ArrayList<ProductDTO> product_list_BannerClick;
    private ProductDTO productDTO;
    private ProgressDialog progressDialog;
    private ProductAdapterGridChild productAdapterGridChild;
    boolean doubleBackToExitPressedOnce = false;
    private String vDefault_Child_Item_URL = "http://ealpha.com/mob/customers.php?get_data=category_data&category_id=207&start_limit=0&end_limit=12";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_child_items_grid,
                container, false);
        MainActivity.view_pagination_index = 1;

        initialize(rootView);
        return rootView;
    }

    private void initialize(View view) {
        // TODO Auto-generated method stub
        childItemsFragment = this;
        HomeFragment.homeFragment = null;
        is_more_items = true;
        page_counter = 0;
        sessionManager = new SessionManager(getActivity());
        grid_product_type = (GridView) view
                .findViewById(R.id.grid_product_type);
        productArrayList = new ArrayList<ProductDTO>();
        productAdapterGridChild = new ProductAdapterGridChild(getActivity(),
                productArrayList);
        grid_product_type.setAdapter(productAdapterGridChild);
        getProducts();
        grid_product_type.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int threshold = 1;
                int count = grid_product_type.getCount();

                if (scrollState == SCROLL_STATE_IDLE) {
                    if (grid_product_type.getLastVisiblePosition() >= count - threshold) {
                        // Execute LoadMoreDataTask AsyncTask
                        vDefault_Child_Item_URL = vDefault_Child_Item_URL.split("&start_limit=")[0] + "&start_limit="
                                + page_counter + "&end_limit=20";
                        System.out.println("link......" + vDefault_Child_Item_URL);
                        if (is_more_items) {
                            getProducts();
                        } else {
                            Toast.makeText(getActivity(), "No more items.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        grid_product_type
                .setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) { // TODO Auto-generated
                        pdt_positionDrawerItemsClick = position; // method stub
                        MainActivity.cartsDTO = new CartsDTO();
                        System.out.println("id_product...."
                                + productArrayList.get(position)
                                .getProduct_id());
                        System.out.println("product_name...."
                                + productArrayList.get(position)
                                .getProduct_name());
                        System.out.println("unit_price...."
                                + productArrayList.get(position)
                                .getProduct_price());
                        System.out.println("product_img...."
                                + productArrayList.get(position)
                                .getProduct_img());
                        System.out.println("product_link...."
                                + productArrayList.get(position)
                                .getProduct_link());
                        System.out.println("availability...."
                                + productArrayList.get(position)
                                .getAvailability());
                        MainActivity.cartsDTO.setId_product(productArrayList
                                .get(position).getProduct_id());
                        MainActivity.cartsDTO.setProduct_name(productArrayList
                                .get(position).getProduct_name());
                        MainActivity.cartsDTO.setUnit_price(productArrayList
                                .get(position).getProduct_price());
                        MainActivity.cartsDTO.setTotal_price(productArrayList
                                .get(position).getProduct_price());
                        MainActivity.cartsDTO.setProduct_img(productArrayList
                                .get(position).getProduct_img());
                        MainActivity.cartsDTO.setProduct_link(productArrayList
                                .get(position).getProduct_link());
                        MainActivity.cartsDTO.setQuantity(1);
                        MainActivity.cartsDTO.setAvailability(productArrayList
                                .get(position).getAvailability());

                        Intent intent_popular_grid = new Intent(getActivity(),
                                DrawerItemsProductDetailActivity.class);
                        intent_popular_grid.putExtra("link", productArrayList
                                .get(position).getProduct_link());
                        startActivity(intent_popular_grid);
                    }
                });
    }

    public void getProducts() {
        if (vDefault_Child_Item_URL != null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("Please wait");
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait");
            progressDialog.show();
            new childItemsAsynchTask().execute(vDefault_Child_Item_URL);
        }
    }

    public void searchOnChildFragment(String vSearcText) {
        if (vSearcText.trim().length() > 0) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("Please wait");
            progressDialog.setCancelable(true);
            progressDialog.setMessage("Please wait");
            progressDialog.show();
            new childItemsAsynchTask()
                    .execute("http://www.ealpha.com/mob/customers.php?customers=search_api&search="
                            + vSearcText + "&start_limit=1&end_limit=100");
        } else {

        }
    }

    class childItemsAsynchTask extends AsyncTask<String, Void, JSONObject> {
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
            String vStatus = "";
            // ----------for child item grid view--------------
            try {
                System.out.println("jsonObject...." + jsonObject.toString());
                JSONObject category_data_Object = jsonObject
                        .getJSONObject("category_data");
                vStatus = category_data_Object.getString("status");
                if (vStatus.trim().equals("Error")) {
                    /*
                     * Toast.makeText(getActivity(), "No Products were found",
					 * Toast.LENGTH_SHORT).show();
					 */
                    Toast.makeText(getActivity(), "No Products were found",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
            try {
                try {
                    if (jsonObject.getJSONObject("category_data")
                            .getString("product_id").equals("null")) {
                        Toast.makeText(getActivity(), "No more items.",
                                Toast.LENGTH_SHORT).show();
                        is_more_items = false;
                        return;
                    }
                } catch (Exception e) {

                }
                JSONObject category_data_Object = jsonObject
                        .getJSONObject("category_data");
                JSONArray productNameArray = category_data_Object
                        .getJSONArray("product_name");
                page_counter = page_counter + 20;
                JSONArray productImageArray = category_data_Object
                        .getJSONArray("image");
                JSONArray productPriceArray = category_data_Object
                        .getJSONArray("price");
                JSONArray productLinkArray = category_data_Object
                        .getJSONArray("link");
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
                    productArrayList.add(productDTO);
                }
                setGridData();
            } catch (Exception e) {
                // TODO: handle exception
            }
            try {
                JSONArray productNameArray = jsonObject
                        .getJSONObject("search_result").getJSONArray("message")
                        .getJSONObject(0).getJSONArray("name");

                JSONArray productImageArray = jsonObject
                        .getJSONObject("search_result").getJSONArray("message")
                        .getJSONObject(0).getJSONArray("product_img");

                JSONArray productPriceArray = jsonObject
                        .getJSONObject("search_result").getJSONArray("message")
                        .getJSONObject(0).getJSONArray("product_price");

                JSONArray productLinkArray = jsonObject
                        .getJSONObject("search_result").getJSONArray("message")
                        .getJSONObject(0).getJSONArray("product_link");
                for (int i = 0; i < productNameArray.length(); i++) {
                    productDTO = new ProductDTO();
                    productDTO
                            .setProduct_id(productLinkArray
                                    .getString(i)
                                    .replace(
                                            "http://ealpha.com/mob/customers.php?get_data=product_data&product_id=",
                                            ""));
                    productDTO.setProduct_name(productNameArray.getString(i));
                    productDTO.setProduct_img(productImageArray.getString(i));
                    productDTO.setProduct_price(productPriceArray.getString(i));
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
                    productArrayList.add(productDTO);
                }
                setGridData();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public void setGridData() {
        if (productAdapterGridChild != null)
            productAdapterGridChild.notifyDataSetChanged();
    }

    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:

                fragment_position = 0;
                fragment = new HomeFragment();

                break;
            case 1:
                fragment_position = 1;
                fragment = new HomeFragment();
                break;

            default:
                break;
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .addToBackStack(TAG_FRAGMENT).commit();

            // update selected item and title, then close the drawer
            // mDrawerList.setItemChecked(position, true);
            // mDrawerList.setSelection(position);
            // if (sessionManager.isLogin()) {
            // if (position == 0) {
            // setTitle(toTitleCase(sessionManager.getUserDetail()
            // .getFirst_name()
            // + " "
            // + sessionManager.getUserDetail().getLast_name()));
            // } else {
            // setTitle(navMenuTitles[position]);
            // }
            // } else {
            // setTitle(navMenuTitles[position]);
            // }
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
        // setLoginDataOnSlideMenu();
    }

	/*
     * public void onBackPressed() { // getFragmentManager().popBackStack(); if
	 * (fragment != null) { displayView(0); } else { displayView(1); }}
	 */

    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {

            displayView(0);

        }
        // super.onBackPressed();
        else {
            if (doubleBackToExitPressedOnce) {
                // super.onBackPressed();
                return;
            }
        }
    }

    public void wistToList(int position) {
        if (!sessionManager.isLogin()) {
            Toast.makeText(getActivity(), "User not logged in.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        vProduct_ID = productArrayList.get(position).getProduct_id();
        if (productArrayList.get(position).isWish_to_list()) {
            productArrayList.get(position).setWish_to_list(false);
            MainActivity.wish_p_ids.remove(vProduct_ID);
            MainActivity.wish_p_ids = new ArrayList<String>(
                    new LinkedHashSet<String>(MainActivity.wish_p_ids));
            productAdapterGridChild.notifyDataSetChanged();
            new addAndRemoveWishListAsyncTask()
                    .execute("http://www.ealpha.com/mob/customers.php?customers=wishlist_remove");
        } else {
            productArrayList.get(position).setWish_to_list(true);
            MainActivity.wish_p_ids.add(vProduct_ID);
            MainActivity.wish_p_ids = new ArrayList<String>(
                    new LinkedHashSet<String>(MainActivity.wish_p_ids));
            productAdapterGridChild.notifyDataSetChanged();
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
                    Toast.makeText(getActivity(), "" + vMessage,
                            Toast.LENGTH_SHORT).show();
                } else if (vStatus.trim().equals("Error")) {
                    Toast.makeText(getActivity(), "" + vMessage,
                            Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Zeero/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; go home
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void finish() {
        // TODO Auto-generated method stub

    }

}
