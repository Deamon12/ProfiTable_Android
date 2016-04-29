package com.ucsandroid.profitable;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class ActivityOrderView extends AppCompatActivity implements View.OnClickListener {

    private FrameLayout customerFragContainer;
    private FrameLayout amountFragContainter;
    private FrameLayout menuitemFragContainer;

    private ImageView dividerArrow;

    private View menuDivider;
    private DisplayMetrics metrics;

    int custFragHeight, custFragWidth;
    int menuItemsFragHeight, menuItemsFragWidth;

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
        menuDivider = findViewById(R.id.menu_divider);
        menuDivider.setOnClickListener(this);

        dividerArrow = (ImageView) findViewById(R.id.divider_image);


        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int orientation = getResources().getConfiguration().orientation;




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

            custFragHeight = (int)(metrics.heightPixels*.6);
            custFragWidth = (int)(metrics.widthPixels*.7);
            menuItemsFragHeight = (int)(metrics.heightPixels*.3);
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


    /**
     * Dynamically show or hide the menu items to give more room to see customer info
     */
    private void toggleMenuContainer(){

        if(menuitemFragContainer.findViewById(R.id.pager).getVisibility() == View.VISIBLE) {
            menuitemFragContainer.findViewById(R.id.pager).setVisibility(View.GONE);
            menuitemFragContainer.getLayoutParams().height = 0;
            dividerArrow.setImageResource(R.drawable.ic_arrow_drop_up_white_36dp);
        }
        else {
            menuitemFragContainer.findViewById(R.id.pager).setVisibility(View.VISIBLE);
            menuitemFragContainer.getLayoutParams().height = menuItemsFragHeight;
            dividerArrow.setImageResource(R.drawable.ic_arrow_drop_down_white_36dp);
        }

    }

    @Override
    public void onClick(View v) {

        if(v == menuDivider){
            toggleMenuContainer();

        }

    }



}
