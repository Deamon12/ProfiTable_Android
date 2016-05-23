package com.ucsandroid.profitable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
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

import supportclasses.DialogDismissListener;
import supportclasses.MenuItem;
import supportclasses.NestedRecyclerAdapter;
import supportclasses.RecyclerViewClickListener;
import supportclasses.RecyclerViewLongClickListener;
import supportclasses.Table;

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


    private void getOrders() {

        Table table = Singleton.getInstance().getTable(Singleton.getInstance().getCurrentTableNumber());

        mAdapter = new NestedRecyclerAdapter(getActivity(), table,
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
     * MenuItem to be used to update amount of orders and orders UI
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
        if(!Singleton.getInstance().getTable(Singleton.getInstance().getCurrentTableNumber()).hasCost()){
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
/*
        try {

            JSONArray defaults = Singleton.getInstance().getCurrentTable().getCustomer(customer).getItem(position).getJsonItem().getJSONArray("defaultAdditions");
            //System.out.println("_____Defaults____");
            for(int a = 0;a < additions.length();a++){
                boolean found = false;
                //System.out.println(defaults.getJSONObject(a).getString("foodAdditionName"));

                for(int aa = 0;aa < defaults.length();aa++){

                    if(defaults.getJSONObject(aa).getString("foodAdditionName").equalsIgnoreCase(additions.getJSONObject(a).getString("foodAdditionName"))){
                        if(additions.getJSONObject(a).getBoolean("checked")){
                            System.out.println("default: "+additions.getJSONObject(a).getString("foodAdditionName")+" = yes");
                        }
                        else{
                            System.out.println("default: "+additions.getJSONObject(a).getString("foodAdditionName")+" : no");
                        }
                        found = true;
                    }

                }

                //If we get here we have an item that is not a default, and might be checked>?
                if(!found && additions.getJSONObject(a).getBoolean("checked")){
                    System.out.println("not default: "+additions.getJSONObject(a).getString("foodAdditionName")+" = yes");
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
*/

        sendUpdateAmountBroadcast();
    }


    private void sendUpdateAmountBroadcast(){
        Intent updateIntent = new Intent("update-amount");
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(updateIntent);
    }


    @Override
    public void onClick(View v) {
        if(v == sendToKitchenButton){
            //TODO: upload orders
            System.out.println("Send to kitchen....or something");
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


}