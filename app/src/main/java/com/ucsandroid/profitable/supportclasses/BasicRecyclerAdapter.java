package com.ucsandroid.profitable.supportclasses;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ucsandroid.profitable.R;

import java.util.ArrayList;

public class BasicRecyclerAdapter extends RecyclerView.Adapter<BasicRecyclerAdapter.ViewHolder> {

    private int layout;
    private ArrayList<String> dataSet;

    private ViewGroup.LayoutParams params;
    private RecyclerViewClickListener clickListener;
    private RecyclerViewCheckListener checkListener;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView mTextView;

        public ViewHolder(View v) {
            super(v);

            mTextView = (TextView) v.findViewById(R.id.tile_text);

            v.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {

            if (clickListener != null) {
                //System.out.println("BasicRecycler: " + getAdapterPosition());

                    clickListener.recyclerViewListClicked(v, -1, getAdapterPosition(), null);

            }
        }

    }


    public BasicRecyclerAdapter(Context context, ArrayList<String> dataSet, int layout, ViewGroup.LayoutParams params, RecyclerViewCheckListener checkListener) {
        this.dataSet = dataSet;
        this.context = context;
        this.layout = layout;
        this.params = params;
        this.checkListener = checkListener;
    }

    public BasicRecyclerAdapter(Context context, ArrayList<String> dataSet, int layout, ViewGroup.LayoutParams params, RecyclerViewClickListener clickListener) {
        this.dataSet = dataSet;
        this.context = context;
        this.layout = layout;
        this.params = params;
        this.clickListener = clickListener;
    }


    public BasicRecyclerAdapter(Context context, ArrayList<String> dataSet, int layout) {
        this.dataSet = dataSet;
        this.context = context;
        this.layout = layout;
        params = null;
    }


    public BasicRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        if (params == null) {

        } else {
            v.getLayoutParams().height = params.height;
            v.getLayoutParams().width = params.width;
        }

        ViewHolder vh = new ViewHolder(v);


        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.mTextView.setText(dataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }



}