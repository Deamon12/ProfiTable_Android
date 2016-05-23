package com.ucsandroid.profitable.supportclasses;

import android.view.View;

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