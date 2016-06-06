package com.ucsandroid.profitable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.ucsandroid.profitable.serverclasses.Customer;
import com.ucsandroid.profitable.serverclasses.Location;
import com.ucsandroid.profitable.serverclasses.OrderedItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityOrderView extends AppCompatActivity implements View.OnClickListener {


    private BroadcastReceiver mUpdateLocationOrdersStatus;

    private CoordinatorLayout mCoordinator;
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

        if (!Singleton.hasBeenInitialized()) {
            Singleton.initialize(this);
        }

        mCoordinator = (CoordinatorLayout) findViewById(R.id.the_coordinator);
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


        getOrderData();

        dynamicallySizeContainers();

        initUpdateLocationStatusListener();
        initFragments();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_order_view, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_check_status:
                showFoodStatusDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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

        transaction.commit();
    }


    /**
     * Dynamically show or hide the menu items to give more room to see customer info
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
        showBillSplitDialog();

    }

    private void showBillSplitDialog() {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("bill_split");

        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        DialogFragment newFragment = DialogBillSplit.newInstance();
        newFragment.show(fragmentTransaction, "bill_split");


    }


    private void showFoodStatusDialog() {


        StringBuilder ready = new StringBuilder();
        StringBuilder delivered = new StringBuilder();

        ready.append("Ready");
        delivered.append("\n\nDelivered");

        for(Customer customer : Singleton.getInstance().getCurrentLocation().getCurrentTab().getCustomers()){

            for(OrderedItem item : customer.getOrders()){
                if(item.getOrderedItemStatus().equalsIgnoreCase("delivered"))
                    delivered.append("\n\t\t"+item.getMenuItem().getName());
                else if(item.getOrderedItemStatus().equalsIgnoreCase("ready"))
                    ready.append("\n\t\t"+item.getMenuItem().getName());
            }

        }


        ready.append(delivered);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Order Status");
        builder.setMessage(ready.toString());

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.setNeutralButton("set Delivered", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                List<Integer> orderedIds = getOrderedItemIds();
                for(Integer orderId : orderedIds){
                    doStatusVolleyCall(orderId, "delivered");
                }
            }
        });

        builder.show();
    }


    private void doNFCReceiptTransfer(){
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


    public void showSnackbar(String message){

        Snackbar snackbar = Snackbar
                .make(mCoordinator, message, Snackbar.LENGTH_LONG)
        .setAction("Refresh", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOrderData();
            }
        });
        snackbar.show();
    }

    private void sendOrderUIUpdate(){
        Intent intent = new Intent("update-orders");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private List<Integer> getOrderedItemIds() {

        List<Integer> orderedIds = new ArrayList<>();
        for (Customer customer : Singleton.getInstance().getCurrentLocation().getCurrentTab().getCustomers()) {

            for (OrderedItem item : customer.getOrders()) {

                if(item.getOrderedItemStatus().equalsIgnoreCase("ready") || item.getOrderedItemStatus().equalsIgnoreCase("ordered"))
                    orderedIds.add(item.getOrderedItemId());
            }

        }
        return orderedIds;
    }


    //----- Volley Calls -----//

    /**
     * Get all orders, customers, and anything else related to this location
     */
    private void getOrderData(){

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

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
                getOrderSuccessListener,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showSnackbar("Error getting order info");
                    }
                });

        Singleton.getInstance().addToRequestQueue(jsObjRequest);

    }


    private Response.Listener getOrderSuccessListener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {

            try {
                JSONObject theResponse = new JSONObject(response.toString());

                if (theResponse.getBoolean("success") && theResponse.has("result")) {

                    Gson gson = new Gson();
                    Location mLocation = gson.fromJson(theResponse.getJSONObject("result").toString(), Location.class);
                    Singleton.getInstance().updateCurrentLocation(mLocation);


                    //TODO: for new orders
                    //Singleton.getInstance().newOrders = mLocation;
                    //Singleton.getInstance().newOrders.getCurrentTab().getCustomers().clear();

                    //System.out.println("newLocation: "+mLocation.getCurrentTab().getCustomers().toString());

                    sendOrderUIUpdate();

                } else {
                    //empty location
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };


    private void doStatusVolleyCall(int orderId, String status) {

            Uri.Builder builder = Uri.parse("http://52.38.148.241:8080").buildUpon();
            builder.appendPath("com.ucsandroid.profitable")
                    .appendPath("rest")
                    .appendPath("orders")
                    .appendPath("item")
                    .appendPath(status)
                    .appendQueryParameter("ordered_item_id", orderId+"");
            String myUrl = builder.build().toString();

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.PUT,
                    myUrl,
                    (JSONObject) null,
                    statusSuccessListener,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //showSnackbar("Error setting order status");
                        }
                    });
            Singleton.getInstance().addToRequestQueue(jsObjRequest);

    }

    private Response.Listener statusSuccessListener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {

        }
    };


    /**
     * Update UI if we receive any status updates pertaining to this order
     */
    private void initUpdateLocationStatusListener() {

        mUpdateLocationOrdersStatus = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                int locationId = intent.getIntExtra("locationId", -1);
                String foodStatus = intent.getStringExtra("locationStatus");

                if(Singleton.getInstance().getCurrentLocation().getId() == locationId){
                    showSnackbar("");
                }

            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mUpdateLocationOrdersStatus,
                new IntentFilter("update-location-status"));

    }


}
