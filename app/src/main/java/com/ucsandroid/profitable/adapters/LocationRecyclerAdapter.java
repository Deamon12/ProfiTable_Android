package com.ucsandroid.profitable.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ucsandroid.profitable.R;
import com.ucsandroid.profitable.listeners.LocationClickListener;
import com.ucsandroid.profitable.listeners.LocationLongClickListener;
import com.ucsandroid.profitable.listeners.RecyclerViewLongClickListener;
import com.ucsandroid.profitable.serverclasses.Location;
import com.ucsandroid.profitable.listeners.RecyclerViewClickListener;

import java.util.ArrayList;

public class LocationRecyclerAdapter extends RecyclerView.Adapter<LocationRecyclerAdapter.ViewHolder> {

    private int layout;
    private ArrayList<Location> dataSet;

    private ViewGroup.LayoutParams params;
    private LocationClickListener clickListener;
    private LocationLongClickListener longClickListener;
    private Context context;

    public LocationRecyclerAdapter(Context context, ArrayList<Location> dataSet, int layout, ViewGroup.LayoutParams params, LocationClickListener clickListener,
                                   LocationLongClickListener longClickListener) {
        this.dataSet = dataSet;
        this.context = context;
        this.layout = layout;
        this.params = params;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public CardView mCardView;
        public TextView mTextView;
        private ImageView mImageView;

        public ViewHolder(View v) {
            super(v);

            mCardView = (CardView) v.findViewById(R.id.the_cardview);
            mTextView = (TextView) v.findViewById(R.id.tile_text);
            mImageView = (ImageView) v.findViewById(R.id.tile_image);

            /*
            if(layout == R.layout.tile_kitchen_order){
            }
            else if(layout == R.layout.tile_bar_new){
            }*/

            v.setOnClickListener(this);
            v.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {

            if (clickListener != null) {
                clickListener.recyclerViewListClicked(v, -1, getAdapterPosition(), null);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (longClickListener != null) {
                longClickListener.recyclerViewListClicked(v, -1, getAdapterPosition(), dataSet.get(getAdapterPosition()));
            }
            return true;
        }
    }


    public LocationRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        if (params != null) {
            v.getLayoutParams().height = params.height;
            v.getLayoutParams().width = params.width;
        }

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        //Kitchen orders
        if(layout == R.layout.tile_kitchen_order){
            holder.mTextView.setText(dataSet.get(position).getName());

        } //Tables
        else if(layout == R.layout.tile_table_new){

            holder.mTextView.setText("Table "+(position+1));

            if(dataSet.get(position).getCurrentTab().getTabId() != 0
                    || dataSet.get(position).getLocationCost() > 0) {

                holder.mImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.table_seated_75x75_blackseats));
            }
            else{
                holder.mImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.table_unseated_75x75_blackseats));
            }
        }
        else if(layout == R.layout.tile_bar_new){

            holder.mTextView.setText("Bar "+(position+1));

            if(dataSet.get(position).getCurrentTab().getTabId() != 0
                    || dataSet.get(position).getLocationCost() > 0) {

                holder.mImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bar_seated));
            }
            else{
                holder.mImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bar_unseated_75));
            }
        }



        else{


            if(dataSet.get(position).getCurrentTab().getTabId() != 0
                    || dataSet.get(position).getLocationCost() > 0){ //TODO change to status

                if(layout == R.layout.tile_table_new){
                    holder.mImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.table_seated_75x75_blackseats));
                }
                else{
                    holder.mCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_light));
                }



            }
            else{
                //holder.mCardView.setCardBackgroundColor(0);
            }

        }



    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}