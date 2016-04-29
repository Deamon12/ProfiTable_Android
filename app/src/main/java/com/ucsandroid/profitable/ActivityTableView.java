package com.ucsandroid.profitable;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

public class ActivityTableView extends AppCompatActivity {


    private int barFragHeight;
    private FrameLayout barFragContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_view);


        Toolbar toolbar = (Toolbar) findViewById(R.id.the_toolbar);

        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);



        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int orientation = getResources().getConfiguration().orientation;

        barFragContainer = (FrameLayout) findViewById(R.id.bar_frag_container);

        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            barFragHeight = (int)(metrics.heightPixels*.5);
            barFragContainer.getLayoutParams().height = barFragHeight;
        }
        else
            barFragHeight = barFragContainer.getLayoutParams().height;





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


    public void toggleBarSection(){


        if(barFragContainer.findViewById(R.id.bar_recyclerview).getVisibility() == View.VISIBLE) {
            barFragContainer.findViewById(R.id.bar_recyclerview).setVisibility(View.GONE);
            barFragContainer.getLayoutParams().height = barFragContainer.findViewById(R.id.partition_bar).getLayoutParams().height;
        }
        else {
            barFragContainer.findViewById(R.id.bar_recyclerview).setVisibility(View.VISIBLE);
            barFragContainer.getLayoutParams().height = barFragHeight;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_table_view, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_kitchen:
                Intent intent = new Intent(ActivityTableView.this, ActivityKitchen.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}