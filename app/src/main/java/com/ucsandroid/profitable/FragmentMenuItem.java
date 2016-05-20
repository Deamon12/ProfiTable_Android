package com.ucsandroid.profitable;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;

import supportclasses.JSONArrayRecyclerAdapter;
import supportclasses.MenuItem;
import supportclasses.MenuItemRecyclerAdapter;
import supportclasses.MyLinearLayoutManager;
import supportclasses.RecyclerViewClickListener;

/**
 * Dynamically build food category fragments
 */


public class FragmentMenuItem extends Fragment {

    RecyclerView recyclerView;

    public static FragmentMenuItem newInstance(int color, JSONArray dataset) {
        FragmentMenuItem fragment = new FragmentMenuItem();
        Bundle args = new Bundle();
        args.putInt("color",  color);
        args.putString("dataset", dataset.toString());

        fragment.setArguments(args);
        return fragment;
    }

    public FragmentMenuItem() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_menu_page, container, false);
        rootView.setBackgroundResource(getArguments().getInt("color"));
        recyclerView = (RecyclerView) rootView.findViewById(R.id.menu_page_recyclerview);


        initRecycler();

        return rootView;
    }


    /**
     * recycler that shows items for a particular food category
     */
    private void initRecycler() {


        try {

            JSONArray dataset = new JSONArray(getArguments().getString("dataset"));

            recyclerView.setLayoutManager(new MyLinearLayoutManager(getActivity()));
            JSONArrayRecyclerAdapter mAdapter = new JSONArrayRecyclerAdapter(getActivity(), dataset, R.layout.item_textview_textview, null, clickListener);
            recyclerView.setAdapter(mAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Recieves the clicked position from a menu category
     * Pass off an itemId from a MenuPage, to the OrderFragment to start the add item flow.
     */
    RecyclerViewClickListener clickListener = new RecyclerViewClickListener() {

        @Override
        public void recyclerViewListClicked(View v, int parentPosition, int position, MenuItem item) {

            FragmentOrders orderFrag = (FragmentOrders) getActivity().getSupportFragmentManager().findFragmentById(R.id.orders_frag_container);

            if (orderFrag != null) {
                orderFrag.addItem(item);
            }

            FragmentOrderAmount amountFrag = (FragmentOrderAmount) getActivity().getSupportFragmentManager().findFragmentById(R.id.amounts_frag_container);

            if (amountFrag != null) {

                try {
                    amountFrag.addItem(item.getJsonItem().getDouble("menuItemPrice"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }



        }
    };

}