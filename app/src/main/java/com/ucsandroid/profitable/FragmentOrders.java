package com.ucsandroid.profitable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ucsandroid.profitable.serverclasses.Customer;
import com.ucsandroid.profitable.serverclasses.FoodAddition;
import com.ucsandroid.profitable.serverclasses.OrderedItem;
import com.ucsandroid.profitable.serverclasses.ServerMenuItem;
import com.ucsandroid.profitable.supportclasses.DialogDismissListener;
import com.ucsandroid.profitable.supportclasses.Location;
import com.ucsandroid.profitable.supportclasses.MenuItem;
import com.ucsandroid.profitable.supportclasses.NestedRecyclerAdapter;
import com.ucsandroid.profitable.supportclasses.RecyclerViewClickListener;
import com.ucsandroid.profitable.supportclasses.RecyclerViewLongClickListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentOrders extends Fragment implements DialogDismissListener, View.OnClickListener {

    private BroadcastReceiver mDoCalculationUpdate;
    private BroadcastReceiver mAddCustomerReceiver;
    private BroadcastReceiver mAddItemToCustomerReceiver;
    private NestedRecyclerAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RelativeLayout sendToKitchenButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (!Singleton.hasBeenInitialized()) {
            Singleton.initialize(getActivity());
        }

        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        sendToKitchenButton = (RelativeLayout) view.findViewById(R.id.sendToKitchen);
        sendToKitchenButton.setOnClickListener(this);
        sendToKitchenButton.setVisibility(View.GONE);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.orders_recyclerview);
        mRecyclerView.setHasFixedSize(false);
        initRecyclerView();
        initAddCustomerListener();
        initAddItemToCustomerListener();
        initUpdateAmountListener();
        checkSendToKitchenVisibility();

        return view;
    }


    /**
     * Unregister Broadcast listeners when this fragment gets detached, to prevent duplication
     */
    @Override
    public void onDetach() {
        super.onDetach();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mAddCustomerReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mAddItemToCustomerReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mDoCalculationUpdate);
    }

    /**
     * Init recyclerview with tiles to hold customers and the orders that go with the customers
     */
    private void initRecyclerView() {

        int spanCount;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 2;
        } else {
            spanCount = 1;
        }

        StaggeredGridLayoutManager stagLayout = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(stagLayout);

        getOrders();

    }


    //TODO: either pull data locally, or get from server
    private void getOrders() {

        mAdapter = new NestedRecyclerAdapter(getActivity(), Singleton.getInstance().getCurrentLocation(),
                R.layout.tile_customer_order, null, clickListener, longClickListener);

        mRecyclerView.setAdapter(mAdapter);

    }


    /**
     * Click listener for inner nested recyclerview. Pair this result with the current adapter position
     * to understand what customer and what item has been clicked.
     */
    RecyclerViewClickListener clickListener = new RecyclerViewClickListener() {
        @Override
        public void recyclerViewListClicked(View v, int parentPosition, int position, MenuItem item) {
            showEditDialog(parentPosition, position);
        }
    };

    /**
     * Click listener for inner nested recyclerview. Pair this result with the current adapter position
     * to understand what customer and what item has been clicked.
     */
    RecyclerViewLongClickListener longClickListener = new RecyclerViewLongClickListener() {
        @Override
        public void recyclerViewListLongClicked(View v, int parentPosition, int position, MenuItem item) {
            showLongClickedDialog(parentPosition, position);
        }


    };

    /**
     * Start the broadcast receiver to listen for add customers broadcasts from other fragments
     * in order to add customers to the order recyclerview in this fragment
     */
    private void initAddCustomerListener() {

        mAddCustomerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mAdapter != null) {
                    mAdapter.addCustomer();
                    mRecyclerView.scrollToPosition(0);
                }
            }
        };

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mAddCustomerReceiver,
                new IntentFilter("add-customer"));
    }


    /**
     * Listen for add item requests from any other fragment
     */
    private void initAddItemToCustomerListener() {

        mAddItemToCustomerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (mAdapter != null) {
                    addItem((MenuItem) intent.getSerializableExtra("menuItem"));
                }

                //Send broadcast to update amount calculation
                sendUpdateAmountBroadcast();

            }
        };

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mAddItemToCustomerReceiver,
                new IntentFilter("add-item"));
    }

    /**
     * ServerMenuItem to be used to update amount of orders and orders UI
     * @param item
     */
    public void addItem(MenuItem item) {

        if (mAdapter.getSelectedPosition() > -1) {
            //System.out.println("Need to add item: " + item + " to customer " + (mAdapter.getSelectedPosition() + 1));
            mAdapter.addItemToCustomer(mAdapter.getSelectedPosition(), item); //TODO: start attribute flow if necessary
            checkSendToKitchenVisibility();

        } else { //No customer is selected
            //System.out.println("Need to add item: "+itemId+ " to nobody "); //TODO: what to do here...
        }


    }



    private void showLongClickedDialog(final int customer, final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("What to do...");

        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                showEditDialog(customer, position);
            }
        });
        builder.setNegativeButton("Remove", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removeItem(customer, position);

            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        builder.show();
    }


    private void removeItem(int customer, int position){
        mAdapter.removeItemFromCustomer(customer, position);
        sendUpdateAmountBroadcast();
        checkSendToKitchenVisibility();
    }

    /**
     * Hide sendToKitchenButton, if the table has no orders for it
     */
    private void checkSendToKitchenVisibility(){

        if(!Singleton.getInstance().getCurrentLocation().hasCost()){
            sendToKitchenButton.setVisibility(View.GONE);
        }
        else{
            sendToKitchenButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * When an item is clicked from a particular customer, open a dialog for that specific item
     * This dialog will allow for selecting or deselecting additions for that item
     * @param parentPosition
     * @param position
     */
    private void showEditDialog(int parentPosition, int position) {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");

        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = DialogItemAttributes.newInstance(parentPosition, position, mAdapter.getItemFromCustomer(parentPosition, position));
        newFragment.setTargetFragment(this, 0);

        newFragment.show(ft, "dialog");

    }


    /**
     * The catching method that gets called when the additions dialog gets closed, dismissed, back buttoned.
     * This method will receive the list of all additions, whether they are selected or not.
     * @param additions
     */
    @Override
    public void dialogDismissListener(int customer, int position, JSONArray additions) {

        mAdapter.setAdditionsForItem(customer, position, additions);

        sendUpdateAmountBroadcast();
    }


    private void sendUpdateAmountBroadcast(){
        Intent updateIntent = new Intent("update-amount");
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(updateIntent);
    }


    @Override
    public void onClick(View v) {
        if(v == sendToKitchenButton){

            uploadOrder();

        }

    }

    private void initUpdateAmountListener() {

        mDoCalculationUpdate = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                checkSendToKitchenVisibility();
            }
        };

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mDoCalculationUpdate,
                new IntentFilter("update-amount"));
    }


    /**
     * CRAZY PARSING
     */
    private void uploadOrder(){

        Location thisLocation = Singleton.getInstance().getCurrentLocation();

        //Add customer to list
        List<Customer> customerList = new ArrayList();
        ArrayList<com.ucsandroid.profitable.supportclasses.Customer> customers = thisLocation.getCustomers();

        try {

            //Customer Loop
            for (int a = 0; a < customers.size(); a++) {

                System.out.println("Customer " + a + " : " + thisLocation.getJsonLocation().getInt("locationId"));


                Customer newCust = new Customer(a, thisLocation.getJsonLocation().getInt("locationId")); //TODO this should be tabId

                //OrderedItems includes menuItem along with other details
                List<OrderedItem> orderedItems = new ArrayList<>();
                ArrayList<MenuItem> menuItems = customers.get(a).getItems();
                //MenuItems Loop - (OrderedItem on server) CHANGE?
                for(int b = 0; b < menuItems.size(); b++){

                    String itemComment = menuItems.get(b).getComment();
                    String status = "";
                    boolean bringFirst = false;
                    List<FoodAddition> foodAdds = new ArrayList<>();
                    JSONArray additions = menuItems.get(b).getAdditions();
                    for(int c = 0; c < additions.length(); c++){

                        String foodAdditionName = additions.getJSONObject(c).getString("foodAdditionName");
                        int foodAdditionPrice = additions.getJSONObject(c).getInt("foodAdditionPrice");
                        boolean available = additions.getJSONObject(c).getBoolean("available");
                        int foodAdditionId = additions.getJSONObject(c).getInt("foodAdditionId");

                        foodAdds.add(new FoodAddition(foodAdditionName, foodAdditionPrice,available,foodAdditionId));
                    }

                    int menuItemId = menuItems.get(b).getJsonItem().getInt("menuItemId");
                    String menuName = menuItems.get(b).getJsonItem().getString("menuName");

                    OrderedItem orderedItem =
                            new OrderedItem(b,
                                    itemComment,
                                    status,
                                    bringFirst,
                                    new ServerMenuItem(menuItemId, menuName),
                                    foodAdds);


                    newCust.addItem(orderedItem);
                }//End menuitem

                customerList.add(newCust);
            }//End customer

        } catch (JSONException e) {
            e.printStackTrace();
        }



        Gson gson = new GsonBuilder().create();

        String customerslist = gson.toJson(customerList);

        System.out.println("GSON customer: "+ customerslist);

        Uri.Builder builder = Uri.parse("http://52.38.148.241:8080").buildUpon();
        builder.appendPath("com.ucsandroid.profitable")
                .appendPath("rest")
                .appendPath("orders")
                .appendPath("parseTest")
                .appendQueryParameter("customers", customerslist);

        String myUrl = builder.build().toString();


        //String workingURL = "http://52.38.148.241:8080/com.ucsandroid.profitable/rest/orders/parseTest?customers=[{%22order%22:[{%22additions%22:[{%22foodAdditionName%22:%22%22,%22available%22:false,%22foodAdditionId%22:3,%22foodAdditionPrice%22:0,%22restaurantId%22:0},{%22foodAdditionName%22:%22%22,%22available%22:false,%22foodAdditionId%22:1,%22foodAdditionPrice%22:0,%22restaurantId%22:0},{%22foodAdditionName%22:%22%22,%22available%22:false,%22foodAdditionId%22:2,%22foodAdditionPrice%22:0,%22restaurantId%22:0}],%22menuItem%22:{%22defaultAdditions%22:[],%22optionalAdditions%22:[],%22available%22:false,%22menuItemId%22:3,%22menuItemPrice%22:0},%22orderedItemNotes%22:%22%22,%22orderedItemStatus%22:%22%22,%22bringFirst%22:true,%22orderedItemId%22:0}],%22customerId%22:0,%22tabId%22:1}]";

        System.out.println("myUrl: " + myUrl);

        /**
         * JSONObject/Array requests were giving errors, F it. This will work for now.
         */
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,
                myUrl,
                (JSONObject) null,
                successListener,
                errorListener);

        Singleton.getInstance().addToRequestQueue(jsObjRequest);

    }

    private Response.Listener successListener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {
            System.out.println("Volley success: " + response);

            //progressDialog.dismiss();
            /*
            try {


                JSONObject theResponse = new JSONObject(response.toString());

                //If successful retrieval, update saved menu
                if(theResponse.getBoolean("success") && theResponse.has("result")){


                }
                else{
                    //TODO:Results Error
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }*/

        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {

            ///TODO Connect/server error
            System.out.println("Volley error: " + error.networkResponse);
        }
    };



}