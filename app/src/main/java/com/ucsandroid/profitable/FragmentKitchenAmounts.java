package com.ucsandroid.profitable;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.ucsandroid.profitable.adapters.NestedKitchenRecyclerAdapter;
import com.ucsandroid.profitable.listeners.LocationClickListener;
import com.ucsandroid.profitable.serverclasses.Location;
import com.ucsandroid.profitable.serverclasses.LocationCategory;
import com.ucsandroid.profitable.serverclasses.OrderedItem;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FragmentKitchenAmounts extends Fragment {

    private RecyclerView recyclerView;
    private List<Location> mLocations;

    public static FragmentKitchenAmounts newInstance(String locations) {
        FragmentKitchenAmounts thisFrag = new FragmentKitchenAmounts();

        Bundle args = new Bundle();
        args.putString("locations", locations);
        thisFrag.setArguments(args);

        return thisFrag;
    }

    public FragmentKitchenAmounts(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_kitchen_amounts, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.kitchen_amounts_recyclerview);

        try {
            parseLocations();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    private void parseLocations() throws JSONException {
        mLocations = new ArrayList<>();

        JSONArray locationCats = new JSONArray(getArguments().getString("locations"));

        Gson gson = new Gson();
        for(int a = 0; a < locationCats.length(); a++){
            LocationCategory locCat = gson.fromJson(locationCats.getJSONObject(a).toString(), LocationCategory.class);
            List<Location> tempLocs = locCat.getLocations();
            for(int b = 0; b < tempLocs.size(); b++){
                mLocations.add(tempLocs.get(b));
            }
        }

        initRecyclerView();
    }


    private void initRecyclerView() {

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);


        int tileLayoutWidth;
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            tileLayoutWidth = (int) (metrics.widthPixels * .3);

        } else {
            tileLayoutWidth = (int) (metrics.widthPixels * .4);
        }


        StaggeredGridLayoutManager stagGridMan = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        GridLayoutManager gridManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.HORIZONTAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(stagGridMan);


        NestedKitchenRecyclerAdapter rcAdapter = new NestedKitchenRecyclerAdapter(getActivity(),
                mLocations,
                R.layout.tile_kitchen_amount,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT),
                null);

        recyclerView.setAdapter(rcAdapter);

    }

}