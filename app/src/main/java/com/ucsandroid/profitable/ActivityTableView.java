package com.ucsandroid.profitable;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;

public class ActivityTableView extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_view);


        Toolbar toolbar = (Toolbar) findViewById(R.id.the_toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.app_name);
            setSupportActionBar(toolbar);
        }


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int orientation = getResources().getConfiguration().orientation;

        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            FrameLayout barFragContainer = (FrameLayout) findViewById(R.id.bar_frag_container);
            barFragContainer.getLayoutParams().height = (int)(metrics.heightPixels*.5);
        }





        initFragments();
    }



    private void initFragments() {


        Fragment tableFrag = new FragmentTable();
        Fragment barFrag = new FragmentBar();
        Fragment takeoutFrag = new FragmentTakeout();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.table_frag_container, tableFrag);
        transaction.replace(R.id.bar_frag_container, barFrag);
        transaction.replace(R.id.takeout_frag_container, takeoutFrag);

        // Commit the transaction
        transaction.commit();


    }


}