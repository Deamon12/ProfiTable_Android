package com.ucsandroid.profitable;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ActivityKitchen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);


        Toolbar toolbar = (Toolbar) findViewById(R.id.the_toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        initFragments();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_kitchen_view, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_tables:
                Intent netLabActivity = new Intent(ActivityKitchen.this, ActivityTableView.class);
                startActivity(netLabActivity);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void initFragments() {


        Fragment ordersFrag = new FragmentKitchenOrders();
        //Fragment quantityFrag = new FragmentBar();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.kitchen_items_frag_container, ordersFrag);
        //transaction.replace(R.id.bar_frag_container, barFrag);

        transaction.commit();


    }






}
