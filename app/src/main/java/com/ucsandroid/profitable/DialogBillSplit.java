package com.ucsandroid.profitable;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class DialogBillSplit extends DialogFragment implements View.OnClickListener {

    private View v;
    private RelativeLayout customerSplit;
    private RelativeLayout noSplit;
    private RelativeLayout equalSplit;

    static DialogBillSplit newInstance() {
        DialogBillSplit f = new DialogBillSplit();

        //Bundle args = new Bundle();
        //f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, 0);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().setTitle("Split the bill?");
        v = inflater.inflate(R.layout.dialog_bill_split, container, false);

        customerSplit = (RelativeLayout) v.findViewById(R.id.split_pre_customer_relative);
        equalSplit = (RelativeLayout) v.findViewById(R.id.equal_split_relative);
        noSplit = (RelativeLayout) v.findViewById(R.id.no_split_relative);

        customerSplit.setOnClickListener(this);
        equalSplit.setOnClickListener(this);
        noSplit.setOnClickListener(this);

        return v;
    }


    @Override
    public void onClick(View v) {

        if(v == customerSplit){

        }
        else if(v == equalSplit){

        }
        else if(v == noSplit){

        }


    }




}
