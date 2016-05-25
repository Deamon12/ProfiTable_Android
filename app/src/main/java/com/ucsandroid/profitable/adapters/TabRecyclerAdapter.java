package com.ucsandroid.profitable.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ucsandroid.profitable.R;
import com.ucsandroid.profitable.listeners.OrderedItemClickListener;
import com.ucsandroid.profitable.listeners.RecyclerViewLongClickListener;
import com.ucsandroid.profitable.serverclasses.Customer;
import com.ucsandroid.profitable.serverclasses.FoodAddition;
import com.ucsandroid.profitable.serverclasses.OrderedItem;
import com.ucsandroid.profitable.serverclasses.Tab;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class TabRecyclerAdapter extends RecyclerView.Adapter<TabRecyclerAdapter.ViewHolder> {

    private int layout;
    private List<Customer> mCustomer;

    private ViewGroup.LayoutParams params;
    private OrderedItemClickListener clickListener;
    private RecyclerViewLongClickListener longClickListener;
    private Context context;
    private int parentPosition = -1;



    public TabRecyclerAdapter(Context context, List<Customer> customers, int layout, int parentPosition, ViewGroup.LayoutParams params,
                              OrderedItemClickListener clickListener, RecyclerViewLongClickListener longClickListener) {

        this.context = context;
        this.layout = layout;
        this.parentPosition = parentPosition;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
        this.params = params;
        mCustomer = customers;

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView mTextView;
        public TextView mTextView2;

        public ViewHolder(View v) {
            super(v);

            mTextView = (TextView) v.findViewById(R.id.tile_text);

            if(layout == R.layout.item_textview_textview || layout == R.layout.item_textview_textview2){
                mTextView2 = (TextView) v.findViewById(R.id.tile_text2);
            }

            v.setOnClickListener(this);
            v.setOnLongClickListener(this);

        }


        @Override
        public void onClick(View v) {

            if (clickListener != null) {

                //TODO clickListener.recyclerViewListClicked(v, parentPosition, getAdapterPosition(), tabItem.get(getAdapterPosition()));

            }
        }

        @Override
        public boolean onLongClick(View v) {

            //if(longClickListener != null)
            //    longClickListener.recyclerViewListLongClicked(v, parentPosition, getAdapterPosition(), dataSet.get(getAdapterPosition()));

            return true;
        }
    }

    public TabRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

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


        //Loop thru customers


        /*
        String menuItemName = tabItem.get(position).getMenuItem().getName();
        holder.mTextView.setText(menuItemName);


        String additions = "";
        List<FoodAddition> foodAdditions = orderedItems.get(position).getAdditions();
        for(int a = 0; a < foodAdditions.size(); a++){

            additions = foodAdditions.get(a).getName() + ", "+additions;
        }


        additions = additions.substring(0, additions.lastIndexOf(','));

        holder.mTextView2.setText("");//TODO need defaults to compare
        */
    }

    @Override
    public int getItemCount() {
        return mCustomer.size();
    }



}