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
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.ArrayList;

import supportclasses.MenuItem;
import supportclasses.RecyclerViewClickListener;

public class FragmentTakeout extends Fragment {

    private RecyclerView mRecyclerView;
    private GridLayoutManager gridLayout;
    private MyTakeoutAdapter mAdapter;


    private int mRecyclerViewWidth;
    private int spanCount;
    private int tileLayoutWidth;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_takeout, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.takeout_recyclerview);

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

        spanCount = getSpanCount();




        ViewTreeObserver vto = mRecyclerView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mRecyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                DisplayMetrics metrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

                int orientation = getResources().getConfiguration().orientation;

                //Cant use the recycler width, because it may be set to GONE, which would be zero width
                if(orientation == Configuration.ORIENTATION_LANDSCAPE){
                    mRecyclerViewWidth  = (int) (metrics.widthPixels*.5);
                }else{
                    mRecyclerViewWidth  = (int) (metrics.widthPixels);
                }

                tileLayoutWidth = (mRecyclerViewWidth/spanCount);

                getTableData();

            }
        });


    }


    private void getTableData() {


        ArrayList<String> dataSet = new ArrayList<>();

        for(int a = 1; a <= 30; a++)
            dataSet.add("Takeout "+a);



        gridLayout = new GridLayoutManager(getActivity(), spanCount);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(gridLayout);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new MyTakeoutAdapter(getActivity(),
                dataSet,
                R.layout.tile_takeout,
                new ViewGroup.LayoutParams(tileLayoutWidth, tileLayoutWidth),
                clickListener);

        mRecyclerView.setAdapter(mAdapter);

    }


    private int getSpanCount(){

        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        int orientation = getResources().getConfiguration().orientation;

        if (tabletSize) {
            if(orientation == Configuration.ORIENTATION_LANDSCAPE){
                return 6;
            }else
                return 8;
        } else {
            if(orientation == Configuration.ORIENTATION_LANDSCAPE)
                return 4;
            else
                return 5;
        }
    }

    RecyclerViewClickListener clickListener = new RecyclerViewClickListener() {

        @Override
        public void recyclerViewListClicked(View v, int parentPosition, int position, MenuItem item) {
            if(position == 0){

                mAdapter.dataSet.add(1, "New " + (mAdapter.getItemCount()));
                mAdapter.notifyDataSetChanged();
            }else{
                Intent orderViewActivity = new Intent(getActivity(), ActivityOrderView.class);
                getActivity().startActivity(orderViewActivity);
            }
        }
    };


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
                clickListener.recyclerViewListClicked(v, -1, getAdapterPosition(), null);
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

        if(mParams != null){
            v.getLayoutParams().height = mParams.height;
            v.getLayoutParams().width = mParams.width;
        }


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