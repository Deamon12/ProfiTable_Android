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

import java.util.ArrayList;
import java.util.Random;

import supportclasses.BasicRecyclerAdapter;
import supportclasses.MyLinearLayoutManager;
import supportclasses.RecyclerViewClickListener;

public class FragmentMenuItems extends Fragment {


    private static BasicRecyclerAdapter mAdapter;
    private MenuCollectionStatePagerAdapter mMenuPages;
    private static ViewPager mViewPager;
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_menu_items, container, false);
        initViewPager();

        return mView;
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
            ArrayList itemSet = new ArrayList<>();
            for(int a = 1; a <= count; a++)
                itemSet.add("Food item "+a);

            recyclerView.setLayoutManager(new MyLinearLayoutManager(getActivity()));
            mAdapter = new BasicRecyclerAdapter(getActivity(), itemSet, R.layout.item_textview_imageview, null, clickListener);

            recyclerView.setAdapter(mAdapter);


        }

        /**
         * Recieves the clicked position from a menu category
         * Pass off an itemId from a MenuPage, to the OrderFragment to start the add item flow.
         */
        RecyclerViewClickListener clickListener = new RecyclerViewClickListener() {

            @Override
            public void recyclerViewListClicked(View v, int position, String item) {
                System.out.println("position: "+position);
                System.out.println("item: "+item);
                //System.out.println("not accurate page: "+mViewPager.getCurrentItem()); //TODO: dont use. have id with food item
               // System.out.println("hmm: "+getActivity().getSupportFragmentManager().findFragmentById(R.id.orders_frag_container).isVisible());

                FragmentOrders orderFrag = (FragmentOrders) getActivity().getSupportFragmentManager().findFragmentById(R.id.orders_frag_container);

                if(orderFrag != null){
                    orderFrag.addItem(item);
                }


            }
        };

    }

}

