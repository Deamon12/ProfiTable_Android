package com.ucsandroid.profitable;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import supportclasses.MenuItem;
import supportclasses.RecyclerViewClickListener;
import supportclasses.TableRecyclerAdapter;


/**
 * Fragment that displays a grid of table icons that
 * are related to a table object with orders attached to it.
 */
public class FragmentTable extends Fragment {

    private RecyclerView mRecyclerView;
    private TableRecyclerAdapter mAdapter;
    int iconRowLength;
    int tileLayoutHeight, tileLayoutWidth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (!Singleton.hasBeenInitialized()) {
            Singleton.initialize(getActivity());
        }

        View view = inflater.inflate(R.layout.fragment_table, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.table_recyclerview);
        initRecyclerView();

        return view;
    }


    private void initRecyclerView() {

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int orientation = getResources().getConfiguration().orientation;

        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            iconRowLength = 6;
            tileLayoutHeight = (int)(metrics.heightPixels*.1);
            tileLayoutWidth = (int)(metrics.widthPixels*.1);
            tileLayoutWidth = tileLayoutHeight;


        }else{
            iconRowLength = 8;
            tileLayoutHeight = (int)(metrics.heightPixels*.1);
            tileLayoutWidth = (int)(metrics.widthPixels*.1);
            tileLayoutHeight = tileLayoutWidth;
        }


        getTableData();


    }


    /**
     * Volley call to acquire table data
     */
    private void getTableData() {

        GridLayoutManager gridLayout = new GridLayoutManager(getActivity(), iconRowLength);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(gridLayout);

        mAdapter = new TableRecyclerAdapter(getActivity(), Singleton.getInstance().getAllTables(), R.layout.tile_table, new ViewGroup.LayoutParams(
                tileLayoutWidth,
                tileLayoutHeight),
                clickListener);
        mRecyclerView.setAdapter(mAdapter);

    }


    /**
     * Click interface for adapter
     */
    RecyclerViewClickListener clickListener = new RecyclerViewClickListener() {

        @Override
        public void recyclerViewListClicked(View v, int parentPosition, int position, MenuItem item) {
            Intent orderViewActivity = new Intent(getActivity(), ActivityOrderView.class);

            Singleton.getInstance().setCurrentTable(position);
            getActivity().startActivity(orderViewActivity);


        }

    };


}

