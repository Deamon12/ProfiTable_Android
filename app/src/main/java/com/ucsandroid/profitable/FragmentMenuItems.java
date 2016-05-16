package com.ucsandroid.profitable;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FragmentMenuItems extends Fragment {


    //private static BasicRecyclerAdapter mAdapter;
    private MenuCollectionStatePagerAdapter mMenuPages;
    private ViewPager mViewPager;
    private View mView;
    private JSONArray mMenuItems;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (!Singleton.hasBeenInitialized()) {
            Singleton.initialize(getActivity());
        }

        mView = inflater.inflate(R.layout.fragment_menu_items, container, false);


        getMenu();

        return mView;
    }


    /**
     * Create and execute a volley call to retrieve JSON data from the server
     */
    private void getMenu() {

        Uri.Builder builder = Uri.parse("http://52.38.148.241:8080").buildUpon();
        builder.appendPath("com.ucsandroid.profitable")
                .appendPath("rest")
                .appendPath("menu")
                .appendPath("categories")
                .appendQueryParameter("rest_id", "1");
        String myUrl = builder.build().toString();

        //System.out.println("url: " + myUrl);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                myUrl,
                (JSONObject) null,
                successListener, errorListener);

        // Access the RequestQueue through your singleton class.
        Singleton.getInstance().addToRequestQueue(jsObjRequest);


    }

    private void initViewPager() {

        mMenuPages =
                new MenuCollectionStatePagerAdapter(getActivity().getSupportFragmentManager(), mMenuItems);
        mViewPager = (ViewPager) mView.findViewById(R.id.pager);
        mViewPager.setAdapter(mMenuPages);


        TabLayout tabLayout = (TabLayout) mView.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }


    /**
     * Since this is an object collection, use a FragmentStatePagerAdapter, NOT a FragmentPagerAdapter.
     */
    public class MenuCollectionStatePagerAdapter extends FragmentStatePagerAdapter {

        JSONArray dataSet;

        public MenuCollectionStatePagerAdapter(FragmentManager fm, JSONArray dataSet) {
            super(fm);
            this.dataSet = dataSet;
        }

        @Override
        public Fragment getItem(int position) {
            //Fragment fragment = new FragmentMenuItem();


            int color;
            if (position % 2 == 0) {
                color = R.color.primary_light;
            } else {
                color = android.R.color.white;
            }

            FragmentMenuItem fragment = null;
            try {
                fragment = FragmentMenuItem.newInstance(color, mMenuItems.getJSONObject(position).getJSONArray("menuItems"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            /*
            Bundle args = new Bundle();
            args.putInt("color", color);
            args.putInt("position", position);
            fragment.setArguments(args);*/

            return fragment;
        }

        @Override
        public int getCount() {
            return dataSet.length();
        }

        @Override
        public CharSequence getPageTitle(int position) {

            String category = "Category " + (position + 1);
            try {
                category = dataSet.getJSONObject(position).getString("categoryName");
            } catch (JSONException e) {
                System.out.println("Error using category from menu dataSet");
                e.printStackTrace();
            }

            return category;
        }

        /**
         * Reduces the horizontal width of the fragments inside the viewpager
         *
         * @param position
         * @return width size
         */
        public float getPageWidth(int position) {
            return 0.3f;
        }

    }




    private Response.Listener successListener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {
            System.out.println("Volley success: " + response);

            try {

                JSONObject theResponse = new JSONObject(response.toString());
                if(theResponse.getBoolean("success") && !theResponse.has("message")){
                    mMenuItems = theResponse.getJSONArray("result");
                    initViewPager();
                }
                else{
                    ((ActivityOrderView)getActivity()).showErrorSnackbar(theResponse.getString("message"));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            System.out.println("Volley error: " + error);
        }
    };





    /*TODO: EMPTY LISTENERS
    private Response.Listener successListener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {
            System.out.println("Volley success: " + response);
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            System.out.println("Volley error: " + error);
        }
    };
*/



}

