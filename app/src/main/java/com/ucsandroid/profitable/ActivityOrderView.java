package com.ucsandroid.profitable;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;

public class ActivityOrderView extends AppCompatActivity {

    private FrameLayout customerFragContainer;
    private FrameLayout amountFragContainter;
    private FrameLayout menuitemFragContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.the_toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.app_name);
            setSupportActionBar(toolbar);
        }

        customerFragContainer = (FrameLayout) findViewById(R.id.orders_frag_container);
        amountFragContainter = (FrameLayout) findViewById(R.id.amounts_frag_container);
        menuitemFragContainer = (FrameLayout) findViewById(R.id.menuitems_frag_container);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int orientation = getResources().getConfiguration().orientation;


        int custFragHeight, custFragWidth;
        int menuItemsFragHeight, menuItemsFragWidth;


        int smallestWidth = getResources().getConfiguration().smallestScreenWidthDp;



        if (smallestWidth > 720) {
            //Device is a 10" tablet
            System.out.println("10 inch tablet");
        }
        else if (smallestWidth > 600) {
            //Device is a 7" tablet
            System.out.println("7 inch tablet");
        }
        else{
            System.out.println("not a tablet: "+smallestWidth);
        }


        if(orientation == Configuration.ORIENTATION_LANDSCAPE){

            custFragHeight = (int)(metrics.heightPixels*.5);
            custFragWidth = (int)(metrics.widthPixels*.7);
            menuItemsFragHeight = (int)(metrics.heightPixels*.4);
            menuItemsFragWidth = (int)(metrics.widthPixels);

        }
        else {

            custFragHeight = (int)(metrics.heightPixels*.5);
            custFragWidth = (int)(metrics.widthPixels*.7);
            menuItemsFragHeight = (int)(metrics.heightPixels*.4);
            menuItemsFragWidth = (int)(metrics.widthPixels);
        }


        //Dynamic resizing of the fragment containers
        customerFragContainer.getLayoutParams().height = custFragHeight;
        customerFragContainer.getLayoutParams().width = custFragWidth;

        menuitemFragContainer.getLayoutParams().height = menuItemsFragHeight;
        menuitemFragContainer.getLayoutParams().width = menuItemsFragWidth;



        initFragments();
    }

    private void initFragments() {


        Fragment orderFrag = new FragmentOrders();
        Fragment amountFrag = new FragmentOrderAmount();
        Fragment menuFrag = new FragmentMenuItems();

        //FragmentTransaction transaction = getFragmentManager().beginTransaction();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.orders_frag_container, orderFrag);
        transaction.replace(R.id.amounts_frag_container, amountFrag);
        transaction.replace(R.id.menuitems_frag_container, menuFrag);

        // Commit the transaction
        transaction.commit();


    }


}
