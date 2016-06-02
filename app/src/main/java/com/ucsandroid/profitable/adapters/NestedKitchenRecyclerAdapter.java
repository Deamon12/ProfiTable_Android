package com.ucsandroid.profitable.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ucsandroid.profitable.R;
import com.ucsandroid.profitable.listeners.NestedClickListener;
import com.ucsandroid.profitable.serverclasses.Tab;
import com.ucsandroid.profitable.supportclasses.MyLinearLayoutManager;

import java.util.List;

public class NestedKitchenRecyclerAdapter extends RecyclerView.Adapter<NestedKitchenRecyclerAdapter.ViewHolder> {

    private int layout;

    private List<Tab> tabData;

    private ViewGroup.LayoutParams layoutParams;
    private static Context context;
    private NestedClickListener nestedClickListener;

    public NestedKitchenRecyclerAdapter(Context context, List<Tab> dataSet, int layout, ViewGroup.LayoutParams params, NestedClickListener clickListener) {
        tabData = dataSet;
        this.context = context;
        this.layout = layout;
        this.layoutParams = params;
        this.nestedClickListener = clickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private ImageView doneButton;
        public TextView mCommentTextView;
        public TextView mTextView;
        public RecyclerView recyclerView;
        private CardView cardView;
        private KitchenCustomerRecyclerAdapter mAdapter;

        public ViewHolder(View v) {
            super(v);

            mTextView = (TextView) v.findViewById(R.id.tile_text);

            if(layout == R.layout.tile_kitchen_order){

                doneButton = (ImageView) v.findViewById(R.id.check_button);
                mCommentTextView = (TextView) v.findViewById(R.id.comment_text);

                cardView = (CardView) v.findViewById(R.id.the_cardview);

                recyclerView = (RecyclerView) v.findViewById(R.id.item_recycler);
                recyclerView.setHasFixedSize(false);
                recyclerView.setLayoutManager(new MyLinearLayoutManager(context));

                v.setOnClickListener(this);
                v.setOnLongClickListener(this);

                cardView.setOnClickListener(this);
                doneButton.setOnClickListener(this);
            }
            else{

            }
        }

        @Override
        public void onClick(View v) {

            if(v == doneButton){
                //Do volley and update UI
                System.out.println("in onclick of nestedKitchen");
                nestedClickListener.nestedClickListener(getAdapterPosition(), tabData.get(getAdapterPosition()));
            }
        }

        @Override
        public boolean onLongClick(View v) {

            System.out.println("In long click: "+tabData.get(getAdapterPosition()).allOrdersReady());

            return true;
        }
    }


    public NestedKitchenRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

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


        //tabData.get(position).getCustomers().get(0).getOrders().get(0).getOrderedItemStatus();



        //Green for done, Blue for not done
        if(tabData.get(position).allOrdersReady())
            holder.doneButton.setColorFilter(ContextCompat.getColor(context, R.color.primary));
        else
            holder.doneButton.setColorFilter(ContextCompat.getColor(context, R.color.accent));


        holder.mCommentTextView.setVisibility(View.GONE); //todo: comments not being used
        //holder.mCommentTextView.setText("");//tabData.get(position).getTabStatus());


        holder.mTextView.setText("Table " + tabData.get(position).getTabId()); //(position+1)


        holder.mAdapter = new KitchenCustomerRecyclerAdapter(context,
                tabData.get(position).getCustomers(),
                R.layout.tile_recyclerview,
                position,
                null,
                null);

        holder.recyclerView.setAdapter(holder.mAdapter);

    }

    @Override
    public int getItemCount() {
        return tabData.size();
    }


    public Tab getDataItem(int position){
        return tabData.get(position);
    }


}