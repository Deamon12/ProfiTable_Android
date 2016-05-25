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

import com.google.gson.Gson;
import com.ucsandroid.profitable.adapters.NestedKitchenRecyclerAdapter;
import com.ucsandroid.profitable.listeners.LocationClickListener;
import com.ucsandroid.profitable.serverclasses.Location;
import com.ucsandroid.profitable.serverclasses.LocationCategory;
import com.ucsandroid.profitable.serverclasses.Tab;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FragmentKitchenOrders extends Fragment {

    private RecyclerView recyclerView;
    private List<Tab> mTabs;

    public static FragmentKitchenOrders newInstance(String tabList) {
        FragmentKitchenOrders thisFrag = new FragmentKitchenOrders();

        Bundle args = new Bundle();
        args.putString("tabList", tabList);
        thisFrag.setArguments(args);

        return thisFrag;
    }

    public FragmentKitchenOrders(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_kitchen_orders, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.kitchen_orders_recyclerview);

        try {
            parseTabs();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    private void parseTabs() throws JSONException {
        mTabs = new ArrayList<>();

        JSONArray tabsJson = new JSONArray(getArguments().getString("tabList"));

        Gson gson = new Gson();
        for(int a = 0; a < tabsJson.length(); a++){
            Tab tab = gson.fromJson(tabsJson.getJSONObject(a).toString(), Tab.class);
            mTabs.add(tab);
        }

        initRecyclerView();
    }

    private void initRecyclerView() {

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int layoutWidth;
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutWidth = (int) (metrics.widthPixels * .3);

        } else {
            layoutWidth = (int) (metrics.widthPixels * .4);
        }


/*
        MyLinearLayoutManager layoutManager
                = new MyLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
*/


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


        NestedKitchenRecyclerAdapter rcAdapter = new NestedKitchenRecyclerAdapter(getActivity(),
                mTabs,
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