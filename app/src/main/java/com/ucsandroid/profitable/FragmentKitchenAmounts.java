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
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.ucsandroid.profitable.adapters.MenuItemRecyclerAdapter;
import com.ucsandroid.profitable.adapters.NestedKitchenRecyclerAdapter;
import com.ucsandroid.profitable.listeners.LocationClickListener;
import com.ucsandroid.profitable.serverclasses.Customer;
import com.ucsandroid.profitable.serverclasses.Location;
import com.ucsandroid.profitable.serverclasses.LocationCategory;
import com.ucsandroid.profitable.serverclasses.MenuItem;
import com.ucsandroid.profitable.serverclasses.OrderedItem;
import com.ucsandroid.profitable.serverclasses.Tab;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FragmentKitchenAmounts extends Fragment {

    private RecyclerView recyclerView;
    private List<Tab> mTabs;

    public static FragmentKitchenAmounts newInstance(String tabList) {
        FragmentKitchenAmounts thisFrag = new FragmentKitchenAmounts();

        Bundle args = new Bundle();
        args.putString("tabList", tabList);
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

        getQuantities();

    }

    private void getQuantities() {

        HashMap<Integer, MenuItem> foundItems = new HashMap<>();

        for(Tab tabItem : mTabs){
            for(Customer customerItem : tabItem.getCustomers()){

                for(OrderedItem orderItem : customerItem.getOrder()){
                    MenuItem receivedItem = null;
                    receivedItem = foundItems.get(orderItem.getMenuItem().getId());

                    if(receivedItem != null){
                        System.out.println("hash contains: "+ orderItem.getMenuItem().getId());
                        receivedItem.addQuantityByIncrement(1);
                        foundItems.put(receivedItem.getId(), receivedItem);
                    }
                    else{
                        System.out.println("hash does not contain: "+ orderItem.getMenuItem().getId());
                        foundItems.put(orderItem.getMenuItem().getId(), orderItem.getMenuItem());
                    }

                }

            }

        }

        initRecyclerView(foundItems);
    }


    private void initRecyclerView(HashMap<Integer, MenuItem> foundItems) {

        //Hash to list
        List<MenuItem> quantityItems = new ArrayList<>();
        for (Map.Entry<Integer, MenuItem> entry : foundItems.entrySet()) {
            int key = entry.getKey();
            MenuItem value = entry.getValue();
            System.out.println("key: "+key +" .... value: "+value.getQuantity());
            quantityItems.add(value);
        }


        StaggeredGridLayoutManager stagGridMan = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(stagGridMan);



        MenuItemRecyclerAdapter rcAdapter = new MenuItemRecyclerAdapter(
                getActivity(),
                quantityItems,
                R.layout.tile_kitchen_amount,
                -1,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT),
                null,
                null);

        recyclerView.setAdapter(rcAdapter);

    }

}