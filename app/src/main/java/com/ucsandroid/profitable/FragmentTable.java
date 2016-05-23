package com.ucsandroid.profitable;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import supportclasses.MenuItem;
import supportclasses.RecyclerViewClickListener;
import supportclasses.TableRecyclerAdapter;


/**
 * Fragment that displays a grid of table icons that
 * are related to a table object with orders attached to it.
 */
public class FragmentTable extends Fragment {

    private BroadcastReceiver mRemeasureFragReceiver;

    private int mRecyclerViewWidth;
    private RecyclerView mRecyclerView;
    private TableRecyclerAdapter mAdapter;
    private int spanCount;
    private int tileLayoutWidth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (!Singleton.hasBeenInitialized()) {
            Singleton.initialize(getActivity());
        }

        View view = inflater.inflate(R.layout.fragment_table, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.table_recyclerview);
        initRecyclerView();

        return view;
    }

    /**
     * Use the recently checked table (via the Singleton) to see if the UI needs updating.
     * The UI will update if a customer is currently at the table
     */
    @Override
    public void onResume() {
        super.onResume();
        if(mAdapter != null)
            mAdapter.notifyItemChanged(Singleton.getInstance().getCurrentTableNumber());

        initRemeasureFragListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRemeasureFragReceiver);
    }

    private void initRecyclerView() {

        spanCount = getSpanCount();

        ViewTreeObserver vto = mRecyclerView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mRecyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                mRecyclerViewWidth  = mRecyclerView.getMeasuredWidth();

                tileLayoutWidth = (mRecyclerViewWidth/spanCount);

                getTableData();

            }
        });

    }


    /**
     * TODO: Volley call to acquire table data
     */
    private void getTableData() {

        GridLayoutManager gridLayout = new GridLayoutManager(getActivity(), spanCount);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(gridLayout);

        mAdapter = new TableRecyclerAdapter(getActivity(),
                Singleton.getInstance().getAllTables(),
                R.layout.tile_table,
                new ViewGroup.LayoutParams(tileLayoutWidth, tileLayoutWidth),
                clickListener);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void updateUI(int tableFragWidth){

        spanCount = getSpanCount();

        tileLayoutWidth = (tableFragWidth/spanCount);

        getTableData();
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

    private int getSpanCount(){

        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        int orientation = getResources().getConfiguration().orientation;

        if (tabletSize) {
            if(orientation == Configuration.ORIENTATION_LANDSCAPE){
                return 7;
            }else
                return 8;
        } else {
            if(orientation == Configuration.ORIENTATION_LANDSCAPE){
                return 6;
            }else
                return 5;
        }
    }


    /**
     * Listen for UI changes. Visibility changes from other frags will allow this fragment to be
     * larger or smaller
     */
    private void initRemeasureFragListener() {

        mRemeasureFragReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getIntExtra("tableWidth", 0) != -1 && getActivity() != null)
                    updateUI(intent.getIntExtra("tableWidth", 0));
            }
        };

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRemeasureFragReceiver,
                new IntentFilter("tablefrag-measure"));
    }


}

