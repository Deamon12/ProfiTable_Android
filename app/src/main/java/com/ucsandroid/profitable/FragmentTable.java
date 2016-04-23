package com.ucsandroid.profitable;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class FragmentTable extends Fragment {

    private RecyclerView mRecyclerView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_table, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.table_recyclerview);
        initRecyclerView();

        return view;
    }

    private void initRecyclerView() {

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int iconRowLength;
        int layoutHeight, layoutWidth;
        int orientation = getResources().getConfiguration().orientation;

        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            iconRowLength = 6;
            layoutHeight = (int)(metrics.heightPixels*.15);
            layoutWidth = (int)(metrics.widthPixels*.1);


        }else{
            iconRowLength = 9;
            layoutHeight = (int)(metrics.heightPixels*.1);
            layoutWidth = (int)(metrics.widthPixels*.2);
        }




        ArrayList<String> dataSet = new ArrayList<>();

        for(int a = 1; a <= 37; a++)
            dataSet.add("Table "+a);


        GridLayoutManager gridLayout = new GridLayoutManager(this.getActivity(), iconRowLength);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(gridLayout);

        MyAdapter rcAdapter = new MyAdapter(getActivity(), dataSet, R.layout.tile_table, new ViewGroup.LayoutParams(
                layoutWidth,
                layoutHeight));
        mRecyclerView.setAdapter(rcAdapter);


    }




}

