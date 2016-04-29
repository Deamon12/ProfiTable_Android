package com.ucsandroid.profitable;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        ArrayList<String> dataSet = new ArrayList<>();

        for(int a = 1; a <= 10; a++)
            dataSet.add("Takeout "+a);


        gridLayout = new GridLayoutManager(this.getActivity(), iconRowLength);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayout);

        MyTakeoutAdapter rcAdapter = new MyTakeoutAdapter(getActivity(), dataSet, R.layout.tile_takeout, new ViewGroup.LayoutParams(
                layoutWidth,
                layoutHeight));
        recyclerView.setAdapter(rcAdapter);
    }


}

class MyTakeoutAdapter extends RecyclerView.Adapter<MyTakeoutAdapter.ViewHolder> {

    private int mLayout;
    private ArrayList<String> mDataset;
    private ViewGroup.LayoutParams mParams;
    Context mContext;



    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.tile_name);
        }

    }

    public static class ViewHolderSpecial extends RecyclerView.ViewHolder {

        public ViewHolderSpecial(View v) {
            super(v);
            //mTextView = (TextView) v.findViewById(R.id.table_tile_name);
        }

    }

    public MyTakeoutAdapter(Context context, ArrayList myDataset, int layout, ViewGroup.LayoutParams params) {
        mDataset = myDataset;
        mContext = context;
        mLayout = layout;
        mParams = params;
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
            holder.mTextView.setText(mDataset.get(position));

    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}