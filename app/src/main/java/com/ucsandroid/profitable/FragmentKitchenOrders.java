package com.ucsandroid.profitable;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import supportclasses.MyLinearLayoutManager;
import supportclasses.OrdersAdapter;

public class FragmentKitchenOrders extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayout;

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

        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            //layoutHeight = (int)(metrics.heightPixels);
            layoutWidth = (int)(metrics.widthPixels*.3);

        }else{
            //layoutHeight = (int)(metrics.heightPixels*.1);
            layoutWidth = (int)(metrics.widthPixels*.4);
            //layoutHeight = layoutWidth;
        }



        ArrayList<String> dataSet = new ArrayList<>();

        for(int a = 1; a <= 10; a++)
            dataSet.add("Table "+a);

        MyLinearLayoutManager layoutManager
                = new MyLinearLayoutManager(this.getActivity(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


        OrdersAdapter rcAdapter = new OrdersAdapter(getActivity(), dataSet, R.layout.tile_kitchen_order,
                new ViewGroup.LayoutParams(layoutWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
        recyclerView.setAdapter(rcAdapter);
    }


    @Override
    public void onClick(View v) {



    }





}