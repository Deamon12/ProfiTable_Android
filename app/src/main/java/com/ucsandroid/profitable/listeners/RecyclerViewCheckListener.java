package com.ucsandroid.profitable.listeners;

import android.view.View;

public interface RecyclerViewCheckListener {

    void recyclerViewListChecked(View v, int parentPosition, int position, boolean isChecked);
}