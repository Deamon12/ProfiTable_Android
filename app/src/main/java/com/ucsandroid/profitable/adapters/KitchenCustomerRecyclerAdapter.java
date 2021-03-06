package com.ucsandroid.profitable.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ucsandroid.profitable.R;
import com.ucsandroid.profitable.listeners.NestedClickListener;
import com.ucsandroid.profitable.listeners.OrderedItemClickListener;
import com.ucsandroid.profitable.listeners.RecyclerViewLongClickListener;
import com.ucsandroid.profitable.serverclasses.Customer;
import com.ucsandroid.profitable.serverclasses.OrderedItem;
import com.ucsandroid.profitable.supportclasses.MyLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class KitchenCustomerRecyclerAdapter extends RecyclerView.Adapter<KitchenCustomerRecyclerAdapter.ViewHolder> {

    private int layout;
    private int childLayout = -1;

    private List<Customer> customerData;
    private List<Customer> oldCustomerData = new ArrayList<>();

    private ViewGroup.LayoutParams layoutParams;
    private static Context context;
    private OrderedItemClickListener nestedClickListener;
    private RecyclerViewLongClickListener nestedLongClickListener;
    private int parentPosition = 0;



    public KitchenCustomerRecyclerAdapter(Context context, List<Customer> dataSet, int layout, int parentPostion, ViewGroup.LayoutParams params, OrderedItemClickListener clickListener) {
        customerData = dataSet;
        this.context = context;
        this.layout = layout;
        this.parentPosition = parentPostion;
        this.layoutParams = params;
        this.nestedClickListener = clickListener;

    }

    public KitchenCustomerRecyclerAdapter(Context context,
                                          List<Customer> dataSet,
                                          int layout,
                                          int parentPostion,
                                          ViewGroup.LayoutParams params,
                                          OrderedItemClickListener clickListener,
                                          int childLayout) {
        customerData = dataSet;
        this.context = context;
        this.layout = layout;
        this.parentPosition = parentPostion;
        this.layoutParams = params;
        this.nestedClickListener = clickListener;
        this.childLayout = childLayout;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

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

        }

        @Override
        public void onClick(View v) {
            System.out.println("nestedCust clicked: "+getAdapterPosition());

        }

        @Override
        public boolean onLongClick(View v) {

            return true;
        }
    }

    /**
     * Pass ordered Item up to fragment
     */
    OrderedItemClickListener orderedItemClickListener = new OrderedItemClickListener() {
        @Override
        public void recyclerViewListClicked(View v, int parentPosition, int position, OrderedItem item) {
            nestedClickListener.recyclerViewListClicked(v, parentPosition, position, item);
        }
    };


    public KitchenCustomerRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

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


        if(childLayout == -1){
            holder.mAdapter = new OrderedItemRecyclerAdapter(context,
                    customerData.get(position).getOrders(),
                    R.layout.item_imageview_textview_textview2,
                    position,
                    null,
                    orderedItemClickListener,
                    null);
        }
        else{
            holder.mAdapter = new OrderedItemRecyclerAdapter(context,
                    customerData.get(position).getOrders(),
                    childLayout,
                    position,
                    null,
                    orderedItemClickListener,
                    null);
        }

        holder.recyclerView.setAdapter(holder.mAdapter);

    }

    @Override
    public int getItemCount() {
        return customerData.size();
    }



}