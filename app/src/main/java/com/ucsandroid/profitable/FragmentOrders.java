package com.ucsandroid.profitable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import supportclasses.NestedRecyclerAdapter;
import supportclasses.RecyclerViewClickListener;

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

        ArrayList<String> dataSet = new ArrayList<>();

        for(int a = 1; a <= 5; a++)
            dataSet.add("Customer "+a);


        mAdapter = new NestedRecyclerAdapter(getActivity(), dataSet, R.layout.tile_customer_order,
                new ViewGroup.LayoutParams(tileLayoutWidth, ViewGroup.LayoutParams.MATCH_PARENT), clickListener);
        mRecyclerView.setAdapter(mAdapter);

    }


    RecyclerViewClickListener clickListener = new RecyclerViewClickListener() {
        @Override
        public void recyclerViewListClicked(View v, int position) {
            Intent orderViewActivity = new Intent(getActivity(), ActivityOrderView.class);
            getActivity().startActivity(orderViewActivity);
        }
    };


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


}