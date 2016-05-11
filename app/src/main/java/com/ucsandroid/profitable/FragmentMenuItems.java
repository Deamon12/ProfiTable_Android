package com.ucsandroid.profitable;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Random;

import supportclasses.BasicRecyclerAdapter;
import supportclasses.MyLinearLayoutManager;

public class FragmentMenuItems extends Fragment implements View.OnClickListener {


    DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
    ViewPager mViewPager;
    View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_menu_items, container, false);

        initViewPager();



        return mView;
    }

    private void initViewPager() {

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int iconRowLength;
        int layoutHeight, layoutWidth;
        int orientation = getResources().getConfiguration().orientation;

        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            iconRowLength = 8;
            layoutHeight = (int)(metrics.heightPixels*.1);
            layoutWidth = (int)(metrics.widthPixels*.1);

        }else{
            iconRowLength = 9;
            layoutHeight = (int)(metrics.heightPixels*.1);
            layoutWidth = (int)(metrics.widthPixels*.2);
        }

        mDemoCollectionPagerAdapter =
                new DemoCollectionPagerAdapter(getActivity().getSupportFragmentManager());
        mViewPager = (ViewPager) mView.findViewById(R.id.pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);

        TabLayout tabLayout = (TabLayout) mView.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);


    }


    @Override
    public void onClick(View v) {



    }

    // Since this is an object collection, use a FragmentStatePagerAdapter,
    // and NOT a FragmentPagerAdapter.

    public class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {

        public DemoCollectionPagerAdapter(FragmentManager fm) {
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


        public float getPageWidth(int position) {
            return 0.3f;
        }

    }


    // Instances of this class are fragments representing a single
    // object in our collection.
    public static class MenuItemFrag extends Fragment {

        RecyclerView recyclerView;


        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_menu_page, container, false);
            int color = this.getArguments().getInt("color");

            rootView.setBackgroundResource(color);
            recyclerView = (RecyclerView) rootView.findViewById(R.id.menu_page_recyclerview);

            initRecycler();

            return rootView;
        }


        private void initRecycler() {

            int count = new Random().nextInt(10);
            ArrayList itemSet = new ArrayList<>();
            for(int a = 1; a <= count; a++)
                itemSet.add(""+a);

            recyclerView.setLayoutManager(new MyLinearLayoutManager(getActivity()));
            BasicRecyclerAdapter rcAdapter = new BasicRecyclerAdapter(getActivity(), itemSet, R.layout.item_textview_imageview);

            recyclerView.setAdapter(rcAdapter);

        }


    }




}