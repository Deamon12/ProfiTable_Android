package com.ucsandroid.profitable;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.ucsandroid.profitable.listeners.LocationClickListener;
import com.ucsandroid.profitable.listeners.RecyclerViewClickListener;
import com.ucsandroid.profitable.adapters.TakeoutRecyclerAdapter;
import com.ucsandroid.profitable.serverclasses.Location;

public class FragmentTakeout extends Fragment {

    private RecyclerView mRecyclerView;
    private GridLayoutManager gridLayout;
    private TakeoutRecyclerAdapter mAdapter;

    private int mRecyclerViewWidth;
    private int spanCount;
    private int tileLayoutWidth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_takeout, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.takeout_recyclerview);
        initRecyclerView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
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

        gridLayout = new GridLayoutManager(getActivity(), spanCount);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(gridLayout);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new TakeoutRecyclerAdapter(getActivity(),
                Singleton.getInstance().getTakeouts(),
                R.layout.tile_takeout_new,
                new ViewGroup.LayoutParams(tileLayoutWidth, tileLayoutWidth),
                clickListener);

        mRecyclerView.setAdapter(mAdapter);

    }

    private int getSpanCount() {

        int orientation = getResources().getConfiguration().orientation;

        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            return getResources().getInteger(R.integer.takeout_tile_span_landscape);
        }else
            return getResources().getInteger(R.integer.takeout_tile_span_portrait);

    }


    LocationClickListener clickListener = new LocationClickListener() {
        @Override
        public void recyclerViewListClicked(View v, int parentPosition, int position, Location item) {
            if (position == 0) {
                //TODO add with server first ---> addTakeoutItem();
            } else {
                Singleton.getInstance().setLocationType(Singleton.TYPE_TAKEOUT);
                Singleton.getInstance().setCurrentLocationPosition(position);
                goToOrder();
            }
        }
    };


    private void addTakeoutItem() {
        mAdapter.addTakeoutItem();
    }

    private void goToOrder() {
        Intent orderViewActivity = new Intent(getActivity(), ActivityOrderView.class);
        getActivity().startActivity(orderViewActivity);
    }



}
