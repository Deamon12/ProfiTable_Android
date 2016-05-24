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

import com.google.gson.Gson;
import com.ucsandroid.profitable.adapters.CategoryRecyclerAdapter;
import com.ucsandroid.profitable.listeners.MenuItemClickListener;
import com.ucsandroid.profitable.serverclasses.Category;
import com.ucsandroid.profitable.serverclasses.MenuItem;
import com.ucsandroid.profitable.supportclasses.MyLinearLayoutManager;


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

    public static FragmentMenuItem newInstance(int color, Category dataset) {
        FragmentMenuItem fragment = new FragmentMenuItem();
        Bundle args = new Bundle();
        args.putInt("color",  color);

        Gson gson = new Gson();
        String json = gson.toJson(dataset);
        args.putString("dataset", json);

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

        Gson gson = new Gson();
        Category category = gson.fromJson(getArguments().getString("dataset"), Category.class);

        //System.out.println("dataset: "+getArguments().getString("dataset"));
        //JSONArray dataset = new JSONArray(getArguments().getString("dataset"));

        recyclerView.setLayoutManager(new MyLinearLayoutManager(getActivity()));
        CategoryRecyclerAdapter mAdapter = new CategoryRecyclerAdapter(getActivity(), category, R.layout.item_textview_textview, null, clickListener);
        recyclerView.setAdapter(mAdapter);


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
    MenuItemClickListener clickListener = new MenuItemClickListener() {
        @Override
        public void recyclerViewListClicked(View v, int parentPosition, int position, MenuItem item) {
            sendAddItemToCustomerBroadcast(parentPosition, position, item);
        }
    };

}