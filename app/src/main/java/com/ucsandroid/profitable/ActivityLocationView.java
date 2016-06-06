package com.ucsandroid.profitable;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabSelectedListener;
import com.ucsandroid.profitable.serverclasses.Category;
import com.ucsandroid.profitable.serverclasses.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityLocationView extends AppCompatActivity {


    private CoordinatorLayout mCoordinator;

    private Fragment mTableFrag;
    private Fragment mBarFrag;
    private Fragment mTakeoutFrag;

    private BottomBar mBottomBar;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_view);

        mCoordinator = (CoordinatorLayout) findViewById(R.id.the_coordinator);
        toolbar = (Toolbar) findViewById(R.id.the_toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        if (!Singleton.hasBeenInitialized()) {
            Singleton.initialize(this);
        }


        initBottomNavigation(savedInstanceState);
        getMenu();
        //evaluateLocationData();
        getLocationsData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_table_view, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_kitchen:
                Intent intent = new Intent(ActivityLocationView.this, ActivityKitchen.class);
                startActivity(intent);
                finish();
                return true;

            case R.id.action_logout:
                clearSharedPrefs();
                Intent logoutIntent = new Intent(ActivityLocationView.this, ActivityLogin.class);
                startActivity(logoutIntent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initBottomNavigation(Bundle savedInstanceState) {



        mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.setActiveTabColor(ContextCompat.getColor(this, R.color.accent));

        mBottomBar.setItemsFromMenu(R.menu.bottom_nav_items, new OnMenuTabSelectedListener() {

            @Override
            public void onMenuItemSelected(int itemId) {
                int location = -1;

                switch (itemId) {
                    case R.id.action_show_tables:
                        if(mTableFrag != null && !mTableFrag.isVisible()){
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.location_frag_container, mTableFrag);
                            transaction.commit();
                            location = 0;
                        }
                        break;
                    case R.id.action_show_bar:
                        if(mBarFrag != null && !mBarFrag.isVisible()){
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.location_frag_container, mBarFrag);
                            transaction.commit();
                            location = 1;
                        }
                        break;
                    case R.id.action_show_takeout:
                        if(mTakeoutFrag != null && !mTakeoutFrag.isVisible()){
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.location_frag_container, mTakeoutFrag);
                            transaction.commit();
                            location = 2;
                        }
                        break;
                }

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ActivityLocationView.this);
                SharedPreferences.Editor edit = settings.edit();
                edit.putInt("location_tab", location);
                if(location != -1)
                    edit.apply();

            }
        });

    }


    private void initFragments() {

        mTableFrag = new FragmentTable();
        mBarFrag = new FragmentBar();
        mTakeoutFrag = new FragmentTakeout();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        int location = settings.getInt("location_tab", 0);

        if(location == 0){
            transaction.replace(R.id.location_frag_container, mTableFrag);
        }
        else if(location == 1){
            transaction.replace(R.id.location_frag_container, mBarFrag);
        }
        else if(location == 2){
            transaction.replace(R.id.location_frag_container, mTakeoutFrag);
        }

        transaction.commit();

        mBottomBar.selectTabAtPosition(location, true);
    }


    /**
     * Used for logout
     */
    private void clearSharedPrefs() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ActivityLocationView.this);
        SharedPreferences.Editor edit = settings.edit();
        edit.clear();
        edit.apply();
    }



    public void showErrorSnackbar(String message){
        Snackbar snackbar = Snackbar
                .make(mCoordinator, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }


    //------ Volley Calls ------//

    /**
     * Call server to get location information for this restaurant
     */
    private void getLocationsData() {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String restId = settings.getString(getString(R.string.rest_id), "1");

        Uri.Builder builder = Uri.parse("http://52.38.148.241:8080").buildUpon();
        builder.appendPath("com.ucsandroid.profitable")
                .appendPath("rest")
                .appendPath("location")
                .appendQueryParameter("rest_id", restId);
        String myUrl = builder.build().toString();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                myUrl,
                (JSONObject) null,
                locationSuccessListener,
                errorListener);

        Singleton.getInstance().addToRequestQueue(jsObjRequest);

    }

    private Response.Listener locationSuccessListener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {

            try {
                JSONObject theResponse = new JSONObject(response.toString());

                if(theResponse.getBoolean("success") && theResponse.has("result")){

                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ActivityLocationView.this);
                    //String newData = theResponse.getJSONArray("result").toString();
                    //String localData = settings.getString(getString(R.string.locations_jsonobject), "");

                    //IF results are different than local data, update local data
                    //if(!newData.equalsIgnoreCase(localData)){

                        SharedPreferences.Editor edit = settings.edit();
                        edit.putString(getString(R.string.locations_jsonobject), theResponse.getJSONArray("result").toString());
                        edit.apply();
                        //setLocationsFromPrefs();

                    //TODO: compare current locations to new -- needs work
                    Singleton.getInstance().setLocations(new JSONArray(theResponse.getJSONArray("result").toString()));

                    initFragments();

                   // }
                    //else if(newData.equalsIgnoreCase(localData)){
                        //System.out.println("Local locations are the same as new locations. Not updating data or UI");
                    //}
                }
                else{
                    showErrorSnackbar("Error getting location data");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    /**
     * Create and execute a volley call to retrieve JSON data from the server
     * Shows a progressDialog dialog before beginning
     */
    private void getMenu() {

        Uri.Builder builder = Uri.parse("http://52.38.148.241:8080").buildUpon();
        builder.appendPath("com.ucsandroid.profitable")
                .appendPath("rest")
                .appendPath("menu")
                .appendPath("entire")
                .appendQueryParameter("rest_id", "1");
        String myUrl = builder.build().toString();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                myUrl,
                (JSONObject) null,
                menuRetrieveSuccessListener,
                errorListener);

        Singleton.getInstance().addToRequestQueue(jsObjRequest);


    }
    /**
     * Menu retrieval success
     */
    private Response.Listener menuRetrieveSuccessListener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {

            try {

                JSONObject theResponse = new JSONObject(response.toString());

                if (theResponse.getBoolean("success") && theResponse.has("result")) {

                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ActivityLocationView.this);
                    SharedPreferences.Editor edit = settings.edit();
                    edit.putString(getString(R.string.menu_jsonobject), theResponse.getJSONArray("result").toString());
                    edit.apply();


                    //Build the category list for viewpager
                    //Also build a global hashmap for menuitems to lookup up details (additions, etc)
                    List<Category> freshCats = new ArrayList<>();
                    Gson gson = new Gson();

                    Category category;
                    for (int a = 0; a < theResponse.getJSONArray("result").length(); a++) {
                        category = gson.fromJson(theResponse.getJSONArray("result").getJSONObject(a).toString(), Category.class);

                        freshCats.add(category);
                        for (com.ucsandroid.profitable.serverclasses.MenuItem menuItem : category.getMenuItems()) {
                            Singleton.getInstance().addMenuItem(menuItem);
                        }
                    }


                } else {
                    showErrorSnackbar(theResponse.getString("message"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };


    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            showErrorSnackbar(error.toString());
        }
    };




}