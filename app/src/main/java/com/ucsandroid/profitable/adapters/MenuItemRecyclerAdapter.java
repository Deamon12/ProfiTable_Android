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
import com.ucsandroid.profitable.supportclasses.MenuItem;
import com.ucsandroid.profitable.listeners.RecyclerViewClickListener;
import com.ucsandroid.profitable.listeners.RecyclerViewLongClickListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

public class MenuItemRecyclerAdapter extends RecyclerView.Adapter<MenuItemRecyclerAdapter.ViewHolder> {

    private int layout;

    private Locale currentLocale;
    private Currency currentCurrency;
    private NumberFormat currencyFormatter;


    private ArrayList<MenuItem> dataSet = null;
    private JSONArray jsonDataset = null;

    private ViewGroup.LayoutParams params;
    private RecyclerViewClickListener clickListener;
    private RecyclerViewLongClickListener longClickListener;
    private Context context;
    private int parentPosition = -1;


    public MenuItemRecyclerAdapter(Context context, ArrayList<MenuItem> dataSet, int layout, int parentPosition, ViewGroup.LayoutParams params,
                                   RecyclerViewClickListener clickListener, RecyclerViewLongClickListener longClickListener) {
        this.dataSet = dataSet;
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



            v.setOnClickListener(this);
            v.setOnLongClickListener(this);

        }


        @Override
        public void onClick(View v) {

            if (clickListener != null) {

                if(dataSet != null)
                    clickListener.recyclerViewListClicked(v, parentPosition, getAdapterPosition(), dataSet.get(getAdapterPosition()));
                else if(jsonDataset != null) {
                    try {
                        clickListener.recyclerViewListClicked(v, parentPosition, getAdapterPosition(), new MenuItem(jsonDataset.getJSONObject(getAdapterPosition())));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        @Override
        public boolean onLongClick(View v) {
            if(longClickListener != null)
                longClickListener.recyclerViewListLongClicked(v, parentPosition, getAdapterPosition(), dataSet.get(getAdapterPosition()));

            return true;
        }
    }

    public MenuItemRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

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

        String menuItemName = "";
        String menuItemPrice = "---";
        String additionsString = "";

        try {


            JSONArray defaults = dataSet.get(position).getJsonItem().getJSONArray("defaultAdditions");
            JSONArray additions = dataSet.get(position).getAdditions();

            for(int a = 0;a < additions.length();a++){
                boolean found = false;
                //System.out.println(defaults.getJSONObject(a).getString("foodAdditionName"));

                for(int aa = 0;aa < defaults.length();aa++){

                    if(defaults.getJSONObject(aa).getString("foodAdditionName").equalsIgnoreCase(additions.getJSONObject(a).getString("foodAdditionName"))){
                        if(additions.getJSONObject(a).getBoolean("checked")){
                            //System.out.println("default: "+additions.getJSONObject(a).getString("foodAdditionName")+" = yes");
                        }
                        else{
                            //System.out.println("default: "+additions.getJSONObject(a).getString("foodAdditionName")+" : no");
                            additionsString = additionsString + " / no "+additions.getJSONObject(a).getString("foodAdditionName");
                        }
                        found = true;
                    }

                }

                //If we get here we have an item that is not a default, and might be checked>?
                if(!found && additions.getJSONObject(a).getBoolean("checked")){
                    //System.out.println("not default: "+additions.getJSONObject(a).getString("foodAdditionName")+" = yes");
                    additionsString = additionsString + " / "+additions.getJSONObject(a).getString("foodAdditionName");
                }

            }



        } catch (JSONException e) {
            e.printStackTrace();
        }



        if(!additionsString.equalsIgnoreCase(""))
            additionsString = additionsString.substring(3);




        try {

            //ServerMenuItem name
            menuItemName = dataSet.get(position).getJsonItem().getString("menuName");
            holder.mTextView.setText(menuItemName);

            //ServerMenuItem with price (menuItemViewpager)
            if(layout == R.layout.item_textview_textview){

                if (dataSet.get(position).getJsonItem().has("menuItemPrice")) {
                    double tempMenuItemPrice = dataSet.get(position).getJsonItem().getDouble("menuItemPrice") / 100;
                    menuItemPrice = currencyFormatter.format(tempMenuItemPrice);
                    holder.mTextView2.setText(menuItemPrice+"");
                }

            }//ServerMenuItem with attributes (orderView)
            else if(layout == R.layout.item_textview_textview2){
                holder.mTextView2.setText(additionsString);


            }




        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }



}