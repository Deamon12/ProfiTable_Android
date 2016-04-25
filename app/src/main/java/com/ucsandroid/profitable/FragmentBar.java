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

public class FragmentBar extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayout;
    private View partitionBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bar, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.bar_recyclerview);
        initRecyclerView();

        partitionBar = view.findViewById(R.id.partition_bar);
        partitionBar.setOnClickListener(this);

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


        }else{
            iconRowLength = 9;
            layoutHeight = (int)(metrics.heightPixels*.1);
            layoutWidth = (int)(metrics.widthPixels*.1);
            layoutHeight = layoutWidth;
        }

        ArrayList<String> dataSet = new ArrayList<>();

        for(int a = 1; a <= 20; a++)
            dataSet.add(""+a);


        gridLayout = new GridLayoutManager(this.getActivity(), iconRowLength);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayout);

        MyAdapter rcAdapter = new MyAdapter(getActivity(), dataSet, R.layout.tile_bar, new ViewGroup.LayoutParams(
                layoutWidth,
                layoutHeight));
        recyclerView.setAdapter(rcAdapter);
    }


    @Override
    public void onClick(View v) {

        if(v == partitionBar){
            ((ActivityTableView)getActivity()).toggleBarSection();
        }

    }


}