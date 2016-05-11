package supportclasses;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ucsandroid.profitable.R;

import java.util.ArrayList;
import java.util.Random;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder>  {

    private int mLayout;
    private ArrayList<String> mDataset;
    private ViewGroup.LayoutParams mParams;
    static Context mContext;
    ArrayList<String> itemSet;


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mTextView;
        public RecyclerView recyclerView;

        public ViewHolder(View v) {
            super(v);

            mTextView = (TextView) v.findViewById(R.id.tile_name_text);
            recyclerView = (RecyclerView) v.findViewById(R.id.item_recycler);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new MyLinearLayoutManager(mContext));
            v.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            System.out.println("Clicked: "+getAdapterPosition());
            //Intent orderViewActivity = new Intent(mContext, ActivityOrderView.class);
            //mContext.startActivity(orderViewActivity);
        }

    }

    public OrdersAdapter(Context context, ArrayList myDataset, int layout, ViewGroup.LayoutParams params) {
        mDataset = myDataset;
        mContext = context;
        mLayout = layout;
        mParams = params;


    }


    public OrdersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(mLayout, parent, false);

        if(mParams == null){

        }
        else{
            //v.getLayoutParams().height = mParams.height;
            v.getLayoutParams().width = mParams.width;
        }


        ViewHolder vh = new ViewHolder(v);

        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if(mLayout == R.layout.tile_kitchen_order)
            holder.mTextView.setText(mDataset.get(position));
        else if(mLayout == R.layout.tile_customer_order)
            holder.mTextView.setText(mDataset.get(position));

        int count = new Random().nextInt(10);
        itemSet = new ArrayList<>();
        for(int a = 1; a <= count; a++)
            itemSet.add(""+a);

        MyAdapter rcAdapter = new MyAdapter(mContext, itemSet, R.layout.item_textview_imageview);

        holder.recyclerView.setAdapter(rcAdapter);


    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}