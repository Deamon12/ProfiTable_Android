package com.ucsandroid.profitable;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean shown = settings.getBoolean("takeoutFragShown", true);

        //Hide fragment based on SharedPrefs
        //if (!shown)
        //    ((ActivityTableView) getActivity()).toggleTakeoutSection(false);

    }


    private void initRecyclerView() {

        spanCount = getSpanCount();


        ViewTreeObserver vto = mRecyclerView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mRecyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                DisplayMetrics metrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

                int orientation = getResources().getConfiguration().orientation;

                //Cant use the recycler width, because it may be set to GONE, which would be zero width
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mRecyclerViewWidth = (int) (metrics.widthPixels * .5);
                } else {
                    mRecyclerViewWidth = (int) (metrics.widthPixels);
                }

                tileLayoutWidth = (mRecyclerViewWidth / spanCount);

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
                R.layout.tile_takeout,
                new ViewGroup.LayoutParams(tileLayoutWidth, tileLayoutWidth),
                clickListener);

        mRecyclerView.setAdapter(mAdapter);

    }

    private int getSpanCount() {

        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        int orientation = getResources().getConfiguration().orientation;

        if (tabletSize) {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                return 6;
            } else
                return 8;
        } else {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE)
                return 4;
            else
                return 5;
        }
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
