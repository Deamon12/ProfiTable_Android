package com.ucsandroid.profitable.listeners;

import android.view.View;

import com.ucsandroid.profitable.supportclasses.MenuItem;

public interface RecyclerViewClickListener {

    /**
     *  Clicke interface to pass into recycler adapters to register clicks in calling frag/activity
     * @param v
     * @param parentPosition return -1, if no parent is available
     * @param position
     * @param item
     */
    void recyclerViewListClicked(View v, int parentPosition, int position, MenuItem item);
}