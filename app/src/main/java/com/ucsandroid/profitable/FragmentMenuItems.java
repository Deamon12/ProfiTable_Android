package com.ucsandroid.profitable;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import supportclasses.BasicRecyclerAdapter;
import supportclasses.MyLinearLayoutManager;
import supportclasses.RecyclerViewClickListener;

public class FragmentMenuItems extends Fragment {


    private static BasicRecyclerAdapter mAdapter;
    private MenuCollectionStatePagerAdapter mMenuPages;
    private ViewPager mViewPager;
    private View mView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (!Singleton.hasBeenInitialized()) {
            Singleton.initialize(getActivity());
        }


        mView = inflater.inflate(R.layout.fragment_menu_items, container, false);
        initViewPager();

        getMenuItems();






        return mView;
    }



    private void getMenuItems() {

        StringRequest myReq = new StringRequest(Request.Method.GET,
                "http://52.38.148.241:8080/com.ucsandroid.profitable/rest/serviceclass/menuItem",
                successListener,
                errorListener) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("rest_id", "1");
                //params.put("param2", "hgt");
                return params;
            };
        };
        //queue.add(myReq);
        Singleton.getInstance().addToRequestQueue(myReq);


    }


    private void initViewPager() {

        mMenuPages =
                new MenuCollectionStatePagerAdapter(getActivity().getSupportFragmentManager());
        mViewPager = (ViewPager) mView.findViewById(R.id.pager);
        mViewPager.setAdapter(mMenuPages);


        TabLayout tabLayout = (TabLayout) mView.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }


    /**
     * Since this is an object collection, use a FragmentStatePagerAdapter, NOT a FragmentPagerAdapter.
     */
    public class MenuCollectionStatePagerAdapter extends FragmentStatePagerAdapter {

        public MenuCollectionStatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new MenuItemFrag();

            int color;
            if(i%2 == 0){
                color = R.color.primary_light;
            }
            else{
                color = android.R.color.white;
            }
            Bundle args = new Bundle();
            args.putInt("color", color);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            //Depend on some JSONArray of Menu categories
            return 10;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Menu " + (position + 1);
        }

        /**
         * Reduces the horizontal width of the fragments inside the viewpager
         * @param position
         * @return width size
         */
        public float getPageWidth(int position) {
            return 0.3f;
        }

    }

    /**
     * Food category fragments
     */
    public static class MenuItemFrag extends Fragment {

        RecyclerView recyclerView;

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_menu_page, container, false);
            int color = getArguments().getInt("color");

            rootView.setBackgroundResource(color);
            recyclerView = (RecyclerView) rootView.findViewById(R.id.menu_page_recyclerview);

            initRecycler();

            return rootView;
        }


        /**
         * recycler that shows items for a particular food category
         */
        private void initRecycler() {

            int count = new Random().nextInt(10);
            JSONArray dataSet = new JSONArray();
            try {
                for(int a = 1; a <= count;a++){
                    JSONObject temp = new JSONObject();
                    temp.put("name", "Food item "+a);
                    dataSet.put(temp);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            recyclerView.setLayoutManager(new MyLinearLayoutManager(getActivity()));
            mAdapter = new BasicRecyclerAdapter(getActivity(), dataSet, R.layout.item_textview_textview, null, clickListener);

            recyclerView.setAdapter(mAdapter);


        }

        /**
         * Recieves the clicked position from a menu category
         * Pass off an itemId from a MenuPage, to the OrderFragment to start the add item flow.
         */
        RecyclerViewClickListener clickListener = new RecyclerViewClickListener() {

            @Override
            public void recyclerViewListClicked(View v, int parentPosition, int position, String item) {
                //TODO : change from String item to JSONObject item or MenuItem obj
                FragmentOrders orderFrag = (FragmentOrders) getActivity().getSupportFragmentManager().findFragmentById(R.id.orders_frag_container);

                if(orderFrag != null){
                    orderFrag.addItem(item);
                }

                FragmentOrderAmount amountFrag = (FragmentOrderAmount) getActivity().getSupportFragmentManager().findFragmentById(R.id.amounts_frag_container);

                if(amountFrag != null){
                    amountFrag.addItem(5); //todo
                }


            }
        };

    }


    private Response.Listener successListener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {
            System.out.println("Volley success: "+response);
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            System.out.println("Volley error: "+error);
        }
    };



}

