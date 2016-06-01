package com.ucsandroid.profitable;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.ucsandroid.profitable.adapters.LocationRecyclerAdapter;
import com.ucsandroid.profitable.listeners.LocationClickListener;
import com.ucsandroid.profitable.listeners.LocationLongClickListener;
import com.ucsandroid.profitable.serverclasses.Location;


/**
 * Fragment that displays a grid of table icons that
 * are related to a table object with orders attached to it.
 */
public class FragmentTable extends Fragment {

    private BroadcastReceiver mUpdateLocationUI;

    private int mRecyclerViewWidth;
    private RecyclerView mRecyclerView;
    private LocationRecyclerAdapter mAdapter;
    private int spanCount;
    private int tileLayoutWidth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (!Singleton.hasBeenInitialized()) {
            Singleton.initialize(getActivity());
        }

        View view = inflater.inflate(R.layout.fragment_table, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.table_recyclerview);
        initRecyclerView();


        return view;
    }

    /**
     * Use the recently checked table (via the Singleton) to see if the UI needs updating.
     * The UI will update if a customer is currently at the table.
     * Also, reinitialize the Measuring listener
     */
    @Override
    public void onResume() {
        super.onResume();

        initUpdateLocationStatus();

    }


    @Override
    public void onDetach() {
        super.onDetach();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mUpdateLocationUI);
    }

    private void initRecyclerView() {

        spanCount = getSpanCount();

        ViewTreeObserver vto = mRecyclerView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mRecyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                mRecyclerViewWidth  = mRecyclerView.getMeasuredWidth();
                tileLayoutWidth = (mRecyclerViewWidth/spanCount);
                showTableData();
            }
        });
    }

    private void showTableData() {

        GridLayoutManager gridLayout = new GridLayoutManager(getActivity(), spanCount);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(gridLayout);

        mAdapter = new LocationRecyclerAdapter(getActivity(),
                Singleton.getInstance().getTables(),
                R.layout.tile_table_new,
                new ViewGroup.LayoutParams(tileLayoutWidth, tileLayoutWidth),
                clickListener,
                locationLongClickListener);

        mRecyclerView.setAdapter(mAdapter);

    }

    /**
     * Click interface for adapter
     */
    LocationClickListener clickListener = new LocationClickListener() {

        @Override
        public void recyclerViewListClicked(View v, int parentPosition, int position, Location location) {
            Singleton.getInstance().setLocationType(Singleton.TYPE_TABLE);
            Singleton.getInstance().setCurrentLocationPosition(position);
            goToOrder();
        }

    };

    LocationLongClickListener locationLongClickListener = new LocationLongClickListener() {

        @Override
        public void recyclerViewListClicked(View v, int parentPosition, int position, Location item) {
            System.out.println("tableId: "+item.getId());
            System.out.println("restId: "+item.getRestaurantId());
            System.out.println("tabId: "+item.getCurrentTab().getTabId());

        }
    };

    private void goToOrder() {
        Intent orderViewActivity = new Intent(getActivity(), ActivityOrderView.class);
        getActivity().startActivity(orderViewActivity);
    }


    private int getSpanCount(){

        int orientation = getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            return getResources().getInteger(R.integer.table_tile_span_landscape);
        }else
            return getResources().getInteger(R.integer.table_tile_span_portrait);

    }


    /**
     * Indicates if location data has changed, if so we need to update the UI.
     * Update Singleton data, and currently used adapter
     */
    private void initUpdateLocationStatus() {

        mUpdateLocationUI = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("Received update location UI broadcast");

                //only update the status of the specific location
                int locationId = intent.getIntExtra("locationId", 0);
                String status = intent.getStringExtra("locationStatus");

                for(int a = 0; a < Singleton.getInstance().getTables().size(); a++){
                    if(Singleton.getInstance().getTables().get(a).getId() == locationId){
                        Singleton.getInstance().getTables().get(a).setStatus(status);
                        mAdapter.updateStatus(a, status);
                        return;
                    }
                }

            }
        };

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mUpdateLocationUI,
                new IntentFilter("update-location"));
    }


}

