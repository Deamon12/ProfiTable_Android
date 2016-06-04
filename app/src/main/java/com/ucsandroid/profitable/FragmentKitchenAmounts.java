package com.ucsandroid.profitable;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.ucsandroid.profitable.adapters.MenuItemRecyclerAdapter;
import com.ucsandroid.profitable.serverclasses.Customer;
import com.ucsandroid.profitable.serverclasses.MenuItem;
import com.ucsandroid.profitable.serverclasses.OrderedItem;
import com.ucsandroid.profitable.serverclasses.Tab;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentKitchenAmounts extends Fragment {

    private BroadcastReceiver mUpdateKitchenUI;
    private RecyclerView recyclerView;
    private MenuItemRecyclerAdapter mAdapter;
    private List<Tab> mTabs;

    public static FragmentKitchenAmounts newInstance() {
        FragmentKitchenAmounts thisFrag = new FragmentKitchenAmounts();

        return thisFrag;
    }

    public FragmentKitchenAmounts(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_kitchen_amounts, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.kitchen_amounts_recyclerview);

        getKitchenData();
        initUpdateKitchenListener();

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mUpdateKitchenUI);
    }

    private void getQuantities() {

        HashMap<Integer, MenuItem> foundItems = new HashMap<>();

        for(Tab tabItem : mTabs){
            for(Customer customerItem : tabItem.getCustomers()){

                for(OrderedItem orderItem : customerItem.getOrders()){
                    MenuItem receivedItem = null;
                    receivedItem = foundItems.get(orderItem.getMenuItem().getId());

                    if(receivedItem != null && !orderItem.getOrderedItemStatus().equalsIgnoreCase("ready")){
                        //System.out.println("hash contains: "+ orderItem.getMenuItem().getId());
                        receivedItem.addQuantityByIncrement(1);
                        foundItems.put(receivedItem.getId(), receivedItem);
                    }
                    else if(!orderItem.getOrderedItemStatus().equalsIgnoreCase("ready")){
                        //System.out.println("hash does not contain: "+ orderItem.getMenuItem().getId());
                        foundItems.put(orderItem.getMenuItem().getId(), orderItem.getMenuItem());
                    }

                }

            }

        }

        if(getActivity() != null)
            initRecyclerView(foundItems);
    }


    private void initRecyclerView(HashMap<Integer, MenuItem> foundItems) {

        //Hash to list
        List<MenuItem> quantityItems = new ArrayList<>();
        for (Map.Entry<Integer, MenuItem> entry : foundItems.entrySet()) {
            int key = entry.getKey();
            MenuItem value = entry.getValue();
            //System.out.println("key: "+key +" .... value: "+value.getQuantity());
            quantityItems.add(value);
        }


        StaggeredGridLayoutManager stagGridMan = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(stagGridMan);

        mAdapter = new MenuItemRecyclerAdapter(
                getActivity(),
                quantityItems,
                R.layout.tile_kitchen_amount,
                -1,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT),
                null,
                null);

        recyclerView.setAdapter(mAdapter);

    }


    // ----- Volley Call ------//

    private void getKitchenData() {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String restId = settings.getString(getString(R.string.rest_id), 1+"");

        Uri.Builder builder = Uri.parse("http://52.38.148.241:8080").buildUpon();
        builder.appendPath("com.ucsandroid.profitable")
                .appendPath("rest")
                .appendPath("orders")
                .appendPath("kitchen")
                .appendQueryParameter("rest_id", restId);
        String myUrl = builder.build().toString();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                myUrl,
                (JSONObject) null,
                kitchenDataSuccessListener,
                errorListener);

        Singleton.getInstance().addToRequestQueue(jsObjRequest);
    }


    private Response.Listener kitchenDataSuccessListener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {

            try {
                JSONObject theResponse = new JSONObject(response.toString());

                if(theResponse.getBoolean("success") && theResponse.has("result")){

                    mTabs = new ArrayList<>();

                    JSONArray tabsJson = new JSONArray(theResponse.getJSONArray("result").toString());

                    Gson gson = new Gson();
                    for(int a = 0; a < tabsJson.length(); a++){
                        Tab tab = gson.fromJson(tabsJson.getJSONObject(a).toString(), Tab.class);
                        mTabs.add(tab);
                    }

                    getQuantities();

                }
                else{
                    //showErrorSnackbar(theResponse.getString("message"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {

            System.out.println("Volley error: " + error.getStackTrace());
            //((ActivityKitchen) getActivity()).showErrorSnackbar("Connection error, try again");
        }
    };



    // ------- BroadCast Receivers -----//

    private void initUpdateKitchenListener() {

        mUpdateKitchenUI = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                getKitchenData();
            }
        };

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mUpdateKitchenUI,
                new IntentFilter("update-kitchen"));
    }



}