package supportclasses;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ucsandroid.profitable.R;

import org.json.JSONArray;
import org.json.JSONException;

public class MenuItemRecyclerAdapter extends RecyclerView.Adapter<MenuItemRecyclerAdapter.ViewHolder> {

    private int layout;
    //private ArrayList<MenuItem> dataSet;
    private JSONArray dataSet;
    private ViewGroup.LayoutParams params;
    private RecyclerViewClickListener clickListener;
    private RecyclerViewLongClickListener longClickListener;
    private Context context;
    private int parentPosition = -1;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView mTextView;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.tile_text);
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);

        }


        @Override
        public void onClick(View v) {

            if (clickListener != null) {
                try {
                    clickListener.recyclerViewListClicked(v, parentPosition, getAdapterPosition(), dataSet.getJSONObject(getAdapterPosition()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if(longClickListener != null)
                try {
                    longClickListener.recyclerViewListLongClicked(v, parentPosition, getAdapterPosition(), dataSet.getJSONObject(getAdapterPosition()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            return true;
        }
    }

    public MenuItemRecyclerAdapter(Context context, JSONArray dataSet, int layout, ViewGroup.LayoutParams params, RecyclerViewClickListener clickListener) {
        this.dataSet = dataSet;
        this.context = context;
        this.layout = layout;
        this.params = params;
        this.clickListener = clickListener;
    }

    public MenuItemRecyclerAdapter(Context context, JSONArray dataSet, int layout, int parentPosition, ViewGroup.LayoutParams params,
                                   RecyclerViewClickListener clickListener, RecyclerViewLongClickListener longClickListener) {
        this.dataSet = dataSet;
        this.context = context;
        this.layout = layout;
        this.parentPosition = parentPosition;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
        this.params = params;
    }

    public MenuItemRecyclerAdapter(Context context, JSONArray dataSet, int layout, int parentPosition,
                                   RecyclerViewClickListener clickListener, RecyclerViewLongClickListener longClickListener) {
        this.dataSet = dataSet;
        this.context = context;
        this.layout = layout;
        this.parentPosition = parentPosition;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }


    public MenuItemRecyclerAdapter(Context context, JSONArray dataSet, int layout) {
        this.dataSet = dataSet;
        this.context = context;
        this.layout = layout;
        params = null;
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
            holder.mTextView.setText(dataSet.getJSONObject(position).getString("menuName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return dataSet.length();
    }



}