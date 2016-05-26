package com.ucsandroid.profitable.listeners;

import com.ucsandroid.profitable.serverclasses.FoodAddition;

import java.util.List;

public interface DialogDismissListener {

    void dialogDismissListener(int customer, int position, List<FoodAddition> additions);
}