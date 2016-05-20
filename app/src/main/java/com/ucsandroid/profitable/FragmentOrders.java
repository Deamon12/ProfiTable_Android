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

import org.json.JSONException;
import org.json.JSONObject;

import supportclasses.MenuItem;
import supportclasses.NestedRecyclerAdapter;
import supportclasses.RecyclerViewClickListener;
import supportclasses.RecyclerViewLongClickListener;
import supportclasses.Table;

public class FragmentOrders extends Fragment {

    private BroadcastReceiver mMessageReceiver;
    private NestedRecyclerAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (!Singleton.hasBeenInitialized()) {
            Singleton.initialize(getActivity());
        }



        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.orders_recyclerview);
        mRecyclerView.setHasFixedSize(false);
        initRecyclerView();
        initAddCustomerListener();

        return view;
    }


    /**
     * Unregister Broadcast listener when this fragment gets detached, to prevent duplicate listeners.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    /**
     * Init recyclerview with tiles to hold customer and the orders that go with the customer
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


        Table table = Singleton.getInstance().getTable(Singleton.getInstance().getCurrentTable());

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
            //System.out.println("clicked: " + position + " on parent " + parentPosition);

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
            //System.out.println("long clicked: " + position + " on parent " + parentPosition);

            showLongClickedDialog(parentPosition, position);
        }


    };

    /**
     * Start the broadcast receiver to listen for add customers broadcasts from other fragments
     * in order to add customers to the order recyclerview in this fragment
     */
    private void initAddCustomerListener() {

        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mAdapter != null) {
                    mAdapter.addCustomer();
                    mRecyclerView.scrollToPosition(0);
                }
            }
        };

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("add-customer"));


    }

    /**
     * ItemId is passed from Menu Items Pager. Use this itemId to pull attributes from webservice.
     * And begin add item dialog flow.
     *
     * @param item
     */
    public void addItem(MenuItem item) {

        if (mAdapter.getSelectedPosition() > -1) {

            //System.out.println("Need to add item: " + item + " to customer " + (mAdapter.getSelectedPosition() + 1));
            mAdapter.addItemToCustomer(mAdapter.getSelectedPosition(), item); //TODO: start attribute flow if necessary

        } else { //No customer is selected
            //System.out.println("Need to add item: "+itemId+ " to nobody "); //TODO: what to do here...
        }


    }


    private void showLongClickedDialog(final int parentPosition, final int subPosition) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("What to do...");

        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                showEditDialog(parentPosition, subPosition);
            }
        });
        builder.setNegativeButton("Remove", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        builder.show();
    }


    private void showEditDialog(int parentPosition, int subPosition) {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");

        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        System.out.println(parentPosition +", "+subPosition);

        System.out.println("Clicked: "+mAdapter.getItemFromCustomer(parentPosition, subPosition).getName());


        // Create and show the dialog.
        DialogFragment newFragment = null;
        try {
            newFragment = DialogItemAttributes.newInstance(mAdapter.getItemFromCustomer(parentPosition, subPosition).getJsonItem());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        newFragment.show(ft, "dialog");
    }


}