package com.ucsandroid.profitable;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ucsandroid.profitable.adapters.NestedKitchenRecyclerAdapter;
import com.ucsandroid.profitable.listeners.LocationClickListener;
import com.ucsandroid.profitable.serverclasses.Location;

public class FragmentKitchenOrders extends Fragment {

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_kitchen_orders, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.kitchen_orders_recyclerview);

        getOrders();

        return view;
    }

    private void getOrders() {

        initRecyclerView();
    }

    private void initRecyclerView() {

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);


        int layoutHeight, layoutWidth;
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //tileLayoutHeight = (int)(metrics.heightPixels);
            layoutWidth = (int) (metrics.widthPixels * .3);

        } else {
            //tileLayoutHeight = (int)(metrics.heightPixels*.1);
            layoutWidth = (int) (metrics.widthPixels * .4);
            //tileLayoutHeight = tileLayoutWidth;
        }



/*
        MyLinearLayoutManager layoutManager
                = new MyLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
*/


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


        NestedKitchenRecyclerAdapter rcAdapter = new NestedKitchenRecyclerAdapter(getActivity(),
                Singleton.getInstance().getBars(),
                R.layout.tile_kitchen_order,
                new ViewGroup.LayoutParams(layoutWidth, ViewGroup.LayoutParams.WRAP_CONTENT),
                null);

        recyclerView.setAdapter(rcAdapter);
    }


    LocationClickListener clickListener = new LocationClickListener() {

        @Override
        public void recyclerViewListClicked(View v, int parentPosition, int position, Location item) {


        }

    };


}