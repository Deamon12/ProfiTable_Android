package com.ucsandroid.profitable;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import supportclasses.BasicRecyclerAdapter;
import supportclasses.MenuItem;
import supportclasses.MyLinearLayoutManager;
import supportclasses.RecyclerViewClickListener;

public class FragmentKitchenOrders extends Fragment{

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_kitchen_orders, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.kitchen_orders_recyclerview);



        getOrders();




        return view;
    }

    private void getOrders() {

        initRecyclerView();
    }

    private void initRecyclerView() {

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);


        int layoutHeight, layoutWidth;
        int orientation = getResources().getConfiguration().orientation;

        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            //tileLayoutHeight = (int)(metrics.heightPixels);
            layoutWidth = (int)(metrics.widthPixels*.3);

        }else{
            //tileLayoutHeight = (int)(metrics.heightPixels*.1);
            layoutWidth = (int)(metrics.widthPixels*.4);
            //tileLayoutHeight = tileLayoutWidth;
        }



        ArrayList<String> dataSet = new ArrayList<>();

        for(int a = 1; a <= 10; a++)
            dataSet.add("Table "+a);

        MyLinearLayoutManager layoutManager
                = new MyLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


        BasicRecyclerAdapter rcAdapter = new BasicRecyclerAdapter(getActivity(), dataSet, R.layout.tile_kitchen_order,null, clickListener);
        recyclerView.setAdapter(rcAdapter);
    }


    /**
     * Click interface for adapter
     */
    RecyclerViewClickListener clickListener = new RecyclerViewClickListener() {

        @Override
        public void recyclerViewListClicked(View v, int parentPosition, int position, MenuItem item) {
            Intent orderViewActivity = new Intent(getActivity(), ActivityOrderView.class);

            Singleton.getInstance().setCurrentTableNumber(position);
            getActivity().startActivity(orderViewActivity);


        }

    };


/*
    class KitchenOrdersAdapter extends RecyclerView.Adapter<KitchenOrdersAdapter.ViewHolder>  {

        private int mLayout;
        private ArrayList<String> mDataset;
        private ViewGroup.LayoutParams mParams;
        ArrayList<String> itemSet;
        Context mContext;


        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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

        public KitchenOrdersAdapter(Context context, ArrayList myDataset, int layout, ViewGroup.LayoutParams params) {
            mDataset = myDataset;
            mContext = context;
            mLayout = layout;
            mParams = params;


        }


        public KitchenOrdersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

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
            JSONArray dataSet = new JSONArray();
            try {
                for(int a = 1; a <= count;a++){
                    JSONObject temp = new JSONObject();
                    temp.put("name", ""+a);
                    dataSet.put(temp);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            //TODO : structure may change
            JSONArrayRecyclerAdapter rcAdapter = new JSONArrayRecyclerAdapter(mContext, dataSet, R.layout.item_textview_imageview);

            holder.recyclerView.setAdapter(rcAdapter);


        }


        @Override
        public int getItemCount() {
            return mDataset.size();
        }

    }

*/



}