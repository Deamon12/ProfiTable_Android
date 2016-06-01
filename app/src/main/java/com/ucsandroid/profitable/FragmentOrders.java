package com.ucsandroid.profitable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ucsandroid.profitable.listeners.DialogDismissListener;
import com.ucsandroid.profitable.listeners.OrderedItemClickListener;
import com.ucsandroid.profitable.serverclasses.FoodAddition;
import com.ucsandroid.profitable.serverclasses.MenuItem;
import com.ucsandroid.profitable.serverclasses.OrderedItem;
import com.ucsandroid.profitable.adapters.CustomerOrdersAdapter;
import com.ucsandroid.profitable.listeners.RecyclerViewLongClickListener;

import java.util.List;


public class FragmentOrders extends Fragment implements DialogDismissListener, View.OnClickListener {

    private ProgressBar mProgress;
    private BroadcastReceiver mUpdateOrderUI;
    private BroadcastReceiver mDoCalculationUpdate;
    private BroadcastReceiver mAddCustomerReceiver;
    private BroadcastReceiver mAddItemToCustomerReceiver;
    private CustomerOrdersAdapter nestedRecyclerAdapter;
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
        mRecyclerView.setHasFixedSize(true);

        mProgress = (ProgressBar) view.findViewById(R.id.progress_spinner);
        mProgress.setVisibility(View.GONE);

        initRecyclerView();

        initUpdateOrderDetails();

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
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mUpdateOrderUI);
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

        nestedRecyclerAdapter = new CustomerOrdersAdapter(getActivity(),
                Singleton.getInstance().getCurrentLocation(),
                R.layout.tile_customer_order,
                null,
                clickListener,
                longClickListener);

        mRecyclerView.setAdapter(nestedRecyclerAdapter);

    }

    /**
     * Click listener for inner nested recyclerview. Pair this result with the current adapter position
     * to understand what customer and what item has been clicked.
     */
    OrderedItemClickListener clickListener = new OrderedItemClickListener() {
        @Override
        public void recyclerViewListClicked(View v, int parentPosition, int position, OrderedItem item) {
            showAdditionsDialog(parentPosition, position);
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
                if (nestedRecyclerAdapter != null) {
                    nestedRecyclerAdapter.addCustomer();
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
                if (nestedRecyclerAdapter != null) {
                    addItem((MenuItem) intent.getSerializableExtra("menuItem"));
                }
            }
        };

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mAddItemToCustomerReceiver,
                new IntentFilter("add-item"));
    }

    /**
     * MenuItem to be used to update amount of orders and orders UI
     * @param item
     */
    public void addItem(MenuItem item) {


        if (nestedRecyclerAdapter.getSelectedPosition() > -1) {

            //System.out.println("Need to add item: " + item.getName() + " to customer " + (nestedRecyclerAdapter.getSelectedPosition() + 1));
            int orderedItemId = 0;
            String orderedItemNotes = "notes";
            String orderedItemStatus = "status";
            boolean bringFirst = false;
            List<FoodAddition> additions = item.getDefaultAdditions();

            nestedRecyclerAdapter.addOrderedItemToCustomer( new OrderedItem(orderedItemId,orderedItemNotes,orderedItemStatus,bringFirst,item,additions ));
            checkSendToKitchenVisibility();

        } else { //No customer is selected
            //System.out.println("Need to add item: "+itemId+ " to nobody "); //TODO: what to do here...
        }

        sendUpdateAmountBroadcast();
    }


    private void showLongClickedDialog(final int customer, final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("What to do...");

        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                showAdditionsDialog(customer, position);
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
        nestedRecyclerAdapter.removeItemFromCustomer(customer, position);
        sendUpdateAmountBroadcast();
        checkSendToKitchenVisibility();
    }

    /**todo
     * Hide sendToKitchenButton, if the table has no orders for it
     */
    private void checkSendToKitchenVisibility(){

        if(Singleton.getInstance().getCurrentLocation().hasCost()){
            sendToKitchenButton.setVisibility(View.VISIBLE);
        }
        else{
            sendToKitchenButton.setVisibility(View.GONE);
        }
    }

    /**
     * When an item is clicked from a particular customer, open a dialog for that specific item
     * This dialog will allow for selecting or deselecting additions for that item
     * @param customerPosition
     * @param position
     */
    private void showAdditionsDialog(int customerPosition, int position) {

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");

        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);

        DialogFragment newFragment = DialogItemAttributes.newInstance(customerPosition, position, nestedRecyclerAdapter.getOrderedItemFromCustomer(customerPosition, position));
        newFragment.setTargetFragment(this, 0);

        newFragment.show(fragmentTransaction, "dialog");

    }


    /**
     * The catching method that gets called when the additions dialog gets closed, dismissed, back buttoned.
     * This method will receive the list of all additions, whether they are selected or not.
     * @param additions
     */
    @Override
    public void dialogDismissListener(int customer, int position, List<FoodAddition> additions) {

        nestedRecyclerAdapter.setAdditionsForItem(customer, position, additions);
        sendUpdateAmountBroadcast();
    }

    private void sendUpdateAmountBroadcast(){
        Singleton.getInstance().getCurrentLocation().setEditedLocally(true);
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



    private void uploadOrder(){

        mProgress.setVisibility(View.VISIBLE);

        Gson gson = new GsonBuilder().create();

        String customerslist = gson.toJson(Singleton.getInstance().getCurrentLocation().getCurrentTab().getCustomers());

        Uri.Builder builder = Uri.parse("http://52.38.148.241:8080").buildUpon();
        builder.appendPath("com.ucsandroid.profitable")
                .appendPath("rest")
                .appendPath("orders")
                .appendPath("parseTest")
                .appendQueryParameter("customers", customerslist);

        String myUrl = builder.build().toString();


        StringRequest jsObjRequest = new StringRequest(Request.Method.POST,
                myUrl,
                uploadOrderSuccessListener,
                uploadOrderErrorListener);

        Singleton.getInstance().addToRequestQueue(jsObjRequest);

    }

    private Response.Listener uploadOrderSuccessListener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {
            sendToKitchenButton.setVisibility(View.GONE);
            mProgress.setVisibility(View.GONE);
            System.out.println("Volley success: " + response);
        }
    };

    Response.ErrorListener uploadOrderErrorListener = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            mProgress.setVisibility(View.GONE);

            if(getActivity().findViewById(R.id.order_coordinator)  != null){
                Snackbar snackbar = Snackbar
                        .make(getActivity().findViewById(R.id.order_coordinator), "Error sending order", Snackbar.LENGTH_LONG);


                snackbar.show();
            }

            System.out.println("Volley error: " + error);
        }
    };


    /**
     * Indicates if location data has changed, if so we need to update the UI.
     */
    private void initUpdateOrderDetails() {
        mUpdateOrderUI = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Singleton.getInstance().getCurrentLocation().setEditedLocally(true);
                initRecyclerView();
                sendUpdateAmountBroadcast();
            }
        };

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mUpdateOrderUI,
                new IntentFilter("update-location"));
    }

}