package com.ucsandroid.profitable;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.ucsandroid.profitable.adapters.CategoryRecyclerAdapter;
import com.ucsandroid.profitable.listeners.MenuItemClickListener;
import com.ucsandroid.profitable.serverclasses.Category;
import com.ucsandroid.profitable.serverclasses.MenuItem;
import com.ucsandroid.profitable.supportclasses.MyLinearLayoutManager;

import org.json.JSONArray;


public class FragmentReciept extends DialogFragment {

    RecyclerView recyclerView;

    public static FragmentReciept newInstance(int color) {
        FragmentReciept fragment = new FragmentReciept();
        Bundle args = new Bundle();
        args.putInt("color",  color);

        fragment.setArguments(args);
        return fragment;
    }

    public FragmentReciept() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NO_FRAME, 0);


        View rootView = inflater.inflate(R.layout.layout_receipt, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.menu_page_recyclerview);

        //initRecycler();

        return rootView;
    }



    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    /**
     * recycler that shows items for a particular food category
     */
    private void initRecycler() {

        Gson gson = new Gson();
        Category category = gson.fromJson(getArguments().getString("dataset"), Category.class);

        recyclerView.setLayoutManager(new MyLinearLayoutManager(getActivity()));
        CategoryRecyclerAdapter mAdapter = new CategoryRecyclerAdapter(getActivity(), category, R.layout.item_textview_textview, null, null);
        recyclerView.setAdapter(mAdapter);

    }


}