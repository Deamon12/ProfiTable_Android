package com.ucsandroid.profitable;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import supportclasses.OrdersAdapter;
import supportclasses.RecyclerViewClickListener;

public class FragmentOrders extends Fragment {

    private RecyclerView mRecyclerView;
    private int tileLayoutHeight, tileLayoutWidth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.orders_recyclerview);
        initRecyclerView();

        return view;
    }

    /**
     * Init recyclerview with some basic properties
     */
    private void initRecyclerView() {

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int spanCount;

        int orientation = getResources().getConfiguration().orientation;

        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            spanCount = 2;
            tileLayoutHeight = (int)(metrics.heightPixels);
            tileLayoutWidth = (int)(metrics.widthPixels);

        }else{
            spanCount = 1;
            tileLayoutHeight = (int)(metrics.heightPixels);
            tileLayoutWidth = (int)(metrics.widthPixels);
        }

        StaggeredGridLayoutManager stagLayout = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(stagLayout);

        getOrders();

    }

    /**
     * Volley call to retrieve data and populate the adapter
     * TODO: add volley
     */
    private void getOrders() {

        ArrayList<String> dataSet = new ArrayList<>();

        for(int a = 1; a <= 5; a++)
            dataSet.add("Customer "+a);

        OrdersAdapter rcAdapter = new OrdersAdapter(getActivity(), dataSet, R.layout.tile_customer_order,
                new ViewGroup.LayoutParams(tileLayoutWidth, ViewGroup.LayoutParams.MATCH_PARENT));
        mRecyclerView.setAdapter(rcAdapter);

    }


    RecyclerViewClickListener clickListener = new RecyclerViewClickListener() {
        @Override
        public void recyclerViewListClicked(View v, int position) {
            Intent orderViewActivity = new Intent(getActivity(), ActivityOrderView.class);
            getActivity().startActivity(orderViewActivity);
        }
    };


}


/*
    class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.ViewHolder> {

        private int mLayout;
        private ArrayList<String> mDataset;
        private ViewGroup.LayoutParams mParams;
        private Context mContext;


        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public TextView mTextView;

            public ViewHolder(View v) {
                super(v);
                mTextView = (TextView) v.findViewById(R.id.customer_count_text);
                v.setOnClickListener(this);

            }


            @Override
            public void onClick(View v) {
                System.out.println("Clicked: " + getAdapterPosition());
                //Intent orderViewActivity = new Intent(mContext, ActivityOrderView.class);
                //mContext.startActivity(orderViewActivity);
            }

        }

        public MyOrdersAdapter(Context context, ArrayList myDataset, int layout, ViewGroup.LayoutParams params) {
            mDataset = myDataset;
            mContext = context;
            mLayout = layout;
            mParams = params;
        }


        public MyOrdersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

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

            holder.mTextView.setText(mDataset.get(position));

        }


        @Override
        public int getItemCount() {
            return mDataset.size();
        }


    }*/


