package com.ucsandroid.profitable;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;

import com.ucsandroid.profitable.adapters.JSONArrayRecyclerAdapter;
import com.ucsandroid.profitable.supportclasses.MenuItem;
import com.ucsandroid.profitable.supportclasses.MyLinearLayoutManager;
import com.ucsandroid.profitable.listeners.RecyclerViewClickListener;


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

    private void sendAddItemToCustomerBroadcast(int customer, int position, MenuItem item) {
        Intent intent = new Intent("add-item");
        intent.putExtra("menuItem", item);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    /**
     * Recieves the clicked position from a menu category
     * Pass off an item from a MenuPage, to the OrderFragment to start the add item flow.
     */
    RecyclerViewClickListener clickListener = new RecyclerViewClickListener() {
        @Override
        public void recyclerViewListClicked(View v, int parentPosition, int position, MenuItem item) {
            sendAddItemToCustomerBroadcast(parentPosition, position, item);
        }
    };

}