package com.ucsandroid.profitable;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import supportclasses.MyAdapter;
import supportclasses.RecyclerViewClickListener;

public class FragmentBar extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bar, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.bar_recyclerview);
        initRecyclerView();



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean shown = settings.getBoolean("barFragShown", true);

        //Hide fragment based on SharedPrefs
        if(!shown)
            ((ActivityTableView)getActivity()).toggleBarSection(false);

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

        for(int a = 1; a <= 50; a++)
            dataSet.add(""+a);


        RecyclerViewClickListener clickListener = new RecyclerViewClickListener() {
            @Override
            public void recyclerViewListClicked(View v, int position) {
                Intent orderViewActivity = new Intent(getActivity(), ActivityOrderView.class);
                getActivity().startActivity(orderViewActivity);
            }
        };

        gridLayout = new GridLayoutManager(this.getActivity(), iconRowLength);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayout);

        MyAdapter rcAdapter = new MyAdapter(getActivity(), dataSet, R.layout.tile_bar, new ViewGroup.LayoutParams(
                layoutWidth,
                layoutHeight),
                clickListener);

        recyclerView.setAdapter(rcAdapter);
    }


    @Override
    public void onClick(View v) {



    }


}