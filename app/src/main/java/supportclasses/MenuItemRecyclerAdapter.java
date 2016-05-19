package supportclasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ucsandroid.profitable.R;

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


    private ArrayList<MenuItem> dataSet;
    private JSONArray jsonDataset;

    private ViewGroup.LayoutParams params;
    private RecyclerViewClickListener clickListener;
    private RecyclerViewLongClickListener longClickListener;
    private Context context;
    private int parentPosition = -1;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView mTextView;
        public TextView mTextView2;

        public ViewHolder(View v) {
            super(v);

            mTextView = (TextView) v.findViewById(R.id.tile_text);

            if(layout == R.layout.item_textview_textview){
                mTextView2 = (TextView) v.findViewById(R.id.tile_text2);
            }


            v.setOnClickListener(this);
            v.setOnLongClickListener(this);

        }


        @Override
        public void onClick(View v) {

            if (clickListener != null) {
                    clickListener.recyclerViewListClicked(v, parentPosition, getAdapterPosition(), dataSet.get(getAdapterPosition()));
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if(longClickListener != null)
                    longClickListener.recyclerViewListLongClicked(v, parentPosition, getAdapterPosition(), dataSet.get(getAdapterPosition()));

            return true;
        }
    }

    public MenuItemRecyclerAdapter(Context context, ArrayList<MenuItem> dataSet, int layout, int parentPosition, ViewGroup.LayoutParams params,
                                   RecyclerViewClickListener clickListener, RecyclerViewLongClickListener longClickListener) {
        this.dataSet = dataSet;
        this.context = context;
        this.layout = layout;
        this.parentPosition = parentPosition;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
        this.params = params;

    }

    public MenuItemRecyclerAdapter(Context context, JSONArray dataSet, int layout, int parentPosition, ViewGroup.LayoutParams params,
                                   RecyclerViewClickListener clickListener, RecyclerViewLongClickListener longClickListener) {
        this.jsonDataset = dataSet;
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
        currentCurrency = Currency.getInstance(currentLocale);
        currencyFormatter = NumberFormat.getCurrencyInstance(currentLocale);
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

        try {
            holder.mTextView.setText(dataSet.get(position).getJsonItem().getString("menuName"));

            if(holder.mTextView2 != null && dataSet.get(position).getJsonItem().has("menuItemPrice")){
                holder.mTextView2.setText((dataSet.get(position).getJsonItem().getInt("menuItemPrice")/100)+"");
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