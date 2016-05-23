package com.ucsandroid.profitable.supportclasses;

import org.json.JSONArray;

public interface DialogDismissListener {

    void dialogDismissListener(int customer, int position, JSONArray additions);
}