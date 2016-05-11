package supportclasses;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ucsandroid.profitable.R;

import java.util.ArrayList;

public class OrderRecyclerAdapter extends RecyclerView.Adapter<OrderRecyclerAdapter.ViewHolder> {

    private int layout;
    private ArrayList<MenuItem> dataSet;
    private ViewGroup.LayoutParams params;
    private RecyclerViewClickListener clickListener;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mTextView;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.tile_text);
            v.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {

            if (clickListener != null) {
                System.out.println("BasicRecycler: " + getAdapterPosition());

                    clickListener.recyclerViewListClicked(v, getAdapterPosition(), dataSet.get(getAdapterPosition()).getName());
            }
        }

    }

    public OrderRecyclerAdapter(Context context, ArrayList dataSet, int layout, ViewGroup.LayoutParams params, RecyclerViewClickListener clickListener) {
        this.dataSet = dataSet;
        this.context = context;
        this.layout = layout;
        this.params = params;
        this.clickListener = clickListener;
        System.out.println("Class: "+dataSet.get(0).getClass().equals(String.class));
    }


    public OrderRecyclerAdapter(Context context, ArrayList dataSet, int layout) {
        this.dataSet = dataSet;
        this.context = context;
        this.layout = layout;
        params = null;
    }


    public OrderRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

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
            holder.mTextView.setText(dataSet.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public String getDataSetTitle(int position){
            return dataSet.get(position).toString();
    }


}