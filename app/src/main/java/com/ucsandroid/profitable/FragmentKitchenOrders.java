package com.ucsandroid.profitable;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.ucsandroid.profitable.adapters.NestedKitchenRecyclerAdapter;
import com.ucsandroid.profitable.listeners.NestedClickListener;
import com.ucsandroid.profitable.listeners.OrderedItemClickListener;
import com.ucsandroid.profitable.serverclasses.Customer;
import com.ucsandroid.profitable.serverclasses.OrderedItem;
import com.ucsandroid.profitable.serverclasses.Tab;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentKitchenOrders extends Fragment {

    private BroadcastReceiver mUpdateKitchenUI;

    private RelativeLayout noOrdersDialog;
    private RecyclerView recyclerView;
    private List<Tab> mTabs;
    private NestedKitchenRecyclerAdapter mAdapter;

    public static FragmentKitchenOrders newInstance() {
        FragmentKitchenOrders thisFrag = new FragmentKitchenOrders();

        return thisFrag;
    }

    //Empty constructor
    public FragmentKitchenOrders(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_kitchen_orders, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.kitchen_orders_recyclerview);
        noOrdersDialog = (RelativeLayout) view.findViewById(R.id.no_orders_relative);

        getKitchenData();
        initUpdateKitchenListener();

        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mUpdateKitchenUI);
    }

    //Remove tabs that are all ready - should do this on server
    private void evaluateStatuses(){

        if(getActivity() != null)
            initRecyclerView();
    }


    private void initRecyclerView() {

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        //Use dimension based on device size
        TypedValue outValue = new TypedValue();

        int orientation = getResources().getConfiguration().orientation;
        int layoutWidth;
        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            getResources().getValue(R.dimen.kitchen_order_width_landscape, outValue, true);
            layoutWidth = (int)(metrics.widthPixels*outValue.getFloat());
        }
        else{
            getResources().getValue(R.dimen.kitchen_order_width_portrait, outValue, true);
            layoutWidth = (int)(metrics.widthPixels*outValue.getFloat());
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new NestedKitchenRecyclerAdapter(getActivity(),
                mTabs,
                R.layout.tile_kitchen_order,
                new ViewGroup.LayoutParams(layoutWidth, ViewGroup.LayoutParams.WRAP_CONTENT),
                tabClickListener,
                orderedItemClickListener);

        recyclerView.setAdapter(mAdapter);

    }

    /**
     * Pass clicks back to this fragment from the local adapter
     */
    NestedClickListener tabClickListener = new NestedClickListener() {

        @Override
        public void nestedClickListener(int position, Tab tab) {
            System.out.println("In nested click listener, updating ALL status");
            setOrderStatusToReady(position);
        }
    };

    OrderedItemClickListener orderedItemClickListener = new OrderedItemClickListener() {
        @Override
        public void recyclerViewListClicked(View v, int parentPosition, int position, OrderedItem item) {
            doStatusVolleyCall(item.getOrderedItemId(), "ready");
        }
    };

    /**
     * Get Tab item from adapter and parse thru it to get OrderId's
     * Need orderId of each item to set status for each item
     * @param position
     */
    private void setOrderStatusToReady(int position){

        List<Integer> orderedIds = getOrderedItemIds(position);
        for(Integer orderId : orderedIds){
            doStatusVolleyCall(orderId, "ready");
        }

    }

    /**
     * Create a list of orderId's (integer's) from
     * this recyclerviews adapter data
     * @param position
     * @return
     */
    private List<Integer> getOrderedItemIds(int position) {

        List<Integer> orderedIds = new ArrayList<>();
        for (Customer customer : mAdapter.getDataItem(position).getCustomers()) {

            for (OrderedItem item : customer.getOrders()) {
                if(item.getOrderedItemStatus().equalsIgnoreCase("ordered"))
                    orderedIds.add(item.getOrderedItemId());
            }
        }
        return orderedIds;
    }


    /**
     * Use the list of orderId's to create a series of volley calls that individually update the
     * statuses of each item.
     * @param orderId as a list of integers
     * @param status: ready, cooking, delivered
     */
    private void doStatusVolleyCall(int orderId, String status) {

            Uri.Builder builder = Uri.parse("http://52.38.148.241:8080").buildUpon();
            builder.appendPath("com.ucsandroid.profitable")
                    .appendPath("rest")
                    .appendPath("orders")
                    .appendPath("item")
                    .appendPath(status)
                    .appendQueryParameter("ordered_item_id", orderId+"");
            String myUrl = builder.build().toString();

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.PUT,
                    myUrl,
                    (JSONObject) null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });

            Singleton.getInstance().addToRequestQueue(jsObjRequest);
    }



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
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

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

                    if(mTabs.size() > 0){
                        noOrdersDialog.setVisibility(View.GONE);
                    }
                    else{
                        noOrdersDialog.setVisibility(View.VISIBLE);
                    }

                    evaluateStatuses();

                }
                else{
                    //Error response
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };



    // ------- BroadCast Receivers -----//

    private void initUpdateKitchenListener() {

        mUpdateKitchenUI = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("Received update location UI broadcast");
                getKitchenData();

            }
        };

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mUpdateKitchenUI,
                new IntentFilter("update-kitchen"));
    }



}