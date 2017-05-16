package com.ealpha.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.ealpha.R;
import com.ealpha.cart.CartFragment;
import com.ealpha.drawer.AboutUs;
import com.ealpha.drawer.Collection;
import com.ealpha.drawer.ManageProfileFragment;
import com.ealpha.drawer.SignIn;
import com.ealpha.navigation.FragmentNavigationManager;
import com.ealpha.navigation.NavigationManager;
import com.ealpha.support.CustomExpandableListAdapter;
import com.ealpha.wishlist.WishListFragment;
import com.ps.DTO.CartsDTO;
import com.ps.DTO.ExpandableListDataSource;
import com.ps.utility.JSONParser;
import com.ps.utility.SessionManager;

public class MainActivity extends Activity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;

    private ExpandableListView mExpandableListView;
    private ExpandableListAdapter mExpandableListAdapter;
    private List<String> mExpandableListTitle;

    private NavigationManager mNavigationManager;
    public static int fragment_position = 0;
    private SessionManager sessionManager;
    boolean doubleBackToExitPressedOnce = false;
    private static String TAG_FRAGMENT = "HOME_FRAGMENT";
    private Map<String, List<String>> mExpandableListData;
    private int selectedPosition;
    private ShareActionProvider mShareActionProvider;
    public static HashMap<String, String> keyItems;
    private TextView mCounter;
    public static MainActivity mainActivity;
    // public static int wish_counter = 0;
    private RelativeLayout badgeLayout;
    public static int view_pagination_index = 0;
    public static ArrayList<String> wish_p_ids;
    public static CartsDTO cartsDTO;
    public static int cart_refresh_status = 1;
    public static int menu_cart_counter = 0;
    public static int home_from_order_complete = 1;

    @SuppressWarnings({"deprecation", "unchecked", "rawtypes", "unused"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android.app.ActionBar bar = getActionBar();
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(
                R.color.header)));
        view_pagination_index = 0;
        mainActivity = this;
        sessionManager = new SessionManager(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        mExpandableListView = (ExpandableListView) findViewById(R.id.navList);
        mNavigationManager = FragmentNavigationManager.obtain(this);
        mExpandableListData = ExpandableListDataSource.getData(this);
        mExpandableListTitle = new ArrayList();
        mExpandableListTitle.addAll(Arrays.asList(getResources()
                .getStringArray(R.array.drawerItems)));
        addDrawerItems();
        setupDrawer();
        if (savedInstanceState == null) {
            selectFirstItemAsDefault();
        } else if (savedInstanceState == null) {
            selectSignIn();
        } else if (savedInstanceState == null) {
            selectWishlist();
        } else if (savedInstanceState == null) {
            selectCart();
        } else if (savedInstanceState == null) {
            selectAboutUs();
        }
        new menuItemsAsynchTask()
                .execute("http://www.ealpha.com/mob/customers.php?customers=sub_category");
        if (sessionManager.isLogin()) {
            new getAllWishListAsyncTask()
                    .execute("http://www.ealpha.com/mob/customers.php?customers=wishlist_view");
        }
    }

    private void selectFirstItemAsDefault() {
        if (mNavigationManager != null) {
            selectItem(1);
        }
    }

    private void selectItem(int position) {
        selectedPosition = position;
        mDrawerLayout.closeDrawer(GravityCompat.START);
        MainActivity.view_pagination_index = 0;
        Fragment fragment = new HomeFragment();
        // Fragment fragment = new SignIn();
        Bundle args = new Bundle();
        // args.putInt(PlanetFragment.ARG_PLANET_NUMBER, selectedPosition);
        fragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment).commit();
        setTitle(mExpandableListTitle.get(selectedPosition));
    }

    private void selectSignIn() {
        if (mNavigationManager != null) {
            selectSignIn(0);
        }
    }

    private void selectSignIn(int position) {
        if (mNavigationManager != null) {
            selectedPosition = position;
            mDrawerLayout.closeDrawer(GravityCompat.START);
            Fragment fragment;
            if (sessionManager.isLogin()) {
                fragment = new ManageProfileFragment();
            } else {
                fragment = new SignIn();
            }
            // Fragment fragment = new SignIn();
            Bundle args = new Bundle();
            // args.putInt(PlanetFragment.ARG_PLANET_NUMBER, selectedPosition);
            fragment.setArguments(args);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment).commit();
            setTitle(mExpandableListTitle.get(selectedPosition));
        }
    }

    private void selectWishlist() {
        if (mNavigationManager != null) {
            selectWishlist(12);
        }
    }

    private void selectWishlist(int position) {
        if (mNavigationManager != null) {
            selectedPosition = position;
            mDrawerLayout.closeDrawer(GravityCompat.START);
            Fragment fragment = new WishListFragment();
            // Fragment fragment = new SignIn();
            Bundle args = new Bundle();
            // args.putInt(PlanetFragment.ARG_PLANET_NUMBER, selectedPosition);
            fragment.setArguments(args);
            FragmentManager fragmentManager = getFragmentManager();

            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment).commit();

            setTitle(mExpandableListTitle.get(selectedPosition));
        }
    }

    private void selectCart() {
        if (mNavigationManager != null) {
            selectCart(13);
        }
    }

    public void selectCart(int position) {
        if (mNavigationManager != null) {
            selectedPosition = position;
            mDrawerLayout.closeDrawer(GravityCompat.START);
            Fragment fragment = new CartFragment();
            // Fragment fragment = new SignIn();
            Bundle args = new Bundle();
            // args.putInt(PlanetFragment.ARG_PLANET_NUMBER, selectedPosition);
            fragment.setArguments(args);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment).commitAllowingStateLoss();
            setTitle(mExpandableListTitle.get(selectedPosition));
        }
    }

    private void selectAboutUs() {
        if (mNavigationManager != null) {
            selectAboutUs(14);
        }
    }

    private void selectAboutUs(int position) {
        if (mNavigationManager != null) {
            selectedPosition = position;
            mDrawerLayout.closeDrawer(GravityCompat.START);
            Fragment fragment = new AboutUs();
            // Fragment fragment = new SignIn();
            Bundle args = new Bundle();
            // args.putInt(PlanetFragment.ARG_PLANET_NUMBER, selectedPosition);
            fragment.setArguments(args);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment).commit();
            setTitle(mExpandableListTitle.get(selectedPosition));

        }

    }

    @Override
    public void setTitle(CharSequence title) {
        // mTitle = title;
        getActionBar().setTitle(title);
    }

    public void addDrawerItems() {
        mExpandableListAdapter = new CustomExpandableListAdapter(this,
                mExpandableListTitle, mExpandableListData);
        mExpandableListView.setAdapter(mExpandableListAdapter);
        mExpandableListView
                .setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                    @Override
                    public void onGroupExpand(int groupPosition) {
                        getActionBar().setTitle(
                                mExpandableListTitle.get(groupPosition)
                                        .toString());
                    }
                });
        mExpandableListView
                .setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                    @Override
                    public void onGroupCollapse(int groupPosition) {
                        getActionBar().setTitle(R.string.app_name);
                    }
                });
        mExpandableListView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // TODO Auto-generated method stub
                if (groupPosition == 0) {
                    fragment_position = 0;
                    selectSignIn(0);
                } else if (groupPosition == 1) {
                    fragment_position = 1;
                    selectItem(1);
                } else if (groupPosition == 12) {
                    fragment_position = 12;
                    selectWishlist(12);
                } else if (groupPosition == 13) {
                    fragment_position = 13;
                    selectCart(13);
                } else if (groupPosition == 14) {
                    fragment_position = 14;
                    selectAboutUs(14);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else if (groupPosition == 15) {
                    if (sessionManager.isLogin()) {
                        fragment_position = 15;
                        logOutAccount().show();
                    } else {
                        selectSignIn(0);
                    }
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
                return false;
            }
        });
        mExpandableListView
                .setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent,
                                                View v, int groupPosition, int childPosition,
                                                long id) {
                        String selectedItem = ((List) (mExpandableListData
                                .get(mExpandableListTitle.get(groupPosition))))
                                .get(childPosition).toString();

                        String selectedGroupItem = mExpandableListTitle.get(
                                groupPosition).toString();

                        getActionBar().setTitle(selectedItem);
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        selectedPosition = groupPosition;
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        Fragment fragment = new ChildItemsFragment(
                                selectedGroupItem + "_" + selectedItem);
                        Bundle args = new Bundle();
                        fragment.setArguments(args);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.content_frame, fragment).commit();
                        setTitle(selectedItem);
                        return false;
                    }
                });
        if (sessionManager.isLogin()) {
            mExpandableListTitle.set(0, sessionManager.getUserDetail()
                    .getFirst_name()
                    + " "
                    + sessionManager.getUserDetail().getLast_name());
            mExpandableListTitle
                    .set(mExpandableListTitle.size() - 1, "Log Out");
            mExpandableListData.remove("Sign In");
            mExpandableListData.remove("Log In");
            mExpandableListData.put(sessionManager.getUserDetail()
                            .getFirst_name()
                            + " "
                            + sessionManager.getUserDetail().getLast_name(),
                    new ArrayList<String>());
            mExpandableListData.put("Log Out", new ArrayList<String>());
            mExpandableListAdapter = new CustomExpandableListAdapter(this,
                    mExpandableListTitle, mExpandableListData);
            mExpandableListView.setAdapter(mExpandableListAdapter);
        } else {
            mExpandableListTitle.set(mExpandableListTitle.size() - 1, "");
            mExpandableListData.remove("Log Out");
            mExpandableListData.put("", new ArrayList<String>());
            mExpandableListAdapter = new CustomExpandableListAdapter(this,
                    mExpandableListTitle, mExpandableListData);
            mExpandableListView.setAdapter(mExpandableListAdapter);
        }
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(R.string.app_name);
                invalidateOptionsMenu(); // creates call to
            }

            /** Called when a drawer has settled in a completely closed state. */
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        badgeLayout = (RelativeLayout) menu.findItem(R.id.action_cart)
                .getActionView();
        mCounter = (TextView) badgeLayout.findViewById(R.id.counter);
        setBadgeOnCartTest();
        badgeLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                selectCart();
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                // search action
                startActivity(new Intent(MainActivity.this, SearchNewActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private AlertDialog logOutAccount() {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(
                MainActivity.this)
                .setTitle("Ealpha")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                // your deleting code
                                dialog.dismiss();
                                if (sessionManager.isLogin()) {
                                    sessionManager.logOut();
                                    finish();
                                } else {
                                    Toast.makeText(MainActivity.this,
                                            "User not logged in.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        return myQuittingDialogBox;
    }

    class menuItemsAsynchTask extends AsyncTask<String, Void, JSONObject> {
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
            // ----------for menu item--------------
            try {
                JSONObject sub_menu_Object = jsonObject
                        .getJSONObject("sub_menu");
                JSONObject sub_menu_link_Object = jsonObject
                        .getJSONObject("sub_menu_link");
                keyItems = new HashMap<String, String>();
                try {
                    JSONArray Men_Object = sub_menu_Object.getJSONArray("Men");
                    JSONArray Men_link_Object = sub_menu_link_Object
                            .getJSONArray("Men");
                    for (int i = 0; i < Men_Object.length(); i++) {
                        keyItems.put("Men_" + Men_Object.getString(i),
                                Men_link_Object.getString(i));
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
                try {
                    JSONArray Women_Object = sub_menu_Object
                            .getJSONArray("Women");
                    JSONArray Women_link_Object = sub_menu_link_Object
                            .getJSONArray("Women");
                    for (int i = 0; i < Women_Object.length(); i++) {
                        keyItems.put("Women_" + Women_Object.getString(i),
                                Women_link_Object.getString(i));
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }

                try {
                    JSONArray kid_Zone_Object = sub_menu_Object
                            .getJSONArray("Kid");
                    JSONArray kid_link_Object = sub_menu_link_Object
                            .getJSONArray("Kid");
                    for (int i = 0; i < kid_Zone_Object.length(); i++) {
                        keyItems.put("Kid_" + kid_Zone_Object.getString(i),
                                kid_link_Object.getString(i));
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }

                try {
                    JSONArray Health_Care_Object = sub_menu_Object
                            .getJSONArray("Health Care");
                    JSONArray Health_Care_link_Object = sub_menu_link_Object
                            .getJSONArray("Health Care");
                    for (int i = 0; i < Health_Care_Object.length(); i++) {
                        keyItems.put(
                                "Health Care_"
                                        + Health_Care_Object.getString(i),
                                Health_Care_link_Object.getString(i));
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }

                try {
                    JSONArray Mobiles_Tablets_Object = sub_menu_Object
                            .getJSONArray("Mobiles & Tablets");
                    JSONArray Mobiles_Tablets_link_Object = sub_menu_link_Object
                            .getJSONArray("Mobiles & Tablets");
                    for (int i = 0; i < Mobiles_Tablets_Object.length(); i++) {
                        keyItems.put("Mobiles & Tablets_"
                                        + Mobiles_Tablets_Object.getString(i),
                                Mobiles_Tablets_link_Object.getString(i));
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }

                try {
                    JSONArray Electronics_Object = sub_menu_Object
                            .getJSONArray("Electronics");
                    JSONArray Electronics_link_Object = sub_menu_link_Object
                            .getJSONArray("Electronics");
                    for (int i = 0; i < Electronics_Object.length(); i++) {
                        keyItems.put(
                                "Electronics_"
                                        + Electronics_Object.getString(i),
                                Electronics_link_Object.getString(i));
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }

                try {
                    JSONArray Home_Decor_Furnishing_Object = sub_menu_Object
                            .getJSONArray("Home Decor & Furnishing");
                    JSONArray Home_Decor_Furnishing_link_Object = sub_menu_link_Object
                            .getJSONArray("Home Decor & Furnishing");
                    for (int i = 0; i < Home_Decor_Furnishing_Object.length(); i++) {
                        keyItems.put("Home Decor & Furnishing_"
                                        + Home_Decor_Furnishing_Object.getString(i),
                                Home_Decor_Furnishing_link_Object.getString(i));
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }

                try {
                    JSONArray Home_Utility_Object = sub_menu_Object
                            .getJSONArray("Home Utility");
                    JSONArray Home_Utility_link_Object = sub_menu_link_Object
                            .getJSONArray("Home Utility");
                    for (int i = 0; i < Home_Utility_Object.length(); i++) {
                        keyItems.put(
                                "Home Utility_"
                                        + Home_Utility_Object.getString(i),
                                Home_Utility_link_Object.getString(i));
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }

                try {
                    JSONArray Sairandhri_Object = sub_menu_Object
                            .getJSONArray("Sairandhri");
                    JSONArray Sairandhri_link_Object = sub_menu_link_Object
                            .getJSONArray("Sairandhri");
                    for (int i = 0; i < Sairandhri_Object.length(); i++) {
                        keyItems.put(
                                "Sairandhri_" + Sairandhri_Object.getString(i),
                                Sairandhri_link_Object.getString(i));
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
                try {
                    JSONArray Spiritual_Items_Object = sub_menu_Object
                            .getJSONArray("Spiritual Items");
                    JSONArray Spiritual_Items_Object_link = sub_menu_link_Object
                            .getJSONArray("Spiritual Items");
                    for (int i = 0; i < Spiritual_Items_Object.length(); i++) {
                        keyItems.put("Spiritual Items_"
                                        + Spiritual_Items_Object.getString(i),
                                Spiritual_Items_Object_link.getString(i));
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }

            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                if (sessionManager.isLogin()) {
                    fragment_position = 0;
                    fragment = new ManageProfileFragment();
                } else {
                    fragment_position = 0;
                    fragment = new SignIn();
                }
                break;
            case 1:
                fragment_position = 1;
                fragment = new HomeFragment();
                break;
            case 2:
                fragment_position = 2;
                fragment = new Collection();
                break;
            case 3:
                fragment_position = 3;
                fragment = new Collection();
                break;
            case 4:
                fragment_position = 4;
                fragment = new Collection();
                break;
            case 5:
                fragment_position = 5;
                fragment = new AboutUs();
                break;
            case 6:
                fragment_position = 6;
                fragment = new AboutUs();
                break;
            case 7:
                fragment_position = 7;
                break;
            case 8:
                fragment_position = 8;
                break;
            case 9:
                // log out code
                logOutAccount().show();
                break;
            default:
                break;
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .addToBackStack(TAG_FRAGMENT).commit();

            mDrawerLayout.closeDrawer(mExpandableListView);
        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            if (fragment_position != 1) {
                displayView(1);
            } else {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }
                if (MainActivity.view_pagination_index != 0) {
                    selectItem(1);
                    return;
                } else {
                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, "Please click BACK again to exit",
                            Toast.LENGTH_SHORT).show();
                }
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            if (MainActivity.view_pagination_index != 0) {
                selectItem(1);
                return;
            } else {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to exit",
                        Toast.LENGTH_SHORT).show();
            }
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
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
                wish_p_ids = new ArrayList<String>();
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
                                wish_p_ids
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
//				MainActivity.mainActivity
//						.setBadgeOnCart(MainActivity.wish_p_ids.size());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cart_refresh_status == 2) {
            cart_refresh_status = 1;
            if (CartFragment.myCartFragment != null) {
                CartFragment.myCartFragment.setData();
            }
        }
        if (MainActivity.home_from_order_complete == 2) {
            MainActivity.home_from_order_complete = 1;
            selectItem(1);
        }
        setBadgeOnCartTest();
    }
}
