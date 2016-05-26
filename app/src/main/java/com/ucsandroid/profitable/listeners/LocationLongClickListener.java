package com.ucsandroid.profitable.listeners;

import android.view.View;

import com.ucsandroid.profitable.serverclasses.Location;

public interface LocationLongClickListener {

    /**
     *
     * @param v
     * @param parentPosition
     * @param position
     * @param item
     */
    void recyclerViewListClicked(View v, int parentPosition, int position, Location item);
}