package com.ucsandroid.profitable;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.ucsandroid.profitable.serverclasses.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityTableView extends AppCompatActivity implements View.OnClickListener {


    private int tableFragWidth;
    private int barFragHeight, takeoutFragHeight;
    private FrameLayout barFragContainer;
    private FrameLayout tableFragContainer;
    private FrameLayout takeoutFragContainer;
    private Toolbar toolbar;
    private TextView barDivider, takeoutDivider, tableDivider;
    private ImageView barArrow, takeoutArrow;

    private ProgressDialog progressDialog;

    private DisplayMetrics metrics;
    private int orientation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_view);


        toolbar = (Toolbar) findViewById(R.id.the_toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);


        if (!Singleton.hasBeenInitialized()) {
            Singleton.initialize(this);
        }

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
            tableFragWidth = (int)(metrics.widthPixels*.5);
        }
        else{
            barFragHeight = (int)((metrics.heightPixels*.2));
            takeoutFragHeight = (int)(metrics.heightPixels*.2);
            tableFragWidth = tableFragContainer.getLayoutParams().width;
        }


        tableFragContainer.getLayoutParams().width = tableFragWidth;
        barFragContainer.getLayoutParams().height = barFragHeight;
        takeoutFragContainer.getLayoutParams().height = takeoutFragHeight;

        getMenu();
        evaluateLocationData();


    }


    /**
     * Compare local location data, initiate volley call, if necessary.
     * todo: push notify to perform this
     */
    private void evaluateLocationData() {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ActivityTableView.this);

        if(settings.getString(getString(R.string.locations_jsonobject), "").equalsIgnoreCase("")) {
            System.out.println("getting tables data");
            getTablesData();
        }
        else if(!Singleton.getInstance().hasLocationData()){
            System.out.println("setting tables data");
            setLocationsFromPrefs();
            getTablesData(); //todo:progress?
        }
        else{ //use local data
            System.out.println("using tables data");
            initFragments();
            getTablesData(); //progress?
        }


    }



    private void getTablesData() {

        //http://52.38.148.241:8080/com.ucsandroid.profitable/rest/location?rest_id=1
        progressDialog = new ProgressDialog(this);
        progressDialog.isIndeterminate();
        progressDialog.setMessage("Retrieving Menu Items");
        progressDialog.show();

        Uri.Builder builder = Uri.parse("http://52.38.148.241:8080").buildUpon();
        builder.appendPath("com.ucsandroid.profitable")
                .appendPath("rest")
                .appendPath("location")
                .appendQueryParameter("rest_id", "1");
        String myUrl = builder.build().toString();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                myUrl,
                (JSONObject) null,
                successListener, errorListener);

        // Access the RequestQueue through your singleton class.
        Singleton.getInstance().addToRequestQueue(jsObjRequest);


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


    //TODO: use listener
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

    //TODO: use listener
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
     * The main logic that resizes Bar and Takeout fragments depending on what views are toggled
     */
    private void checkVisibility() {
        if(barFragContainer.findViewById(R.id.the_relative) == null || takeoutFragContainer.findViewById(R.id.the_relative) == null)
            return;

        orientation = getResources().getConfiguration().orientation;

        if(orientation == Configuration.ORIENTATION_LANDSCAPE) {

            //bar showing, but takeout is gone: put takeout at bottom
            if (barFragContainer.findViewById(R.id.the_relative).getVisibility() == View.VISIBLE &&
                    takeoutFragContainer.findViewById(R.id.the_relative).getVisibility() == View.GONE) {
                //System.out.println("In if..." + tableFragContainer.getHeight());


                if (tableFragContainer.getHeight() == 0) {
                    tableFragContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            tableFragContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
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
                //System.out.println("In if else...");
                tableFragContainer.getLayoutParams().width = (int) (metrics.widthPixels * .85);

            } else if (barFragContainer.findViewById(R.id.the_relative).getVisibility() == View.VISIBLE &&
                    takeoutFragContainer.findViewById(R.id.the_relative).getVisibility() == View.VISIBLE) {
                //System.out.println("In vis if...");
                barFragContainer.getLayoutParams().height = barFragHeight;
                takeoutFragContainer.getLayoutParams().height = takeoutFragHeight;
            } else if (barFragContainer.findViewById(R.id.the_relative).getVisibility() == View.GONE &&
                    takeoutFragContainer.findViewById(R.id.the_relative).getVisibility() == View.VISIBLE) {
                //System.out.println("In gone/vis if...");
                takeoutFragContainer.getLayoutParams().height = tableFragContainer.getHeight() - (barDivider.getHeight() * 2);
                tableFragContainer.getLayoutParams().width = tableFragWidth;
            } else {
                //System.out.println("In else...");
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

                //System.out.println("In gone if..."  );
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


        //BroadCast table frag width update
        sendRemeasureTableWidthBroadcast();


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

    private void sendRemeasureTableWidthBroadcast() {
        Intent intent = new Intent("tablefrag-measure");
        intent.putExtra("tableWidth", tableFragContainer.getLayoutParams().width);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    private Response.Listener successListener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {
            //System.out.println("Volley success: " + response);
            progressDialog.dismiss();
            try {
                JSONObject theResponse = new JSONObject(response.toString());

                //If successful retrieval, update saved menu
                if(theResponse.getBoolean("success") && theResponse.has("result")){

                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ActivityTableView.this);

                    String newData = theResponse.getJSONArray("result").toString();
                    String localData = settings.getString(getString(R.string.locations_jsonobject), "");



                    //IF results are different than local data, update local data
                    if(!newData.equalsIgnoreCase(localData)){
                        //System.out.println("updating local locations");
                        SharedPreferences.Editor edit = settings.edit();
                        edit.putString(getString(R.string.locations_jsonobject), theResponse.getJSONArray("result").toString());
                        edit.apply();
                        setLocationsFromPrefs();
                    }
                    else if(newData.equalsIgnoreCase(localData)){
                        //System.out.println("Local locations are the same as new locations. Not updating data or UI");
                    }

                }
                else{
                    //TODO:Results Error ((ActivityOrderView)getActivity()).showErrorSnackbar(theResponse.getString("message"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.dismiss();
            ///TODO Connect/server error
            System.out.println("Volley error: " + error);
        }
    };


    private void setLocationsFromPrefs() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ActivityTableView.this);
        try {
            Singleton.getInstance().setLocations(new JSONArray(settings.getString(getString(R.string.locations_jsonobject), "")));


            initFragments();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    /**
     * Create and execute a volley call to retrieve JSON data from the server
     * Shows a progressDialog dialog before beginning
     */
    private void getMenu() {

        Uri.Builder builder = Uri.parse("http://52.38.148.241:8080").buildUpon();
        builder.appendPath("com.ucsandroid.profitable")
                .appendPath("rest")
                .appendPath("menu")
                .appendPath("entire")
                .appendQueryParameter("rest_id", "1");
        String myUrl = builder.build().toString();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                myUrl,
                (JSONObject) null,
                menuRetrieveSuccessListener, errorListener);

        // Access the RequestQueue through singleton class.
        Singleton.getInstance().addToRequestQueue(jsObjRequest);


    }

    /**
     * Menu retrieval success
     */
    private Response.Listener menuRetrieveSuccessListener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {

            try {

                JSONObject theResponse = new JSONObject(response.toString());

                //If successful retrieval, update saved menu
                if (theResponse.getBoolean("success") && theResponse.has("result")) {

                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ActivityTableView.this);
                    SharedPreferences.Editor edit = settings.edit();
                    edit.putString(getString(R.string.menu_jsonobject), theResponse.getJSONArray("result").toString());
                    edit.apply();


                    //Build the category list for viewpager
                    //Also build a global hashmap for menuitems to lookup up details (additions, etc)
                    List<Category> freshCats = new ArrayList<>();
                    Gson gson = new Gson();

                    Category category;
                    for (int a = 0; a < theResponse.getJSONArray("result").length(); a++) {
                        category = gson.fromJson(theResponse.getJSONArray("result").getJSONObject(a).toString(), Category.class);
                        //System.out.println("Categ: "+category.getMenuItems());

                        freshCats.add(category);
                        for (com.ucsandroid.profitable.serverclasses.MenuItem menuItem : category.getMenuItems()) {
                            Singleton.getInstance().addMenuItem(menuItem);
                        }
                    }


                } else {
                    //((ActivityOrderView)getActivity()).showErrorSnackbar(theResponse.getString("message"));
                    //TODO show error
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialog.dismiss();

        }
    };


}