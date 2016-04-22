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

public class FragmentTakeout extends Fragment {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_takeout, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.takeout_recyclerview);

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
            iconRowLength = 8;
            layoutHeight = (int)(metrics.heightPixels*.1);
            layoutWidth = (int)(metrics.widthPixels*.1);
            System.out.println("layoutHeight: "+layoutHeight);
            System.out.println("layoutWidth: "+layoutWidth);
            System.out.println("metrics.widthPixels/iconRowLength: "+metrics.widthPixels/iconRowLength);

        }else{
            iconRowLength = 9;
            layoutHeight = (int)(metrics.heightPixels*.1);
            layoutWidth = (int)(metrics.widthPixels*.2);
            System.out.println("layoutHeight: "+layoutHeight);
            System.out.println("layoutWidth: "+layoutWidth);
            System.out.println("metrics.widthPixels/iconRowLength: "+metrics.widthPixels/iconRowLength);
        }

        ArrayList<String> dataSet = new ArrayList<>();

        for(int a = 1; a <= 10; a++)
            dataSet.add("Takeout "+a);


        gridLayout = new GridLayoutManager(this.getActivity(), iconRowLength);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayout);

        MyAdapter rcAdapter = new MyAdapter(getActivity(), dataSet, R.layout.tile_table, new ViewGroup.LayoutParams(
                layoutWidth,
                layoutHeight));
        recyclerView.setAdapter(rcAdapter);
    }


}