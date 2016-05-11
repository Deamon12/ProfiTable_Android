package supportclasses;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ucsandroid.profitable.R;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private int mLayout;
    ArrayList<String> dataset;
    private ViewGroup.LayoutParams mParams;
    private RecyclerViewClickListener clickListener;
    Context mContext;


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mTextView;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.tile_text);
            v.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {

            System.out.println("Clicked: "+getAdapterPosition());
            if (clickListener != null) {
                clickListener.recyclerViewListClicked(v, getAdapterPosition());
            }

        }


    }

    public MyAdapter(Context context, ArrayList myDataset, int layout, ViewGroup.LayoutParams params, RecyclerViewClickListener clickListener) {
        dataset = myDataset;
        mContext = context;
        mLayout = layout;
        mParams = params;
        this.clickListener = clickListener;
    }

    public MyAdapter(Context context, ArrayList myDataset, int layout) {
        dataset = myDataset;
        mContext = context;
        mLayout = layout;
        mParams = null;
    }


    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(mLayout, parent, false);

        if (mParams == null) {

        } else {
            v.getLayoutParams().height = mParams.height;
            v.getLayoutParams().width = mParams.width;
        }


        ViewHolder vh = new ViewHolder(v);


        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.mTextView.setText(dataset.get(position));

    }


    @Override
    public int getItemCount() {
        return dataset.size();
    }

}