package supportclasses;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ucsandroid.profitable.ActivityOrderView;
import com.ucsandroid.profitable.R;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>  {

    private int mLayout;
    private ArrayList<String> mDataset;
    private ViewGroup.LayoutParams mParams;
    static Context mContext;



    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mTextView;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.tile_text);
            v.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            System.out.println("Clicked: "+getAdapterPosition());
            Intent orderViewActivity = new Intent(mContext, ActivityOrderView.class);
            mContext.startActivity(orderViewActivity);
        }

    }

    public MyAdapter(Context context, ArrayList myDataset, int layout, ViewGroup.LayoutParams params) {
        mDataset = myDataset;
        mContext = context;
        mLayout = layout;
        mParams = params;
    }

    public MyAdapter(Context context, ArrayList myDataset, int layout) {
        mDataset = myDataset;
        mContext = context;
        mLayout = layout;
        mParams = null;
    }


    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(mLayout, parent, false);

        if(mParams == null){

        }else{
            v.getLayoutParams().height = mParams.height;
            v.getLayoutParams().width = mParams.width;
        }



        ViewHolder vh = new ViewHolder(v);



        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.mTextView.setText(mDataset.get(position));

    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}