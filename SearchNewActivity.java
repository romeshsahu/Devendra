package com.ealpha.main;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Toast;

import com.ealpha.R;
import com.ealpha.homeclick.DrawerItemsProductDetailActivity;
import com.ealpha.support.ProductAdapterGridChild;
import com.ps.DTO.CartsDTO;
import com.ps.DTO.ProductDTO;
import com.ps.utility.JSONParser;

public class SearchNewActivity extends Activity {
    private GridView grid_product_type;
    public static ArrayList<ProductDTO> productArrayList;
    private ProductDTO productDTO;
    private ProgressDialog progressDialog;
    private ProductAdapterGridChild productAdapterGridChild;
    private String vDefault_Child_Item_URL = "http://ealpha.com/mob/customers.php?get_data=category_data&category_id=207&start_limit=0&end_limit=12";
    public static SearchNewActivity searchNewActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_new);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        searchNewActivity = this;
        initialize();
    }

    public void initialize() {
        grid_product_type = (GridView) findViewById(R.id.grid_product_type);
        grid_product_type
                .setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) { // TODO Auto-generated

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

                        Intent intent_popular_grid = new Intent(
                                SearchNewActivity.this,
                                DrawerItemsProductDetailActivity.class);
                        intent_popular_grid.putExtra("link", productArrayList
                                .get(position).getProduct_link());
                        startActivity(intent_popular_grid);
                    }
                });
        // if (vDefault_Child_Item_URL != null) {
        // progressDialog = new ProgressDialog(this);
        // progressDialog.setIndeterminate(true);
        // progressDialog.setTitle("Please wait");
        // progressDialog.setCancelable(false);
        // progressDialog.setMessage("Please wait");
        // progressDialog.show();
        // new childItemsAsynchTask().execute(vDefault_Child_Item_URL, "");
        // }
    }

    public void searchOnChildFragment(String vSearcText) {
        if (vSearcText.trim().length() > 0) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("Please wait");
            progressDialog.setCancelable(true);
            progressDialog.setMessage("Please wait");
            progressDialog.show();
            // try {
            // new childItemsAsynchTask()
            // .execute("http://www.ealpha.com/mob/customers.php?customers=search_api&start_limit=1&end_limit=100&search="
            // + URLEncoder.encode(vSearcText, "UTF-8"));
            // } catch (Exception e) {
            //
            // }
            new childItemsAsynchTask()
                    .execute("http://www.ealpha.com/mob/customers.php?customers=search_api&search="
                            + vSearcText.replaceAll(" ", "%20")
                            + "&start_limit=0&end_limit=100");
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
                JSONObject category_data_Object = jsonObject
                        .getJSONObject("category_data");
                vStatus = category_data_Object.getString("status");
                if (vStatus.trim().equals("Error")) {
                    Toast.makeText(SearchNewActivity.this,
                            "Details Not found corresponding to category_id",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
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
                productArrayList = new ArrayList<ProductDTO>();
                for (int i = 0; i < productNameArray.length(); i++) {
                    productDTO = new ProductDTO();
                    productDTO
                            .setProduct_id(productLinkArray
                                    .getString(i)
                                    .replace(
                                            "http://ealpha.com/mob/customers.php?get_data=product_data&product_id=",
                                            ""));
                    productDTO.setProduct_name(productNameArray.getString(i));
                    try {
                        productDTO.setProduct_img(productImageArray
                                .getString(i));
                    } catch (Exception e) {

                    }
                    productDTO.setProduct_price(""
                            + (int) Float.parseFloat(productPriceArray
                            .getString(i)));
                    productDTO.setProduct_link(productLinkArray.getString(i));
                    productDTO.setWish_to_list(false);
                    productArrayList.add(productDTO);
                }
                setGridData();
            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    }

    public void setGridData() {
        productAdapterGridChild = new ProductAdapterGridChild(this,
                productArrayList);
        grid_product_type.setAdapter(productAdapterGridChild);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("What are you looking for?");
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (null != searchManager) {
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getComponentName()));
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.trim().length() > 0) {
                    searchOnChildFragment(query.toString().trim());
                } else {
                    Toast.makeText(SearchNewActivity.this,
                            "Enter search text.", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
