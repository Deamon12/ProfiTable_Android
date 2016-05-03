package com.ucsandroid.profitable;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivityTableView extends AppCompatActivity implements View.OnClickListener {


    private int tableFragWidth;
    private int tableFragHeight, barFragHeight, takeoutFragHeight;
    private FrameLayout barFragContainer;
    private FrameLayout tableFragContainer;
    private FrameLayout takeoutFragContainer;
    private Toolbar toolbar;
    private TextView barDivider, takeoutDivider, tableDivider;
    private ImageView barArrow, takeoutArrow;


    private DisplayMetrics metrics;
    private int orientation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_view);


        toolbar = (Toolbar) findViewById(R.id.the_toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);



        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        orientation = getResources().getConfiguration().orientation;

        tableFragContainer = (FrameLayout) findViewById(R.id.table_frag_container);
        barFragContainer = (FrameLayout) findViewById(R.id.bar_frag_container);
        takeoutFragContainer = (FrameLayout) findViewById(R.id.takeout_frag_container);



        barDivider = (TextView) findViewById(R.id.section_text_bar);
        barDivider.setOnClickListener(this);

        takeoutDivider = (TextView) findViewById(R.id.section_text_takeout);
        takeoutDivider.setOnClickListener(this);

        barArrow = (ImageView) findViewById(R.id.bar_divider_arrow);
        barArrow.setOnClickListener(this);

        takeoutArrow = (ImageView) findViewById(R.id.takeout_divider_arrow);
        takeoutArrow.setOnClickListener(this);

        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            barFragHeight = (int)(metrics.heightPixels*.4);
            takeoutFragHeight = (int)(metrics.heightPixels*.4);
            barFragContainer.getLayoutParams().height = barFragHeight;
        }
        else{
            tableFragHeight = (int)(metrics.heightPixels*.5);
            barFragHeight = (int)((metrics.heightPixels*.2));
            takeoutFragHeight = (int)(metrics.heightPixels*.2);
        }

        tableFragWidth = tableFragContainer.getLayoutParams().width;

        //tableFragContainer.getLayoutParams().height = tableFragHeight;
        barFragContainer.getLayoutParams().height = barFragHeight;
        takeoutFragContainer.getLayoutParams().height = takeoutFragHeight;

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


    /**
     * Hide or show bar section fragment container
     */
    public void toggleBarSection(boolean show){

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = settings.edit();

        if(!show){
            barFragContainer.findViewById(R.id.the_relative).setVisibility(View.GONE);
            ((ImageView) findViewById(R.id.bar_divider_arrow)).setImageResource(R.drawable.ic_arrow_left_white_18dp);
            barFragContainer.getLayoutParams().height = 0;
        }
        else{
            barFragContainer.findViewById(R.id.the_relative).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.bar_divider_arrow)).setImageResource(R.drawable.ic_arrow_drop_down_white_18dp);
            barFragContainer.getLayoutParams().height = barFragHeight;
        }

        edit.putBoolean("barFragShown", show);
        edit.apply();

        checkVisibility();

    }

    public void toggleTakeoutSection(boolean show){

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = settings.edit();

        if(!show){
            takeoutFragContainer.findViewById(R.id.the_relative).setVisibility(View.GONE);
            ((ImageView) findViewById(R.id.takeout_divider_arrow)).setImageResource(R.drawable.ic_arrow_left_white_18dp);
            takeoutFragContainer.getLayoutParams().height = 0;
        }
        else{
            takeoutFragContainer.findViewById(R.id.the_relative).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.takeout_divider_arrow)).setImageResource(R.drawable.ic_arrow_drop_down_white_18dp);
            takeoutFragContainer.getLayoutParams().height = takeoutFragHeight;


        }
        edit.putBoolean("takeoutFragShown", show);
        edit.apply();
        checkVisibility();
    }

    /**
     * Enlarge table view if bar and takeout are hidden
     */
    private void checkVisibility() {
        orientation = getResources().getConfiguration().orientation;

        if(orientation == Configuration.ORIENTATION_LANDSCAPE) {

            //bar showing, but takeout is gone: put takeout at bottom
            if (barFragContainer.findViewById(R.id.the_relative).getVisibility() == View.VISIBLE &&
                    takeoutFragContainer.findViewById(R.id.the_relative).getVisibility() == View.GONE) {
                System.out.println("In if..." + tableFragContainer.getHeight());

                if (tableFragContainer.getHeight() == 0) {
                    tableFragContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            //tableFragContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            barFragContainer.getLayoutParams().height = tableFragContainer.getHeight() - (barDivider.getHeight() * 2);
                            tableFragContainer.getLayoutParams().width = tableFragWidth;
                        }
                    });
                } else {
                    barFragContainer.getLayoutParams().height = tableFragContainer.getHeight() - (barDivider.getHeight() * 2);
                    tableFragContainer.getLayoutParams().width = tableFragWidth;
                }

            }
            //bar and takeout gone
            else if (barFragContainer.findViewById(R.id.the_relative).getVisibility() == View.GONE &&
                    takeoutFragContainer.findViewById(R.id.the_relative).getVisibility() == View.GONE) {
                System.out.println("In if else...");
                tableFragContainer.getLayoutParams().width = (int) (metrics.widthPixels * .85);

            } else if (barFragContainer.findViewById(R.id.the_relative).getVisibility() == View.VISIBLE &&
                    takeoutFragContainer.findViewById(R.id.the_relative).getVisibility() == View.VISIBLE) {
                System.out.println("In vis if...");
                barFragContainer.getLayoutParams().height = barFragHeight;
                takeoutFragContainer.getLayoutParams().height = takeoutFragHeight;
            } else if (barFragContainer.findViewById(R.id.the_relative).getVisibility() == View.GONE &&
                    takeoutFragContainer.findViewById(R.id.the_relative).getVisibility() == View.VISIBLE) {
                System.out.println("In gone/vis if...");
                takeoutFragContainer.getLayoutParams().height = tableFragContainer.getHeight() - (barDivider.getHeight() * 2);
                tableFragContainer.getLayoutParams().width = tableFragWidth;
            } else {
                System.out.println("In else...");
                tableFragContainer.getLayoutParams().width = tableFragWidth;

            }
        }
        else{ //portrait

            if (barFragContainer.findViewById(R.id.the_relative).getVisibility() == View.VISIBLE &&
                    takeoutFragContainer.findViewById(R.id.the_relative).getVisibility() == View.GONE) {

                barFragContainer.getLayoutParams().height = takeoutFragHeight*2;

            }
            else if (barFragContainer.findViewById(R.id.the_relative).getVisibility() == View.GONE &&
                    takeoutFragContainer.findViewById(R.id.the_relative).getVisibility() == View.GONE) {

                System.out.println("In gone if..."  );
                //takeoutFragContainer.getLayoutParams().height = 0;
                //tableFragContainer.getLayoutParams().height = (int) metrics.heightPixels - toolbar.getHeight() - (150*2);
            }
            else if (barFragContainer.findViewById(R.id.the_relative).getVisibility() == View.GONE &&
                    takeoutFragContainer.findViewById(R.id.the_relative).getVisibility() == View.VISIBLE) {

                takeoutFragContainer.getLayoutParams().height = takeoutFragHeight*2;
                barFragContainer.getLayoutParams().height = 0;

            }
            else if (barFragContainer.findViewById(R.id.the_relative).getVisibility() == View.VISIBLE &&
                    takeoutFragContainer.findViewById(R.id.the_relative).getVisibility() == View.VISIBLE) {

                takeoutFragContainer.getLayoutParams().height = takeoutFragHeight;
                barFragContainer.getLayoutParams().height = takeoutFragHeight;

            }

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


    @Override
    public void onClick(View v) {

        if(v == barDivider || v == barArrow){
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            toggleBarSection(!settings.getBoolean("barFragShown", false));

        }
        else if(v == takeoutDivider || v == takeoutArrow){
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            toggleTakeoutSection(!settings.getBoolean("takeoutFragShown", false));

        }

    }



}