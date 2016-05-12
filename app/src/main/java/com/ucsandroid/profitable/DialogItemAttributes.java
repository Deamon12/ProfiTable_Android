package com.ucsandroid.profitable;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DialogItemAttributes extends DialogFragment {

        int mNum;

        static DialogItemAttributes newInstance(int num) {
            DialogItemAttributes f = new DialogItemAttributes();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments().getInt("num");


        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            getDialog().setTitle("Choose Extras");
            View v = inflater.inflate(R.layout.dialog_attributes, container, false);



            return v;
        }
    }
