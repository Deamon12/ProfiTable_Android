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

import com.ucsandroid.profitable.adapters.FoodAdditionsRecyclerAdapter;
import com.ucsandroid.profitable.listeners.DialogDismissListener;
import com.ucsandroid.profitable.listeners.RecyclerViewCheckListener;
import com.ucsandroid.profitable.serverclasses.FoodAddition;
import com.ucsandroid.profitable.serverclasses.MenuItem;
import com.ucsandroid.profitable.serverclasses.OrderedItem;

import java.util.ArrayList;
import java.util.List;

public class DialogItemAttributes extends DialogFragment {

    private FoodAdditionsRecyclerAdapter mAdapter;
    private RecyclerView addonsRecycler;

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

        Button doneButton = (Button) v.findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        initAddonsRecycler();

        return v;
    }


    /**
     * If this menuItem already has some selected additions, use them. The already selected additions
     * will be in the MenuItem.attributes JSONArray
     * Otherwise this item has not been adjust, so use the default additions.
     */
    private void initAddonsRecycler() {

        OrderedItem orderedItem = (OrderedItem) getArguments().getSerializable("orderedItem");

        List<FoodAddition> selectedAdditions = orderedItem.getAdditions();
        List<FoodAddition> defaults;
        List<FoodAddition> optionals;
        List<FoodAddition> allAdditions = new ArrayList<>();

        MenuItem defaultItem = Singleton.getInstance().getMenuItem(orderedItem.getMenuItem().getId());

        if (defaultItem != null) {
            defaults = defaultItem.getDefaultAdditions();
            optionals = defaultItem.getOptionalAdditions();

            //Add defaults to an overall List
            for (FoodAddition item : defaults) {
                allAdditions.add(item);
            }

            //Add optionals to an overall List
            for (FoodAddition item : optionals)
                allAdditions.add(item);


            //Set item checked if in selected list
            for (int a = 0; a < selectedAdditions.size(); a++) {
                for (int b = 0; b < allAdditions.size(); b++) {
                    if (selectedAdditions.get(a).getId() == allAdditions.get(b).getId()) {
                        allAdditions.get(b).setChecked(true);
                        //break;
                    }
                }
            }

            //Should have complete additions with checked items (allAdditions)

        }


        int logValue = binlog(allAdditions.size());

        //Set span count to log of dataSet.length
        int spanCount = logValue >= 1 ? logValue : 1;

        addonsRecycler.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), spanCount, GridLayoutManager.HORIZONTAL, false);
        addonsRecycler.setLayoutManager(gridLayoutManager);
        mAdapter = new FoodAdditionsRecyclerAdapter(getActivity(), allAdditions, R.layout.item_checkbox, null, addonsCheckListener);
        addonsRecycler.setAdapter(mAdapter);


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
     *
     * @param bits
     * @return
     */
    public static int binlog(int bits) // returns 0 for bits=0
    {
        int log = 0;
        if ((bits & 0xffff0000) != 0) {
            bits >>>= 16;
            log = 16;
        }
        if (bits >= 256) {
            bits >>>= 8;
            log += 8;
        }
        if (bits >= 16) {
            bits >>>= 4;
            log += 4;
        }
        if (bits >= 4) {
            bits >>>= 2;
            log += 2;
        }
        return log + (bits >>> 1);
    }

    /**
     * Overridden onDismiss to communicate back to the calling fragment
     * So that we the addition items can be passed back to update the UI and data structures
     *
     * @param dialog
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        int customer = getArguments().getInt("customer");
        int position = getArguments().getInt("position");
        ((DialogDismissListener) getTargetFragment()).dialogDismissListener(customer, position, mAdapter.getCheckedDataSet());
    }


}
