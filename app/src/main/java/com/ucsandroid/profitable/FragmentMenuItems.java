package com.ucsandroid.profitable;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

            //Bundle args = new Bundle();
            //args.putInt("ARG_OBJ", i + 1);
            //fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 100;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Menu " + (position + 1);
        }
    }


    // Instances of this class are fragments representing a single
    // object in our collection.
    public static class MenuItemFrag extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_order_amount, container, false);

            return rootView;
        }
    }




}