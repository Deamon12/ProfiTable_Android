package com.ucsandroid.profitable.supportclasses;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ucsandroid.profitable.R;
import com.ucsandroid.profitable.Singleton;

import org.json.JSONException;

import java.util.ArrayList;

public class LocationRecyclerAdapter extends RecyclerView.Adapter<LocationRecyclerAdapter.ViewHolder> {

    private int layout;
    private ArrayList<Location> dataSet;

    private ViewGroup.LayoutParams params;
    private RecyclerViewClickListener clickListener;
    private Context context;


    public LocationRecyclerAdapter(Context context, ArrayList<Location> dataSet, int layout, ViewGroup.LayoutParams params, RecyclerViewClickListener clickListener) {
        this.dataSet = dataSet;
        this.context = context;
        this.layout = layout;
        this.params = params;
        this.clickListener = clickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public CardView mCardView;
        public TextView mTextView;

        public ViewHolder(View v) {
            super(v);

            mCardView = (CardView) v.findViewById(R.id.the_cardview);
            mTextView = (TextView) v.findViewById(R.id.tile_text);

            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            if (clickListener != null) {
                clickListener.recyclerViewListClicked(v, -1, getAdapterPosition(), null);
            }
        }

    }


    public LocationRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

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

        if(dataSet.get(position).hasCustomer()){
            holder.mCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_light));
        }
        else{
            holder.mCardView.setCardBackgroundColor(0);
        }


        holder.mTextView.setText(""+(position+1));

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}