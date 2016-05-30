package com.ucsandroid.profitable;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.ucsandroid.profitable.adapters.LocationRecyclerAdapter;
import com.ucsandroid.profitable.listeners.LocationClickListener;
import com.ucsandroid.profitable.listeners.LocationLongClickListener;
import com.ucsandroid.profitable.serverclasses.Location;

public class FragmentBar extends Fragment {

    private LocationRecyclerAdapter mAdapter;
    private int mRecyclerViewWidth;
    private RecyclerView mRecyclerView;
    private int spanCount;
    private int tileLayoutWidth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bar, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.bar_recyclerview);
        initRecyclerView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean shown = settings.getBoolean("barFragShown", true);

        //Hide fragment based on SharedPrefs
        //if(!shown)
        //    ((ActivityTableView)getActivity()).toggleBarSection(false);

        if(mAdapter != null && Singleton.getInstance().getCurrentLocationType() == Singleton.TYPE_BAR) {
            mAdapter.notifyItemChanged(Singleton.getInstance().getCurrentLocationPosition());
        }
        else{
            //System.out.println("bar adapter is null");
        }

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
                if(orientation == Configuration.ORIENTATION_LANDSCAPE){
                    mRecyclerViewWidth  = (int) (metrics.widthPixels*.5);
                }else{
                    mRecyclerViewWidth  = (int) (metrics.widthPixels);
                }

                tileLayoutWidth = (mRecyclerViewWidth/spanCount);

                getTableData();

            }
        });

    }


    private void getTableData() {

        GridLayoutManager gridLayout = new GridLayoutManager(getActivity(), spanCount);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(gridLayout);


        mAdapter = new LocationRecyclerAdapter(
                        getActivity(),
                        Singleton.getInstance().getBars(),
                        R.layout.tile_bar_new,
                        new ViewGroup.LayoutParams(tileLayoutWidth, tileLayoutWidth),
                        clickListener,
                        locationLongClickListener);

        mRecyclerView.setAdapter(mAdapter);


    }

    private int getSpanCount(){

        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        int orientation = getResources().getConfiguration().orientation;

        if (tabletSize) {
            if(orientation == Configuration.ORIENTATION_LANDSCAPE){
                return 8;
            }else
                return 9;
        } else {
            if(orientation == Configuration.ORIENTATION_LANDSCAPE)
                return 5;
            else
                return 6;
        }
    }

    LocationClickListener clickListener = new LocationClickListener() {
        @Override
        public void recyclerViewListClicked(View v, int parentPosition, int position, Location item) {
            Singleton.getInstance().setLocationType(Singleton.TYPE_BAR);
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

}