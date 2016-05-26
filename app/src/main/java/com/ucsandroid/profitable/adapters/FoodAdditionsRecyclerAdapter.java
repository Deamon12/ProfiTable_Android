package com.ucsandroid.profitable.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ucsandroid.profitable.R;
import com.ucsandroid.profitable.listeners.RecyclerViewCheckListener;
import com.ucsandroid.profitable.listeners.RecyclerViewClickListener;
import com.ucsandroid.profitable.serverclasses.FoodAddition;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class FoodAdditionsRecyclerAdapter extends RecyclerView.Adapter<FoodAdditionsRecyclerAdapter.ViewHolder> {

    private double taxRate = 7.5;
    private Locale currentLocale;
    private Currency currentCurrency;
    private NumberFormat currencyFormatter;

    private int layout;
    private List<FoodAddition> additionList;

    private ViewGroup.LayoutParams params;
    private RecyclerViewClickListener clickListener;
    private RecyclerViewCheckListener checkListener;
    private Context context;

    public FoodAdditionsRecyclerAdapter(Context context, List<FoodAddition> dataSet, int layout, ViewGroup.LayoutParams params, RecyclerViewCheckListener checkListener) {
        additionList = dataSet;
        this.context = context;
        this.layout = layout;
        this.params = params;
        this.checkListener = checkListener;

        //temp US currency
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String localeLang = settings.getString("locale_lang", "en");
        String localeCountry = settings.getString("locale_country", "us");
        taxRate = settings.getLong("tax_rate", 75)*.001;

        currentLocale = new Locale(localeLang, localeCountry);
        currentCurrency = Currency.getInstance(currentLocale);
        currencyFormatter = NumberFormat.getCurrencyInstance(currentLocale);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {


        public TextView mTextView;
        public TextView mTextView2;
        public CheckBox mCheckBox;

        public ViewHolder(View v) {
            super(v);

            if(layout == R.layout.item_checkbox){
                mCheckBox = (CheckBox) v.findViewById(R.id.checkbox);
                mCheckBox.setOnCheckedChangeListener(this);
            }
            else if(layout == R.layout.item_textview_textview){
                mTextView = (TextView) v.findViewById(R.id.tile_text);
                mTextView2 = (TextView) v.findViewById(R.id.tile_text2);
            }
            else{
                mTextView = (TextView) v.findViewById(R.id.tile_text);
            }

            v.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {

            if (clickListener != null) {
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            additionList.get(getAdapterPosition()).setChecked(isChecked);
        }
    }



    public FoodAdditionsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

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

        if(layout == R.layout.item_checkbox){


            holder.mCheckBox.setText(additionList.get(position).getName());
            holder.mCheckBox.setChecked(additionList.get(position).isChecked());

            if(additionList.get(position).isDefault())
                holder.mCheckBox.setTextColor(context.getResources().getColor(R.color.primary_dark));
            else
                holder.mCheckBox.setTextColor(context.getResources().getColor(R.color.primary_text));


        }
        else if(layout == R.layout.item_textview_textview){

            holder.mTextView.setText(additionList.get(position).getName());

            double additionPrice = additionList.get(position).getPrice()/100;
            holder.mTextView2.setText(currencyFormatter.format(additionPrice));

        }


    }

    @Override
    public int getItemCount() {
        return additionList.size();
    }

    public List<FoodAddition> getDataSet(){
        return additionList;
    }

    public List<FoodAddition> getCheckedDataSet(){
        List<FoodAddition> checkedList = new ArrayList<>();
        for(FoodAddition item : additionList){
            if(item.isChecked())
                checkedList.add(item);
        }
        return checkedList;
    }

}