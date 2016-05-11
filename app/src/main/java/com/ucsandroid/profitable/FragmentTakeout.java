package com.ucsandroid.profitable;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import supportclasses.RecyclerViewClickListener;

public class FragmentTakeout extends Fragment {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayout;
    private MyTakeoutAdapter mAdapter;

    private ArrayList<String> dataSet;
    int iconRowLength;
    int layoutHeight, layoutWidth;
    RecyclerViewClickListener clickListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_takeout, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.takeout_recyclerview);

        initRecyclerView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean shown = settings.getBoolean("takeoutFragShown", true);

        //Hide fragment based on SharedPrefs
        if(!shown)
            ((ActivityTableView)getActivity()).toggleTakeoutSection(false);

    }

    private void initRecyclerView() {

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);


        int orientation = getResources().getConfiguration().orientation;

        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            iconRowLength = 8;
            layoutHeight = (int)(metrics.heightPixels*.1);
            layoutWidth = (int)(metrics.widthPixels*.1);
            //System.out.println("layoutHeight: "+layoutHeight);
            //System.out.println("layoutWidth: "+layoutWidth);
            //System.out.println("metrics.widthPixels/iconRowLength: "+metrics.widthPixels/iconRowLength);

        }else{
            iconRowLength = 9;
            layoutHeight = (int)(metrics.heightPixels*.1);
            layoutWidth = (int)(metrics.widthPixels*.2);
            //System.out.println("layoutHeight: "+layoutHeight);
            //System.out.println("layoutWidth: "+layoutWidth);
            //System.out.println("metrics.widthPixels/iconRowLength: "+metrics.widthPixels/iconRowLength);
        }

        dataSet = new ArrayList<>();

        for(int a = 1; a <= 30; a++)
            dataSet.add("Takeout "+a);

         clickListener = new RecyclerViewClickListener() {
            @Override
            public void recyclerViewListClicked(View v, int position) {

                if(position == 0){
                    mAdapter.dataSet.add(1, "New " + dataSet.size());
                    mAdapter.notifyDataSetChanged();
                }else{
                    Intent orderViewActivity = new Intent(getActivity(), ActivityOrderView.class);
                    getActivity().startActivity(orderViewActivity);
                }

            }
        };

        gridLayout = new GridLayoutManager(this.getActivity(), iconRowLength);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayout);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new MyTakeoutAdapter(getActivity(), dataSet, R.layout.tile_takeout, new ViewGroup.LayoutParams(
                layoutWidth,
                layoutHeight),
                clickListener);
        recyclerView.setAdapter(mAdapter);
    }



}

class MyTakeoutAdapter extends RecyclerView.Adapter<MyTakeoutAdapter.ViewHolder> {

    private int mLayout;
    protected ArrayList<String> dataSet;
    private ViewGroup.LayoutParams mParams;
    Context mContext;
    private RecyclerViewClickListener clickListener;


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mTextView;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.tile_name);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (clickListener != null) {
                clickListener.recyclerViewListClicked(v, getAdapterPosition());
            }

        }

    }

    public MyTakeoutAdapter(Context context,
                            ArrayList myDataset,
                            int layout,
                            ViewGroup.LayoutParams params,
                            RecyclerViewClickListener clickListener) {
        dataSet = myDataset;
        mContext = context;
        mLayout = layout;
        mParams = params;
        this.clickListener = clickListener;
    }

    public MyTakeoutAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;

        if(viewType == 0){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tile_takeout_plus, parent, false);
        }
        else{
            v = LayoutInflater.from(parent.getContext()).inflate(mLayout, parent, false);
        }

        v.getLayoutParams().height = mParams.height;
        v.getLayoutParams().width = mParams.width;

        ViewHolder vh = new ViewHolder(v);


        return vh;
    }

    @Override
    public int getItemViewType(int position) {

        if(position == 0){
            return 0;
        }
        else
            return 1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if(position > 0)
            holder.mTextView.setText(dataSet.get(position));

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}