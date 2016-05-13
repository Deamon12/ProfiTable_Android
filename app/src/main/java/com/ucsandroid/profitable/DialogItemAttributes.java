package com.ucsandroid.profitable;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import supportclasses.BasicRecyclerAdapter;
import supportclasses.RecyclerViewCheckListener;

public class DialogItemAttributes extends DialogFragment {

    private RecyclerView addonsRecycler;
    private RecyclerView sidesRecycler;

    static DialogItemAttributes newInstance(JSONObject item) { //TODO: array of assets or object id? who knows
        DialogItemAttributes f = new DialogItemAttributes();

        Bundle args = new Bundle();
        //args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().setTitle("Choose Extras");

        View v = inflater.inflate(R.layout.dialog_attributes, container, false);
        addonsRecycler = (RecyclerView) v.findViewById(R.id.addons_recycler);
        sidesRecycler = (RecyclerView) v.findViewById(R.id.sides_recycler);


        Button doneButton = (Button) v.findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Save and dismiss
                getDialog().dismiss();
            }
        });

        initAddonsRecycler();
        initSidesRecycler();


        return v;
    }

    private void initAddonsRecycler() {

        JSONArray dataSet = dataSet = new JSONArray();

        try {

            for(int a = 1; a <= 35;a++){
                JSONObject temp = new JSONObject();
                temp.put("name", "Addon "+a);
                temp.put("checked", false);
                dataSet.put(temp);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        addonsRecycler.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4, GridLayoutManager.HORIZONTAL, false);
        addonsRecycler.setLayoutManager(gridLayoutManager);
        BasicRecyclerAdapter rcAdapter = new BasicRecyclerAdapter(getActivity(), dataSet, R.layout.item_checkbox, null, addonsCheckListener);

        addonsRecycler.setAdapter(rcAdapter);

    }

    private void initSidesRecycler() {

        JSONArray dataSet = dataSet = new JSONArray();

        try {

            for(int a = 1; a <= 35;a++){
                JSONObject temp = new JSONObject();
                temp.put("name", "Side "+a);
                temp.put("checked", false);
                dataSet.put(temp);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        sidesRecycler.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4, GridLayoutManager.HORIZONTAL, false);
        sidesRecycler.setLayoutManager(gridLayoutManager);
        BasicRecyclerAdapter rcAdapter = new BasicRecyclerAdapter(getActivity(), dataSet, R.layout.item_checkbox, null, sidesCheckListener);
        sidesRecycler.setAdapter(rcAdapter);

    }

    RecyclerViewCheckListener sidesCheckListener = new RecyclerViewCheckListener() {
        @Override
        public void recyclerViewListChecked(View v, int parentPosition, int position, boolean isChecked) {
            System.out.println("Sides: " + position + " to " + isChecked);
        }
    };

    RecyclerViewCheckListener addonsCheckListener = new RecyclerViewCheckListener() {
        @Override
        public void recyclerViewListChecked(View v, int parentPosition, int position, boolean isChecked) {
            System.out.println("Addons: " + position + " to " + isChecked);
        }
    };


}
