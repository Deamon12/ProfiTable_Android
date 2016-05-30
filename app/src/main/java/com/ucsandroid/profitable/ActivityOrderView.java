package com.ucsandroid.profitable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ucsandroid.profitable.serverclasses.Location;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityOrderView extends AppCompatActivity implements View.OnClickListener {


    private FrameLayout customerFragContainer;
    private FrameLayout amountFragContainter;
    private FrameLayout menuitemFragContainer;

    private ImageView dividerArrow;
    private FloatingActionButton addCustomer;
    private FloatingActionButton doCheckout;
    private View menuDivider;


    int custFragHeight, custFragWidth;
    int menuItemsFragHeight, menuItemsFragWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_view);

        initToolbar();

        customerFragContainer = (FrameLayout) findViewById(R.id.orders_frag_container);
        amountFragContainter = (FrameLayout) findViewById(R.id.amounts_frag_container);
        menuitemFragContainer = (FrameLayout) findViewById(R.id.menuitems_frag_container);
        addCustomer = (FloatingActionButton) findViewById(R.id.addcustomer_fab);
        doCheckout = (FloatingActionButton) findViewById(R.id.docheckout_fab);
        doCheckout.setOnClickListener(this);
        addCustomer.setOnClickListener(this);
        menuDivider = findViewById(R.id.menu_divider);
        menuDivider.setOnClickListener(this);

        dividerArrow = (ImageView) findViewById(R.id.divider_image);


        //Dont update if local edits exist
        if(!Singleton.getInstance().getCurrentLocation().isEditedLocally())
            getLocationData();

        dynamicallySizeContainers();

        initFragments();
    }


    /**
     * Get all orders, customers, and anything else related to this location
     */
    private void getLocationData(){

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        ////http://52.38.148.241:8080/com.ucsandroid.profitable/rest/orders?rest_id=1&location_id=1

        int locationId = Singleton.getInstance().getCurrentLocation().getId();
        String restId = settings.getString(getString(R.string.rest_id), 1+"");

        Uri.Builder builder = Uri.parse("http://52.38.148.241:8080").buildUpon();
        builder.appendPath("com.ucsandroid.profitable")
                .appendPath("rest")
                .appendPath("orders")
                .appendQueryParameter("location_id", locationId+"")
                .appendQueryParameter("rest_id", restId+"");
        String myUrl = builder.build().toString();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                myUrl,
                (JSONObject) null,
                getOrderSuccesListener,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });


        Singleton.getInstance().addToRequestQueue(jsObjRequest);

    }


    /**
     * Use screen metrics to adjust the sizing of fragment containers.
     */
    private void dynamicallySizeContainers() {

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int orientation = getResources().getConfiguration().orientation;

        if(orientation == Configuration.ORIENTATION_LANDSCAPE){

            custFragHeight = (int)(metrics.heightPixels*.6);
            custFragWidth = (int)(metrics.widthPixels*.7);
            menuItemsFragHeight = (int)(metrics.heightPixels*.3);
            menuItemsFragWidth = (metrics.widthPixels);
        }
        else {

            custFragHeight = (int)(metrics.heightPixels*.6);
            custFragWidth = (int)(metrics.widthPixels*.7);
            menuItemsFragHeight = (int)(metrics.heightPixels*.3);
            menuItemsFragWidth = (int)(metrics.widthPixels);
        }


        //Dynamic resizing of the fragment containers
        customerFragContainer.getLayoutParams().height = custFragHeight;
        customerFragContainer.getLayoutParams().width = custFragWidth;

        menuitemFragContainer.getLayoutParams().height = menuItemsFragHeight;
        menuitemFragContainer.getLayoutParams().width = menuItemsFragWidth;

    }



    private void initToolbar(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.the_toolbar);

        if(Singleton.getInstance().getCurrentLocationType() == Singleton.TYPE_TABLE){
            toolbar.setTitle("Table " + (Singleton.getInstance().getCurrentLocationPosition() + 1));
        }
        else if(Singleton.getInstance().getCurrentLocationType() == Singleton.TYPE_BAR){
            toolbar.setTitle("Bar " + (Singleton.getInstance().getCurrentLocationPosition() + 1));
        }
        else if(Singleton.getInstance().getCurrentLocationType() == Singleton.TYPE_TAKEOUT){
            toolbar.setTitle("Takeout");
        }

        setSupportActionBar(toolbar);

    }

    /**
     * Basic fragment initialization
     */
    private void initFragments() {

        Fragment orderFrag = new FragmentOrders();
        Fragment amountFrag = new FragmentOrderAmount();
        Fragment menuFrag = new FragmentMenuViewpager();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.orders_frag_container, orderFrag);
        transaction.replace(R.id.amounts_frag_container, amountFrag);
        transaction.replace(R.id.menuitems_frag_container, menuFrag);

        // Commit the transaction
        transaction.commit();

    }


    /**
     * Dynamically show or hide the menu items to give more room to see customer info
     * TODO:animate
     */
    private void toggleMenuContainer(){

        if(menuitemFragContainer.findViewById(R.id.pager).getVisibility() == View.VISIBLE) {
            menuitemFragContainer.findViewById(R.id.pager).setVisibility(View.GONE);
            menuitemFragContainer.getLayoutParams().height = 0;
            dividerArrow.setImageResource(R.drawable.ic_arrow_drop_up_white_18dp);
        }
        else {
            menuitemFragContainer.findViewById(R.id.pager).setVisibility(View.VISIBLE);
            menuitemFragContainer.getLayoutParams().height = menuItemsFragHeight;
            dividerArrow.setImageResource(R.drawable.ic_arrow_drop_down_white_18dp);
        }

    }

    @Override
    public void onClick(View v) {

        if(v == menuDivider){
            toggleMenuContainer();
        }
        else if(v == addCustomer){
            sendAddCustomerBroadcast();
        }
        else if(v == doCheckout){
            doCheckOut();
        }

    }

    //TODO: split bills dialog
    private void doCheckOut() {
        Intent intent = new Intent(ActivityOrderView.this, ActivityCheckout.class);
        startActivity(intent);
    }

    /**
     * Create and send a broadcast for the order fragment to recieve and create a new customer,
     * and add them to the dataset.
     */
    private void sendAddCustomerBroadcast() {
        Intent intent = new Intent("add-customer");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    //TODO: change to snackbar
    public void showErrorSnackbar(String message){
        //Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    private Response.Listener getOrderSuccesListener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {
            //System.out.println("Location Volley success: " + response);

            try {
                JSONObject theResponse = new JSONObject(response.toString());

                //If successful retrieval, update Singleton
                if (theResponse.getBoolean("success") && theResponse.has("result")) {

                    Gson gson = new Gson();
                    Location mLocation = gson.fromJson(theResponse.getJSONObject("result").toString(), Location.class);

                    Singleton.getInstance().updateCurrentLocation(mLocation);

                    //TODO more logic, but dont erase local data, unless we NEED to

                    sendLocationUpdateBroadcast();

                } else {
                    if (theResponse.has("message")) {
                        showErrorSnackbar(theResponse.getString("message"));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };


    private void sendLocationUpdateBroadcast(){
        Intent intent = new Intent("update-location");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
