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
import com.ucsandroid.profitable.serverclasses.Category;
import com.ucsandroid.profitable.serverclasses.MenuItem;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.ViewHolder> {

    private double taxRate = 7.5;
    private Locale currentLocale;
    private Currency currentCurrency;
    private NumberFormat currencyFormatter;

    private int layout;
    private List<MenuItem> mMenuItems;

    private ViewGroup.LayoutParams params;
    private MenuItemClickListener clickListener;
    private Context context;


    public CategoryRecyclerAdapter(Context context, Category dataSet, int layout, ViewGroup.LayoutParams params, MenuItemClickListener clickListener) {
        mMenuItems = dataSet.getMenuItems();
        this.context = context;
        this.layout = layout;
        this.params = params;
        this.clickListener = clickListener;

        //temp US currency
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String localeLang = settings.getString("locale_lang", "en");
        String localeCountry = settings.getString("locale_country", "us");
        taxRate = settings.getLong("tax_rate", 75) * .001;

        currentLocale = new Locale(localeLang, localeCountry);
        currentCurrency = Currency.getInstance(currentLocale);
        currencyFormatter = NumberFormat.getCurrencyInstance(currentLocale);

    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public TextView mTextView;
        public TextView mTextView2;

        public ViewHolder(View v) {
            super(v);


            if (layout == R.layout.item_textview_textview) {
                mTextView = (TextView) v.findViewById(R.id.tile_text);
                mTextView2 = (TextView) v.findViewById(R.id.tile_text2);
            } else {
                mTextView = (TextView) v.findViewById(R.id.tile_text);
            }

            v.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {

            if (clickListener != null) {
                clickListener.recyclerViewListClicked(v, -1, getAdapterPosition(), mMenuItems.get(getAdapterPosition()));
            }
        }

    }


    public CategoryRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

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

        if (layout == R.layout.item_textview_textview) {

            holder.mTextView.setText(mMenuItems.get(position).getName());

            double menuItemPrice = mMenuItems.get(position).getPrice() / 100;
            holder.mTextView2.setText(currencyFormatter.format(menuItemPrice));

        }

    }

    @Override
    public int getItemCount() {
        return mMenuItems.size();
    }

    public List<MenuItem> getDataSet() {
        return mMenuItems;
    }


}