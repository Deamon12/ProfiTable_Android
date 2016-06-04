package com.ucsandroid.profitable;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class ActivityKitchen extends AppCompatActivity {

    private CoordinatorLayout mCoordinator;
    private FrameLayout kitchenAmountsFragContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);

        if (!Singleton.hasBeenInitialized()) {
            Singleton.initialize(this);
        }

        mCoordinator = (CoordinatorLayout) findViewById(R.id.the_coordinator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.the_toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        setupFragmentContainers();
        //getKitchenData();
        initFragments();

    }

    private void setupFragmentContainers(){

        kitchenAmountsFragContainer = (FrameLayout) findViewById(R.id.kitchen_totals_frag_container);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        TypedValue outValue = new TypedValue();
        int orientation = getResources().getConfiguration().orientation;
        int amountsHeight;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getResources().getValue(R.dimen.kitchen_amount_frag_width_landscape, outValue, true);
            amountsHeight = (int) (metrics.heightPixels * outValue.getFloat());

        } else {
            getResources().getValue(R.dimen.kitchen_amount_frag_width_portrait, outValue, true);
            amountsHeight = (int) (metrics.heightPixels * outValue.getFloat());
        }

        kitchenAmountsFragContainer.getLayoutParams().height = amountsHeight;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_kitchen_view, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent netLabActivity = new Intent(ActivityKitchen.this, ActivityLocationView.class);
            startActivity(netLabActivity);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_tables:
                Intent netLabActivity = new Intent(ActivityKitchen.this, ActivityLocationView.class);
                startActivity(netLabActivity);
                finish();
                return true;

            case R.id.action_refresh:
                sendUpdateKitchenBroadcast();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void initFragments() {

        Fragment ordersFrag = FragmentKitchenOrders.newInstance();
        Fragment quantityFrag = FragmentKitchenAmounts.newInstance();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.kitchen_items_frag_container, ordersFrag);
        transaction.replace(R.id.kitchen_totals_frag_container, quantityFrag);

        transaction.commit();

    }

    private void sendUpdateKitchenBroadcast(){
        Intent updateIntent = new Intent("update-kitchen");
        LocalBroadcastManager.getInstance(this).sendBroadcast(updateIntent);
    }


}
