package com.ucsandroid.profitable;

import android.content.DialogInterface;
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

import com.ucsandroid.profitable.adapters.JSONArrayRecyclerAdapter;
import com.ucsandroid.profitable.listeners.DialogDismissListener;
import com.ucsandroid.profitable.supportclasses.MenuItem;
import com.ucsandroid.profitable.listeners.RecyclerViewCheckListener;

public class DialogItemAttributes extends DialogFragment{

    private JSONArrayRecyclerAdapter mAdapter;
    private RecyclerView addonsRecycler;
    private RecyclerView sidesRecycler;

    static DialogItemAttributes newInstance(int customer, int position, MenuItem item) {
        DialogItemAttributes f = new DialogItemAttributes();

        Bundle args = new Bundle();
        args.putSerializable("menuItem", item);
        args.putInt("customer", customer);
        args.putInt("position", position);
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

        getDialog().setTitle("Choose Additions...");

        View v = inflater.inflate(R.layout.dialog_attributes, container, false);
        addonsRecycler = (RecyclerView) v.findViewById(R.id.addons_recycler);
        sidesRecycler = (RecyclerView) v.findViewById(R.id.sides_recycler);

        Button doneButton = (Button) v.findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        initAddonsRecycler();
        //initSidesRecycler();


        return v;
    }


    /**
     * If this menuItem already has some selected additions, use them. The already selected additions
     * will be in the ServerMenuItem.attributes JSONArray
     * Otherwise this item has not been adjust, so use the default additions.
     */
    private void initAddonsRecycler() {

        MenuItem menuItem = (MenuItem) getArguments().getSerializable("menuItem");
        JSONArray allAddons = new JSONArray();

        try {

            if(menuItem.getAdditions().length() > 0){
                allAddons = menuItem.getAdditions();
            }
            else{
                JSONArray defaults = menuItem.getJsonItem().getJSONArray("defaultAdditions");
                for(int a = 0; a < defaults.length();a++){
                    defaults.getJSONObject(a).put("checked", true);
                    allAddons.put(defaults.get(a));
                }
                JSONArray optional = menuItem.getJsonItem().getJSONArray("optionalAdditions");
                for(int a = 0; a < optional.length();a++){
                    optional.getJSONObject(a).put("checked", false);
                    allAddons.put(optional.get(a));
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        int logValue = binlog(allAddons.length());

        //Set span count to log of dataSet.length
        int spanCount = logValue >= 1 ? logValue : 1;

        addonsRecycler.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), spanCount, GridLayoutManager.HORIZONTAL, false);
        addonsRecycler.setLayoutManager(gridLayoutManager);
        mAdapter = new JSONArrayRecyclerAdapter(getActivity(), allAddons, R.layout.item_checkbox, null, addonsCheckListener);

        addonsRecycler.setAdapter(mAdapter);

    }


    //TODO: not implemented, visibility GONE for now
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
        JSONArrayRecyclerAdapter rcAdapter = new JSONArrayRecyclerAdapter(getActivity(), dataSet, R.layout.item_checkbox, null, sidesCheckListener);
        sidesRecycler.setAdapter(rcAdapter);

    }

    RecyclerViewCheckListener sidesCheckListener = new RecyclerViewCheckListener() {
        @Override
        public void recyclerViewListChecked(View v, int parentPosition, int position, boolean isChecked) {
           // System.out.println("Sides: " + position + " to " + isChecked);
        }
    };

    RecyclerViewCheckListener addonsCheckListener = new RecyclerViewCheckListener() {
        @Override
        public void recyclerViewListChecked(View v, int parentPosition, int position, boolean isChecked) {
            //System.out.println("Addons: " + position + " to " + isChecked);
        }
    };

    /**
     * Method to acquire accurate log2() values - quickly
     * @param bits
     * @return
     */
    public static int binlog( int bits ) // returns 0 for bits=0
    {
        int log = 0;
        if( ( bits & 0xffff0000 ) != 0 ) { bits >>>= 16; log = 16; }
        if( bits >= 256 ) { bits >>>= 8; log += 8; }
        if( bits >= 16  ) { bits >>>= 4; log += 4; }
        if( bits >= 4   ) { bits >>>= 2; log += 2; }
        return log + ( bits >>> 1 );
    }

    /**
     * Overridden onDismiss to communicate back to the interface attached to the calling fragment
     * So that we the addition items can be passed back to update the UI and data structures
     * @param dialog
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        int customer = getArguments().getInt("customer");
        int position = getArguments().getInt("position");
        ((DialogDismissListener)getTargetFragment()).dialogDismissListener(customer, position, mAdapter.getDataSet());
    }



}
