package com.ucsandroid.profitable;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ucsandroid.profitable.serverclasses.Location;
import com.ucsandroid.profitable.serverclasses.LocationCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityKitchen extends AppCompatActivity {

    private FrameLayout kitchenAmountsFragContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);

        if (!Singleton.hasBeenInitialized()) {
            Singleton.initialize(this);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.the_toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        setupFragmentContainers();

        getKitchenData();


    }

    private void setupFragmentContainers(){

        kitchenAmountsFragContainer = (FrameLayout) findViewById(R.id.kitchen_totals_frag_container);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        TypedValue outValue = new TypedValue();
        int orientation = getResources().getConfiguration().orientation;
        int amountsHeight;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getResources().getValue(R.dimen.kitchen_amount_frag_width_landscape, outValue, true);
            amountsHeight = (int) (metrics.heightPixels * outValue.getFloat());

        } else {
            getResources().getValue(R.dimen.kitchen_amount_frag_width_portrait, outValue, true);
            amountsHeight = (int) (metrics.heightPixels * outValue.getFloat());
        }


        kitchenAmountsFragContainer.getLayoutParams().height = amountsHeight;

    }


    private void getKitchenData() {

        Uri.Builder builder = Uri.parse("http://52.38.148.241:8080").buildUpon();
        builder.appendPath("com.ucsandroid.profitable")
                .appendPath("rest")
                .appendPath("orders")
                .appendPath("kitchen")
                .appendQueryParameter("rest_id", "1"); //TODO
        String myUrl = builder.build().toString();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                myUrl,
                (JSONObject) null,
                successListener, errorListener);

        // Access the RequestQueue through your singleton class.
        Singleton.getInstance().addToRequestQueue(jsObjRequest);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_kitchen_view, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_tables:
                Intent netLabActivity = new Intent(ActivityKitchen.this, ActivityTableView.class);
                startActivity(netLabActivity);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Pass the Tab list to the fragments as a string. Gson parse them on the other side.
     * @param tabList
     */
    private void initFragments(String tabList) {

        Fragment ordersFrag = FragmentKitchenOrders.newInstance(tabList);
        Fragment quantityFrag = FragmentKitchenAmounts.newInstance(tabList);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.kitchen_items_frag_container, ordersFrag);
        transaction.replace(R.id.kitchen_totals_frag_container, quantityFrag);

        transaction.commit();

    }

    private Response.Listener successListener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {

            try {
                JSONObject theResponse = new JSONObject(response.toString());

                if(theResponse.getBoolean("success") && theResponse.has("result")){

                    JSONArray locationCats = theResponse.getJSONArray("result");
                    initFragments(locationCats.toString());

                }
                else{
                    //TODO:Results Error ((ActivityOrderView)getActivity()).showErrorSnackbar(theResponse.getString("message"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            ///TODO Connect/server error
            System.out.println("Volley error: " + error);
        }
    };


}
