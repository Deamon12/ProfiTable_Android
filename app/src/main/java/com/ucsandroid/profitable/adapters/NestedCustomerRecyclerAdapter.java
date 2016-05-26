package com.ucsandroid.profitable.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ucsandroid.profitable.R;
import com.ucsandroid.profitable.listeners.OrderedItemClickListener;
import com.ucsandroid.profitable.listeners.RecyclerViewLongClickListener;
import com.ucsandroid.profitable.serverclasses.Customer;
import com.ucsandroid.profitable.serverclasses.MenuItem;
import com.ucsandroid.profitable.serverclasses.OrderedItem;
import com.ucsandroid.profitable.supportclasses.MyLinearLayoutManager;

import java.util.List;

public class NestedCustomerRecyclerAdapter extends RecyclerView.Adapter<NestedCustomerRecyclerAdapter.ViewHolder> {

    private int layout;

    private List<Customer> customerData;

    private ViewGroup.LayoutParams layoutParams;
    private static Context context;
    private OrderedItemClickListener nestedClickListener;
    private RecyclerViewLongClickListener nestedLongClickListener;
    private int parentPosition = 0;



    public NestedCustomerRecyclerAdapter(Context context, List<Customer> dataSet, int layout, int parentPostion, ViewGroup.LayoutParams params, OrderedItemClickListener clickListener) {
        customerData = dataSet;
        this.context = context;
        this.layout = layout;
        this.parentPosition = parentPostion;
        this.layoutParams = params;
        this.nestedClickListener = clickListener;
        this.nestedLongClickListener = longClickListener;

    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView mCommentTextView;
        public TextView mTextView;
        public RecyclerView recyclerView;
        private CardView cardView;
        private OrderedItemRecyclerAdapter mAdapter;

        public ViewHolder(View v) {
            super(v);

            mTextView = (TextView) v.findViewById(R.id.tile_text);


            if(layout == R.layout.tile_recyclerview){

                recyclerView = (RecyclerView) v.findViewById(R.id.the_recycler);
                recyclerView.setHasFixedSize(false);
                recyclerView.setLayoutManager(new MyLinearLayoutManager(context));

            }
            else if(layout == R.layout.tile_kitchen_order){

                mCommentTextView = (TextView) v.findViewById(R.id.comment_text);

                cardView = (CardView) v.findViewById(R.id.the_cardview);

                recyclerView = (RecyclerView) v.findViewById(R.id.item_recycler);
                recyclerView.setHasFixedSize(false);
                recyclerView.setLayoutManager(new MyLinearLayoutManager(context));

                v.setOnClickListener(this);
                v.setOnLongClickListener(this);

                cardView.setOnClickListener(this);
            }
            else{

            }


        }


        @Override
        public void onClick(View v) {


        }

        @Override
        public boolean onLongClick(View v) {

            return true;
        }
    }



    public NestedCustomerRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        if (layoutParams != null) {
            v.getLayoutParams().height = layoutParams.height;
            v.getLayoutParams().width = layoutParams.width;
        }

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {



        //holder.mCommentTextView.setText(customerData.get(position));

        //holder.mTextView.setText("Table " + (position+1));



        holder.mAdapter = new OrderedItemRecyclerAdapter(context,
                customerData.get(position).getOrders(),
                R.layout.item_textview_textview2,
                position,
                null,
                null,
                null);

        holder.recyclerView.setAdapter(holder.mAdapter);


    }

    @Override
    public int getItemCount() {
        return customerData.size();
    }


    /**
     * Pass clicks from nested recyclerview through parent recyclerview, to fragment
     */
    OrderedItemClickListener orderedItemClickListener = new OrderedItemClickListener() {
        @Override
        public void recyclerViewListClicked(View v, int parentPosition, int position, OrderedItem item) {
            nestedClickListener.recyclerViewListClicked(v, parentPosition, position, item);
        }
    };

    /**
     * Pass clicks from nested recyclerview through parent recyclerview, to fragment
     */
    RecyclerViewLongClickListener longClickListener = new RecyclerViewLongClickListener() {

        @Override
        public void recyclerViewListLongClicked(View v, int parentPosition, int position, MenuItem item) {
            nestedLongClickListener.recyclerViewListLongClicked(v, parentPosition, position, item);
        }
    };


}