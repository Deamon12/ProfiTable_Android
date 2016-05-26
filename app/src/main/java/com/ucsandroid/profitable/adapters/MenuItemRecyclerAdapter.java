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
import com.ucsandroid.profitable.listeners.MenuItemClickListener;
import com.ucsandroid.profitable.serverclasses.FoodAddition;
import com.ucsandroid.profitable.serverclasses.MenuItem;
import com.ucsandroid.profitable.listeners.RecyclerViewLongClickListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class MenuItemRecyclerAdapter extends RecyclerView.Adapter<MenuItemRecyclerAdapter.ViewHolder> {

    private int layout;

    private Locale currentLocale;
    private Currency currentCurrency;
    private NumberFormat currencyFormatter;

    private List<MenuItem> menuItemsData;

    private ViewGroup.LayoutParams params;
    private MenuItemClickListener clickListener;
    private RecyclerViewLongClickListener longClickListener;
    private Context context;
    private int parentPosition = -1;


    public MenuItemRecyclerAdapter(Context context, List<MenuItem> dataSet, int layout, int parentPosition, ViewGroup.LayoutParams params,
                                   MenuItemClickListener clickListener, RecyclerViewLongClickListener longClickListener) {
        this.menuItemsData = dataSet;
        this.context = context;
        this.layout = layout;
        this.parentPosition = parentPosition;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
        this.params = params;

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String localeLang = settings.getString("locale_lang", "en");
        String localeCountry = settings.getString("locale_country", "us");

        currentLocale = new Locale(localeLang, localeCountry);
        //currentCurrency = Currency.getInstance(currentLocale);
        currencyFormatter = NumberFormat.getCurrencyInstance(currentLocale);
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
            else if(layout == R.layout.tile_kitchen_amount){
                mTextView2 = (TextView) v.findViewById(R.id.tile_text2);
            }




            v.setOnClickListener(this);
            v.setOnLongClickListener(this);

        }


        @Override
        public void onClick(View v) {

            if (clickListener != null) {

                clickListener.recyclerViewListClicked(v, parentPosition, getAdapterPosition(), menuItemsData.get(getAdapterPosition()));

            }
        }

        @Override
        public boolean onLongClick(View v) {

            //if(longClickListener != null)
            //    longClickListener.recyclerViewListLongClicked(v, parentPosition, getAdapterPosition(), dataSet.get(getAdapterPosition()));

            return true;
        }
    }

    public MenuItemRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

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

        if(layout == R.layout.tile_kitchen_amount){
            holder.mTextView.setText(menuItemsData.get(position).getName());
            holder.mTextView2.setText(menuItemsData.get(position).getQuantity()+"");
        }


    }

    @Override
    public int getItemCount() {
        return menuItemsData.size();
    }



}