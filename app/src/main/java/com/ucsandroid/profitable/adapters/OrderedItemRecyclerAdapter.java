package com.ucsandroid.profitable.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ucsandroid.profitable.R;
import com.ucsandroid.profitable.Singleton;
import com.ucsandroid.profitable.listeners.MenuItemClickListener;
import com.ucsandroid.profitable.listeners.OrderedItemClickListener;
import com.ucsandroid.profitable.listeners.RecyclerViewLongClickListener;
import com.ucsandroid.profitable.serverclasses.Category;
import com.ucsandroid.profitable.serverclasses.FoodAddition;
import com.ucsandroid.profitable.serverclasses.MenuItem;
import com.ucsandroid.profitable.serverclasses.OrderedItem;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class OrderedItemRecyclerAdapter extends RecyclerView.Adapter<OrderedItemRecyclerAdapter.ViewHolder> {

    private int layout;

    private Locale currentLocale;
    private NumberFormat currencyFormatter;


    private List<OrderedItem> orderedItems;

    private ViewGroup.LayoutParams params;
    private OrderedItemClickListener clickListener;
    private RecyclerViewLongClickListener longClickListener;
    private Context context;
    private int parentPosition = -1;


    public OrderedItemRecyclerAdapter(Context context, List<OrderedItem> dataSet, int layout, int parentPosition, ViewGroup.LayoutParams params,
                                      OrderedItemClickListener clickListener, RecyclerViewLongClickListener longClickListener) {
        this.orderedItems = dataSet;
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

                clickListener.recyclerViewListClicked(v, parentPosition, getAdapterPosition(), orderedItems.get(getAdapterPosition()));

            }
        }

        @Override
        public boolean onLongClick(View v) {

            //if(longClickListener != null)
            //    longClickListener.recyclerViewListLongClicked(v, parentPosition, getAdapterPosition(), dataSet.get(getAdapterPosition()));

            return true;
        }
    }

    public OrderedItemRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

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


        //Set itemName
        String menuItemName = orderedItems.get(position).getMenuItem().getName();
        holder.mTextView.setText(menuItemName);

        //Start create additions string
        int menuItemId = orderedItems.get(position).getMenuItem().getId();

        //At this point we only have additions that are selected.
        //Need to use Singleton hash, to set if the items are checked, and if they are default.
        List<FoodAddition> defaults = Singleton.getInstance().getMenuItem(menuItemId).getDefaultAdditions();
        List<FoodAddition> optionals = Singleton.getInstance().getMenuItem(menuItemId).getOptionalAdditions();
        List<FoodAddition> allAdditions = new ArrayList<>();
        List<FoodAddition> selectedAdditions = orderedItems.get(position).getAdditions();

        //Add defaults to an overall List
        for (FoodAddition item : defaults) {
            item.setIsDefault(true);
            allAdditions.add(item);
        }

        //Add optionals to an overall List
        for (FoodAddition item : optionals) {
            item.setIsDefault(false);
            allAdditions.add(item);
        }

        //Have list of ALL at this point


        //Loop through all and check the correct ones
        for(FoodAddition item : allAdditions){
            for(int a = 0; a < selectedAdditions.size(); a++){
                if(selectedAdditions.get(a).getId() == item.getId()){
                    item.setChecked(true);
                }
            }
        }


        //Loop back through the edited list (which knows if default or not), and create string;
        //Check if selected is default or not. And let the UI know via stringbuilder
        StringBuilder sb = new StringBuilder();

        for(int a = 0; a < allAdditions.size(); a++){

            if(allAdditions.get(a).isDefault() && !allAdditions.get(a).isChecked()){
                if(sb.toString().equalsIgnoreCase("")){
                    sb.append("no "+allAdditions.get(a).getName());
                }
                else
                    sb.append(", no "+allAdditions.get(a).getName());
            }else if(!allAdditions.get(a).isDefault() && allAdditions.get(a).isChecked()){
                if(sb.toString().equalsIgnoreCase("")){
                    sb.append(allAdditions.get(a).getName());
                }
                else
                    sb.append(", "+allAdditions.get(a).getName());
            }

        }

        holder.mTextView2.setText(sb.toString());
    }

    @Override
    public int getItemCount() {
        return orderedItems.size();
    }

}