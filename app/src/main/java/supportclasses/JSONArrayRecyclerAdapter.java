package supportclasses;

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

import org.json.JSONArray;
import org.json.JSONException;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class JSONArrayRecyclerAdapter extends RecyclerView.Adapter<JSONArrayRecyclerAdapter.ViewHolder> {

    private double taxRate = 7.5;
    private Locale currentLocale;
    private Currency currentCurrency;
    private NumberFormat currencyFormatter;

    private int layout;
    private JSONArray dataSet;

    private ViewGroup.LayoutParams params;
    private RecyclerViewClickListener clickListener;
    private RecyclerViewCheckListener checkListener;
    private Context context;

    public JSONArrayRecyclerAdapter(Context context, JSONArray dataSet, int layout, ViewGroup.LayoutParams params, RecyclerViewCheckListener checkListener) {
        this.dataSet = dataSet;
        this.context = context;
        this.layout = layout;
        this.params = params;
        this.checkListener = checkListener;
    }

    public JSONArrayRecyclerAdapter(Context context, JSONArray dataSet, int layout, ViewGroup.LayoutParams params, RecyclerViewClickListener clickListener) {
        this.dataSet = dataSet;
        this.context = context;
        this.layout = layout;
        this.params = params;
        this.clickListener = clickListener;

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
                //System.out.println("BasicRecycler: " + getAdapterPosition());
                try {
                    clickListener.recyclerViewListClicked(v, -1, getAdapterPosition(), new MenuItem(dataSet.getJSONObject(getAdapterPosition())));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            try {
                dataSet.getJSONObject(getAdapterPosition()).put("checked", isChecked);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            checkListener.recyclerViewListChecked(buttonView, -1, getAdapterPosition(), isChecked);
        }

    }



    public JSONArrayRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

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

        if(layout == R.layout.item_checkbox){
            try {
                holder.mCheckBox.setText(dataSet.getJSONObject(position).getString("foodAdditionName"));
                holder.mCheckBox.setChecked(dataSet.getJSONObject(position).getBoolean("checked"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(layout == R.layout.item_textview_textview){

            try {

                if(dataSet.getJSONObject(position).has("name")){
                    holder.mTextView.setText(dataSet.getJSONObject(position).getString("name"));
                }
                if(dataSet.getJSONObject(position).has("menuName")){
                    holder.mTextView.setText(dataSet.getJSONObject(position).getString("menuName"));
                }
                if(dataSet.getJSONObject(position).has("menuItemPrice")){
                    double menuItemPrice = dataSet.getJSONObject(position).getDouble("menuItemPrice")/100;
                    holder.mTextView2.setText(currencyFormatter.format(menuItemPrice));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {

            try {
                if(dataSet.getJSONObject(position).has("name")){
                    holder.mTextView.setText(dataSet.getJSONObject(position).getString("name"));
                }
                else{
                    holder.mTextView.setText(dataSet.getJSONObject(position).getString("menuName"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public int getItemCount() {
        return dataSet.length();
    }

    public JSONArray getDataSet(){
        return dataSet;
    }


}