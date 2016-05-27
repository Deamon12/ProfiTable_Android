package com.ucsandroid.profitable;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.ucsandroid.profitable.serverclasses.Category;
import com.ucsandroid.profitable.serverclasses.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentMenuViewpager extends Fragment {

    private MenuCollectionStatePagerAdapter mMenuPages;
    private ViewPager mViewPager;
    private View mView;

    private List<Category> mCategories = new ArrayList<>();;
    private ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (!Singleton.hasBeenInitialized()) {
            Singleton.initialize(getActivity());
        }

        mView = inflater.inflate(R.layout.fragment_menu_items, container, false);


        //TODO: make the call to get updated menu? Could be push notified
        //if(!hasMenu())
        getMenu();

        return mView;
    }


    /**
     * Create and execute a volley call to retrieve JSON data from the server
     * Shows a progressDialog dialog before beginning
     */
    private void getMenu() {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.isIndeterminate();
        progressDialog.setMessage("Retrieving Menu Items");
        progressDialog.show();

        Uri.Builder builder = Uri.parse("http://52.38.148.241:8080").buildUpon();
        builder.appendPath("com.ucsandroid.profitable")
                .appendPath("rest")
                .appendPath("menu")
                .appendPath("entire")
                .appendQueryParameter("rest_id", "1");
        String myUrl = builder.build().toString();

        //System.out.println("url: " + myUrl);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                myUrl,
                (JSONObject) null,
                menuRetrieveSuccessListener, errorListener);

        // Access the RequestQueue through singleton class.
        Singleton.getInstance().addToRequestQueue(jsObjRequest);


    }


    private boolean hasMenu(){

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if(settings.contains(getString(R.string.menu_jsonobject))){

            try {

                JSONArray categoryList = new JSONArray(settings.getString(getString(R.string.menu_jsonobject), ""));
                mCategories = new ArrayList<>();
                Gson gson = new Gson();

                Category category;
                for(int a = 0; a < categoryList.length(); a++){
                    category = gson.fromJson(categoryList.getJSONObject(a).toString(), Category.class);
                    //System.out.println("Categ: "+category.getMenuItems());
                    mCategories.add(category);
                }

                initViewPager();
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Populate menu items in the viewpager
     * TODO: Adjust tab item widths, reduce space between them to sync movement better
     */
    private void initViewPager() {

        if(getActivity() == null){
        }
        else {
            mMenuPages =
                    new MenuCollectionStatePagerAdapter(getActivity().getSupportFragmentManager(), mCategories);
            mViewPager = (ViewPager) mView.findViewById(R.id.pager);
            mViewPager.setAdapter(mMenuPages);

            TabLayout tabLayout = (TabLayout) mView.findViewById(R.id.sliding_tabs);
            tabLayout.setupWithViewPager(mViewPager);

        }
    }


    /**
     * Since this is an object collection, use a FragmentStatePagerAdapter, NOT a FragmentPagerAdapter.
     */
    public class MenuCollectionStatePagerAdapter extends FragmentStatePagerAdapter {

        TypedValue outValue = new TypedValue();
        List<Category> dataSet;

        public MenuCollectionStatePagerAdapter(FragmentManager fm, List<Category> dataSet) {
            super(fm);
            this.dataSet = dataSet;
            getResources().getValue(R.dimen.menu_item_width, outValue, true);
        }

        @Override
        public Fragment getItem(int position) {

            int color;
            if (position % 2 == 0) {
                color = R.color.primary_light;
            } else {
                color = android.R.color.white;
            }

            FragmentMenuItem fragment = FragmentMenuItem.newInstance(color, mCategories.get(position));

            return fragment;
        }

        @Override
        public int getCount() {
            return dataSet.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return dataSet.get(position).getName();
        }

        /**
         * Reduces the horizontal width of the fragments inside the viewpager
         * @param position
         * @return width size
         */
        public float getPageWidth(int position) {
            return outValue.getFloat();
        }

    }




    private Response.Listener menuRetrieveSuccessListener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {

            try {

                JSONObject theResponse = new JSONObject(response.toString());

                //If successful retrieval, update saved menu
                if(theResponse.getBoolean("success") && theResponse.has("result")){

                    if(getActivity() != null){
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor edit = settings.edit();
                        edit.putString(getString(R.string.menu_jsonobject), theResponse.getJSONArray("result").toString());
                        edit.apply();
                    }


                    //Build the category list for viewpager
                    //Also build a global hashmap for menuitems to lookup up details (additions, etc)
                    List<Category> freshCats = new ArrayList<>();
                    Gson gson = new Gson();

                    Category category;
                    for(int a = 0; a < theResponse.getJSONArray("result").length(); a++){
                        category = gson.fromJson(theResponse.getJSONArray("result").getJSONObject(a).toString(), Category.class);
                        //System.out.println("Categ: "+category.getMenuItems());

                        freshCats.add(category);
                        for(MenuItem menuItem : category.getMenuItems()){
                            Singleton.getInstance().addMenuItem(menuItem);
                        }
                    }

                    //Update UI with new data
                    if(!freshCats.equals(mCategories)){
                        mCategories = freshCats;
                        initViewPager();
                    }

                }
                else{
                    ((ActivityOrderView)getActivity()).showErrorSnackbar(theResponse.getString("message"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialog.dismiss();

        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.dismiss();
            System.out.println("Volley error: " + error);
        }
    };




}

