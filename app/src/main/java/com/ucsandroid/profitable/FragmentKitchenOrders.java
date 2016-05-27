package com.ucsandroid.profitable;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.ucsandroid.profitable.adapters.NestedKitchenRecyclerAdapter;
import com.ucsandroid.profitable.listeners.NestedClickListener;
import com.ucsandroid.profitable.serverclasses.Customer;
import com.ucsandroid.profitable.serverclasses.OrderedItem;
import com.ucsandroid.profitable.serverclasses.Tab;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentKitchenOrders extends Fragment {

    private RecyclerView recyclerView;
    private List<Tab> mTabs;
    private NestedKitchenRecyclerAdapter mAdapter;
    private int volleyCounter = 0;

    public static FragmentKitchenOrders newInstance(String tabList) {
        FragmentKitchenOrders thisFrag = new FragmentKitchenOrders();

        Bundle args = new Bundle();
        args.putString("tabList", tabList);
        thisFrag.setArguments(args);

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

        try {
            parseTabs();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    /**
     * Parse input data from JSON to Objects
     * @throws JSONException
     */
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



        //Use dimension based on device size
        TypedValue outValue = new TypedValue();

        int orientation = getResources().getConfiguration().orientation;
        int layoutWidth;
        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            getResources().getValue(R.dimen.kitchen_order_width_landscape, outValue, true);
            layoutWidth = (int)(metrics.widthPixels*outValue.getFloat()); //.15
        }
        else{
            getResources().getValue(R.dimen.kitchen_order_width_landscape, outValue, true);
            layoutWidth = (int)(metrics.widthPixels*outValue.getFloat()); //.1
        }


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new NestedKitchenRecyclerAdapter(getActivity(),
                mTabs,
                R.layout.tile_kitchen_order,
                new ViewGroup.LayoutParams(layoutWidth, ViewGroup.LayoutParams.WRAP_CONTENT),
                tabClickListener);

        recyclerView.setAdapter(mAdapter);
    }

    /**
     * Pass clicks back to this fragment from the local adapter
     */
    NestedClickListener tabClickListener = new NestedClickListener() {

        @Override
        public void nestedClickListener(int position, Tab tab) {
            System.out.println("In nested click listener");
            setOrderStatus(position);
        }
    };

    /**
     * Get Tab item from adapter and parse thru it to get OrderId's
     * Need orderId of each item to set status for each item
     * @param position
     */
    private void setOrderStatus(int position){

        List<Integer> orderedIds = getOrderedItemIds(position);
        doStatusVolleyCalls(orderedIds);

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

                orderedIds.add(item.getOrderedItemId());
            }
        }
        return orderedIds;
    }

    /**
     * Use the list of orderId's to create a series of volley calls that inidividually update the
     * statuses of each item.
     * @param orderIds
     */
    private void doStatusVolleyCalls(List<Integer> orderIds) {


        volleyCounter = orderIds.size();

        for(Integer orderId : orderIds){

            System.out.println("In volley call");

            Uri.Builder builder = Uri.parse("http://52.38.148.241:8080").buildUpon();
            builder.appendPath("com.ucsandroid.profitable")
                    .appendPath("rest")
                    .appendPath("orders")
                    .appendPath("item")
                    .appendPath("ready")
                    .appendQueryParameter("ordered_item_id", orderId+"");
            String myUrl = builder.build().toString();

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.PUT,
                    myUrl,
                    (JSONObject) null,
                    successListener,
                    errorListener);

            Singleton.getInstance().addToRequestQueue(jsObjRequest);

        }

    }

    private Response.Listener successListener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {

            try {
                JSONObject theResponse = new JSONObject(response.toString());

                if(theResponse.getBoolean("success")){
                    volleyCounter -= 1;
                    System.out.println("volleycounter: "+volleyCounter);

                    if(volleyCounter == 0){
                        System.out.println("Status updates complete...do something");


                    }
                    else{
                        System.out.println("Waiting for other volleys to finish");
                    }
                }
                else{
                    //TODO:Results Error
                    System.out.println("volley error: "+theResponse);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            ///TODO Connect/server error
            System.out.println("Volley error: " + error);
        }
    };


}