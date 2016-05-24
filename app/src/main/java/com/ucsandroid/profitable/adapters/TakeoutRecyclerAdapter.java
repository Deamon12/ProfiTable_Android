package com.ucsandroid.profitable.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ucsandroid.profitable.R;
import com.ucsandroid.profitable.listeners.LocationClickListener;
import com.ucsandroid.profitable.listeners.RecyclerViewClickListener;
import com.ucsandroid.profitable.serverclasses.Location;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class TakeoutRecyclerAdapter extends RecyclerView.Adapter<TakeoutRecyclerAdapter.ViewHolder> {

    private int mLayout;
    protected List<Location> locationList;
    private ViewGroup.LayoutParams mParams;
    Context mContext;
    private LocationClickListener clickListener;


    public TakeoutRecyclerAdapter(Context context, List<Location> myDataset,
                                  int layout,ViewGroup.LayoutParams params,
                                  LocationClickListener clickListener) {

        locationList = myDataset;
        mContext = context;
        mLayout = layout;
        mParams = params;
        this.clickListener = clickListener;

    }

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



    public TakeoutRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;

        if (viewType == 0) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tile_takeout_plus, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(mLayout, parent, false);
        }

        if (mParams != null) {
            v.getLayoutParams().height = mParams.height;
            v.getLayoutParams().width = mParams.width;
        }


        ViewHolder vh = new ViewHolder(v);


        return vh;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return 0;
        } else
            return 1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (position > 0) {

            holder.mTextView.setText(locationList.get(position).getName());

        }
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public void addTakeoutItem(){
        //TODO: create takeout location with server, then save it.
        //dataSet.add(1, new Location("New takeout"));
        //notifyDataSetChanged();
    }

}