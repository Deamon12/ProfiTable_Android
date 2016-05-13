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
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import java.util.ArrayList;

import supportclasses.NestedRecyclerAdapter;
import supportclasses.Order;
import supportclasses.RecyclerViewClickListener;
import supportclasses.RecyclerViewLongClickListener;

public class FragmentOrders extends Fragment {

    private BroadcastReceiver mMessageReceiver;
    private NestedRecyclerAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private int tileLayoutHeight, tileLayoutWidth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.orders_recyclerview);
        initRecyclerView();
        initAddCustomerListener();

        return view;
    }

    /**
     * Init recyclerview with some basic properties
     */
    private void initRecyclerView() {

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int spanCount;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            spanCount = 2;
            tileLayoutHeight = (int)(metrics.heightPixels);
            tileLayoutWidth = (int)(metrics.widthPixels);

        }else{
            spanCount = 1;
            tileLayoutHeight = (int)(metrics.heightPixels);
            tileLayoutWidth = (int)(metrics.widthPixels);
        }

        StaggeredGridLayoutManager stagLayout = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(stagLayout);

        getOrders();

    }

    /**
     * Volley call to retrieve data and populate the adapter
     * TODO: add volley
     */
    private void getOrders() {

        ArrayList<Order> dataSet = new ArrayList<>();
        dataSet.add(new Order("Customer "+1));


        mAdapter = new NestedRecyclerAdapter(getActivity(), dataSet, R.layout.tile_customer_order,
                new ViewGroup.LayoutParams(tileLayoutWidth, ViewGroup.LayoutParams.MATCH_PARENT), clickListener, longClickListener);
        mRecyclerView.setAdapter(mAdapter);

    }


    /**
     * Click listener for inner nested recyclerview. Pair this result with the current adapter position
     * to understand what customer and what item has been clicked.
     */
    RecyclerViewClickListener clickListener = new RecyclerViewClickListener() {
        @Override
        public void recyclerViewListClicked(View v, int parentPosition, int position, String item) {
            System.out.println("clicked: " + position + " on parent " + parentPosition);

            showEditDialog(parentPosition, position);
        }
    };

    /**
     * Click listener for inner nested recyclerview. Pair this result with the current adapter position
     * to understand what customer and what item has been clicked.
     */
    RecyclerViewLongClickListener longClickListener = new RecyclerViewLongClickListener() {
        @Override
        public void recyclerViewListLongClicked(View v, int parentPosition, int position, String item) {
            System.out.println("long clicked: " + position + " on parent " + parentPosition);

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
                if(mAdapter != null) {
                    mAdapter.addCustomer();
                    mRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
                }
            }
        };

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("add-customer"));
    }

    /**
     * ItemId is passed from Menu Items Pager. Use this itemId to pull attributes from webservice.
     * And begin add item dialog flow.
     * @param itemId
     */
    public void addItem(String itemId){

        if(mAdapter.getSelectedPosition() > -1){
            System.out.println("Need to add item: "+itemId+ " to customer "+(mAdapter.getSelectedPosition()+1));
            mAdapter.addItemToCustomer(mAdapter.getSelectedPosition(), itemId); //TODO: start attribute flow if necessary

        }
        else
            System.out.println("Need to add item: "+itemId+ " to nobody "); //TODO: what to do here...

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


    private void showEditDialog(final int parentPosition, final int subPosition) {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = DialogItemAttributes.newInstance(new JSONObject());

        newFragment.show(ft, "dialog");
    }


}