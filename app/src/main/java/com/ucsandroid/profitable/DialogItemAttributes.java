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

import com.ucsandroid.profitable.adapters.FoodAdditionsRecyclerAdapter;
import com.ucsandroid.profitable.adapters.JSONArrayRecyclerAdapter;
import com.ucsandroid.profitable.listeners.DialogDismissListener;
import com.ucsandroid.profitable.listeners.RecyclerViewCheckListener;
import com.ucsandroid.profitable.serverclasses.Category;
import com.ucsandroid.profitable.serverclasses.FoodAddition;
import com.ucsandroid.profitable.serverclasses.MenuItem;
import com.ucsandroid.profitable.serverclasses.OrderedItem;

import java.util.ArrayList;
import java.util.List;

public class DialogItemAttributes extends DialogFragment{

    private FoodAdditionsRecyclerAdapter mAdapter;
    private RecyclerView addonsRecycler;
    private RecyclerView sidesRecycler;

    static DialogItemAttributes newInstance(int customer, int position, OrderedItem item) {
        DialogItemAttributes f = new DialogItemAttributes();

        Bundle args = new Bundle();
        args.putSerializable("orderedItem", item);
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
     * will be in the MenuItem.attributes JSONArray
     * Otherwise this item has not been adjust, so use the default additions.
     */
    private void initAddonsRecycler() {


        //TODO :need defaults and optionals

        OrderedItem orderedItem = (OrderedItem) getArguments().getSerializable("orderedItem");

        List<FoodAddition> additions = orderedItem.getAdditions();
        List<FoodAddition> defaults;// = orderedItem.getMenuItem().getDefaultAdditions(); //empty?
        List<FoodAddition> optionals;// = orderedItem.getMenuItem().getOptionalAdditions(); //empty?



        /*

        //All this sucks, need defaults, and optionals attached to menuItems
        //Start bullshit parsing
        List<Category> categs = Singleton.getInstance().getmCategories();

        MenuItem foundItem = null;

        for(int a = 0; a < categs.size(); a++){
            //System.out.println("cats: "+categs);
            if(foundItem != null)
                break;

            for(int b = 0; b < categs.get(a).getMenuItems().size(); b++){
                if(categs.get(a).getMenuItems().get(b).getId() == orderedItem.getMenuItem().getId()){
                    foundItem = categs.get(a).getMenuItems().get(b);
                    //System.out.println("found");
                    break;
                }
            }
        }

        List<FoodAddition> allAdditions = new ArrayList<>();

        if(foundItem != null){
            //System.out.println("item found ");
            defaults = foundItem.getDefaultAdditions();
            optionals = foundItem.getOptionalAdditions();

            //Add defaults, and evaluate if they are checked or not
            for(int a = 0; a < defaults.size(); a++){
                for(int b = 0; b < additions.size(); b++){
                    if(defaults.get(a).getId() == additions.get(b).getId()){
                        //System.out.println("Checked: "+defaults.get(a).getName());
                        defaults.get(a).setChecked(true);
                        break;
                    }
                    else{
                        defaults.get(a).setChecked(false);
                    }
                }
                allAdditions.add(defaults.get(a));
            }

            //Add optionals, and see if they are checked
            for(int a = 0; a < optionals.size(); a++){
                for(int b = 0; b < additions.size(); b++){
                    if(optionals.get(a).getId() == additions.get(b).getId()){
                        //System.out.println("Checked: "+optionals.get(a).getName());
                        optionals.get(a).setChecked(true);
                        break;
                    }
                    else{
                        optionals.get(a).setChecked(false);
                    }
                }
                allAdditions.add(optionals.get(a));
            }

            //End bullshit parsing




            int logValue = binlog(allAdditions.size());

            //Set span count to log of dataSet.length
            int spanCount = logValue >= 1 ? logValue : 1;

            addonsRecycler.setHasFixedSize(true);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), spanCount, GridLayoutManager.HORIZONTAL, false);
            addonsRecycler.setLayoutManager(gridLayoutManager);
            mAdapter = new FoodAdditionsRecyclerAdapter(getActivity(), allAdditions, R.layout.item_checkbox, null, addonsCheckListener);
            addonsRecycler.setAdapter(mAdapter);

        }
        else{
        */
            int logValue = binlog(additions.size());

            //Set span count to log of dataSet.length
            int spanCount = logValue >= 1 ? logValue : 1;

            addonsRecycler.setHasFixedSize(true);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), spanCount, GridLayoutManager.HORIZONTAL, false);
            addonsRecycler.setLayoutManager(gridLayoutManager);
            mAdapter = new FoodAdditionsRecyclerAdapter(getActivity(), additions, R.layout.item_checkbox, null, addonsCheckListener);
            addonsRecycler.setAdapter(mAdapter);
        //}



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
            System.out.println("Addons: " + position + " to " + isChecked);

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
