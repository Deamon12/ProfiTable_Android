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
import com.ucsandroid.profitable.serverclasses.Customer;
import com.ucsandroid.profitable.serverclasses.Location;
import com.ucsandroid.profitable.serverclasses.OrderedItem;


/**
 * Fragment that displays a grid of table icons that
 * are related to a table object with orders attached to it.
 */
public class FragmentTable extends Fragment {

    private BroadcastReceiver mRemeasureFragReceiver;

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

        if(mAdapter != null) {
            if(Singleton.getInstance().getCurrentLocationType() == Singleton.TYPE_TABLE){
                mAdapter.notifyItemChanged(Singleton.getInstance().getCurrentLocationPosition());
            }
        }
        else{
            System.out.println("adapter is null");
        }

        initRemeasureFragListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRemeasureFragReceiver);
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

                getTableData();

            }
        });

    }



    private void getTableData() {

        GridLayoutManager gridLayout = new GridLayoutManager(getActivity(), spanCount);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(gridLayout);

        mAdapter = new LocationRecyclerAdapter(getActivity(),
                Singleton.getInstance().getTables(),
                R.layout.tile_table,
                new ViewGroup.LayoutParams(tileLayoutWidth, tileLayoutWidth),
                clickListener,
                locationLongClickListener);

        mRecyclerView.setAdapter(mAdapter);

    }

    private void updateUI(int tableFragWidth){

        spanCount = getSpanCount();
        tileLayoutWidth = (tableFragWidth/spanCount);
        getTableData();
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

        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        int orientation = getResources().getConfiguration().orientation;

        if (tabletSize) {
            if(orientation == Configuration.ORIENTATION_LANDSCAPE){
                return 7;
            }else
                return 8;
        } else {
            if(orientation == Configuration.ORIENTATION_LANDSCAPE){
                return 6;
            }else
                return 5;
        }
    }


    /**
     * Listen for UI changes. Visibility changes from other frags will allow this fragment to be
     * larger or smaller
     */
    private void initRemeasureFragListener() {
        mRemeasureFragReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getIntExtra("tableWidth", 0) != -1 && getActivity() != null)
                    updateUI(intent.getIntExtra("tableWidth", 0));
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRemeasureFragReceiver,
                new IntentFilter("tablefrag-measure"));
    }


}

