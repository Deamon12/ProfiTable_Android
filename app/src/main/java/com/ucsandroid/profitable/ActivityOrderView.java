package com.ucsandroid.profitable;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class ActivityOrderView extends AppCompatActivity implements View.OnClickListener {


    private Fragment orderFrag;

    private FrameLayout customerFragContainer;
    private FrameLayout amountFragContainter;
    private FrameLayout menuitemFragContainer;

    private ImageView dividerArrow;
    private FloatingActionButton addCustomer;
    private FloatingActionButton doCheckout;
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

            //TODO: This might not be a table, need bar, takeout
            toolbar.setTitle("Table "+(Singleton.getInstance().getCurrentTable()+1));
            setSupportActionBar(toolbar);
        }

        customerFragContainer = (FrameLayout) findViewById(R.id.orders_frag_container);
        amountFragContainter = (FrameLayout) findViewById(R.id.amounts_frag_container);
        menuitemFragContainer = (FrameLayout) findViewById(R.id.menuitems_frag_container);
        addCustomer = (FloatingActionButton) findViewById(R.id.addcustomer_fab);
        doCheckout = (FloatingActionButton) findViewById(R.id.docheckout_fab);
        doCheckout.setOnClickListener(this);
        addCustomer.setOnClickListener(this);
        menuDivider = findViewById(R.id.menu_divider);
        menuDivider.setOnClickListener(this);

        dividerArrow = (ImageView) findViewById(R.id.divider_image);

        dynamicallySizeContainers();

        initFragments();
    }

    /**
     * Use screen metrics to adjust the sizing of fragment containers.
     */
    private void dynamicallySizeContainers() {

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int orientation = getResources().getConfiguration().orientation;


        if(orientation == Configuration.ORIENTATION_LANDSCAPE){

            custFragHeight = (int)(metrics.heightPixels*.6);
            custFragWidth = (int)(metrics.widthPixels*.7);
            menuItemsFragHeight = (int)(metrics.heightPixels*.3);
            menuItemsFragWidth = (int)(metrics.widthPixels);
        }
        else {

            custFragHeight = (int)(metrics.heightPixels*.6);
            custFragWidth = (int)(metrics.widthPixels*.7);
            menuItemsFragHeight = (int)(metrics.heightPixels*.3);
            menuItemsFragWidth = (int)(metrics.widthPixels);
        }


        //Dynamic resizing of the fragment containers
        customerFragContainer.getLayoutParams().height = custFragHeight;
        customerFragContainer.getLayoutParams().width = custFragWidth;

        menuitemFragContainer.getLayoutParams().height = menuItemsFragHeight;
        menuitemFragContainer.getLayoutParams().width = menuItemsFragWidth;

    }


    /**
     * Basic fragment initialization
     */
    private void initFragments() {

        orderFrag = new FragmentOrders();
        Fragment amountFrag = new FragmentOrderAmount();
        Fragment menuFrag = new FragmentMenuViewpager();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.orders_frag_container, orderFrag);
        transaction.replace(R.id.amounts_frag_container, amountFrag);
        transaction.replace(R.id.menuitems_frag_container, menuFrag);

        // Commit the transaction
        transaction.commit();

    }


    /**
     * Dynamically show or hide the menu items to give more room to see customer info
     * TODO:animate
     */
    private void toggleMenuContainer(){

        if(menuitemFragContainer.findViewById(R.id.pager).getVisibility() == View.VISIBLE) {
            menuitemFragContainer.findViewById(R.id.pager).setVisibility(View.GONE);
            menuitemFragContainer.getLayoutParams().height = 0;
            dividerArrow.setImageResource(R.drawable.ic_arrow_drop_up_white_18dp);
        }
        else {
            menuitemFragContainer.findViewById(R.id.pager).setVisibility(View.VISIBLE);
            menuitemFragContainer.getLayoutParams().height = menuItemsFragHeight;
            dividerArrow.setImageResource(R.drawable.ic_arrow_drop_down_white_18dp);
        }

    }

    @Override
    public void onClick(View v) {

        if(v == menuDivider){
            toggleMenuContainer();
        }
        else if(v == addCustomer){
            sendAddCustomerBroadcast();
        }
        else if(v == addCustomer){
            sendAddCustomerBroadcast();
        }

    }

    /**
     * Create and send a broadcast for the order fragment to recieve and create a new customer,
     * and add them to the dataset.
     */
    private void sendAddCustomerBroadcast() {
        Intent intent = new Intent("add-customer");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    //TODO: change to snackbar
    public void showErrorSnackbar(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }



}
