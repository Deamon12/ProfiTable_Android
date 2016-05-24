package com.ucsandroid.profitable.listeners;

import android.view.View;

import com.ucsandroid.profitable.serverclasses.MenuItem;

public interface RecyclerViewLongClickListener {

    void recyclerViewListLongClicked(View v, int parentPosition, int position, MenuItem item);
}