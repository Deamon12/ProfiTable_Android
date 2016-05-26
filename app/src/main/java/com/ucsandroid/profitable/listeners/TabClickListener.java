package com.ucsandroid.profitable.listeners;

import android.view.View;

import com.ucsandroid.profitable.serverclasses.Tab;

public interface TabClickListener {

    /**
     * @param v
     * @param parentPosition
     * @param position
     * @param item
     */
    void recyclerViewListClicked(View v, int parentPosition, int position, Tab item);
}