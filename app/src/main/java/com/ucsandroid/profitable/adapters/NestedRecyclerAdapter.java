package com.ucsandroid.profitable.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ucsandroid.profitable.R;
import com.ucsandroid.profitable.listeners.OrderedItemClickListener;
import com.ucsandroid.profitable.serverclasses.Customer;
import com.ucsandroid.profitable.serverclasses.FoodAddition;
import com.ucsandroid.profitable.serverclasses.Location;
import com.ucsandroid.profitable.serverclasses.MenuItem;
import com.ucsandroid.profitable.serverclasses.OrderedItem;
import com.ucsandroid.profitable.serverclasses.Tab;
import com.ucsandroid.profitable.supportclasses.MyLinearLayoutManager;
import com.ucsandroid.profitable.listeners.RecyclerViewLongClickListener;

import java.util.List;

public class NestedRecyclerAdapter extends RecyclerView.Adapter<NestedRecyclerAdapter.ViewHolder> {

    private int layout;

    private Location locationData;
    private Tab mTab;

    private ViewGroup.LayoutParams layoutParams;
    private static Context context;
    private int lastClickedItem = -11;
    private int selectedPosition = -11;
    private OrderedItemClickListener nestedClickListener;
    private RecyclerViewLongClickListener nestedLongClickListener;


    public NestedRecyclerAdapter(Context context, Location dataSet, int layout, ViewGroup.LayoutParams params, OrderedItemClickListener clickListener,
                                 RecyclerViewLongClickListener longClickListener) {
        this.locationData = dataSet;
        this.context = context;
        this.layout = layout;
        this.layoutParams = params;
        this.nestedClickListener = clickListener;
        this.nestedLongClickListener = longClickListener;

        //Could use Singleton.getInst.getCurrentLocation

        mTab = locationData.getCurrentTab();

    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ImageView mCommentImageView;
        public ImageView mSettingsButton;
        public TextView mCommentTextView;
        public TextView mTextView;
        public RecyclerView recyclerView;
        private CardView cardView;
        OrderedItemRecyclerAdapter rcAdapter;

        public ViewHolder(View v) {
            super(v);

            mCommentTextView = (TextView) v.findViewById(R.id.comment_text);
            mCommentImageView = (ImageView) v.findViewById(R.id.comment_image);
            mSettingsButton = (ImageView) v.findViewById(R.id.settings);
            mTextView = (TextView) v.findViewById(R.id.tile_name_text);
            cardView = (CardView) v.findViewById(R.id.the_cardview);
            recyclerView = (RecyclerView) v.findViewById(R.id.item_recycler);
            recyclerView.setHasFixedSize(false);
            recyclerView.setLayoutManager(new MyLinearLayoutManager(context));
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
            mCommentImageView.setOnClickListener(this);
            cardView.setOnClickListener(this);
            mSettingsButton.setOnClickListener(this);

        }


        /**
         * Changes the bg color of the currently selected card.
         * Resets the recently selected tile, if one exists.
         * Update only the altered viewholders, if they are being shown.
         *
         * @param v
         */
        @Override
        public void onClick(View v) {


            if(v == mCommentImageView){
                showCustomerCommentDialog(getAdapterPosition());
            }
            else if(v == mSettingsButton){
                showCustomerEditDialog(getAdapterPosition());
            }
            else{

                if (selectedPosition == getAdapterPosition()) {
                    lastClickedItem = selectedPosition;
                    selectedPosition = -1;
                } else {
                    lastClickedItem = selectedPosition;
                    selectedPosition = getAdapterPosition();
                }

                if (lastClickedItem != -1)
                    notifyItemChanged(lastClickedItem);
                if (selectedPosition != -1)
                    notifyItemChanged(selectedPosition);

            }

        }

        @Override
        public boolean onLongClick(View v) {

            showCustomerEditDialog(getAdapterPosition());

            return true;
        }
    }

    /**
     * Add new order/customer to local data structure
     */
    //TODO add to server
    public void addCustomer() {
        mTab.addCustomer(new Customer());
        selectedPosition = 0;
        notifyDataSetChanged();
    }

    /**
     * Remove customer from table, update datastructure, update UI
     * @param position
     */
    public void removeCustomer(int position) {
        mTab.removeCustomer(position);
        //locationData.removeCustomer(position);
        selectedPosition = -1;
        sendUpdateAmountBroadcast();
        notifyDataSetChanged();


    }

    public NestedRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        if (layoutParams == null) {
        } else {
            //v.getLayoutParams().height = layoutParams.height;
            v.getLayoutParams().width = layoutParams.width;
        }

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        //System.out.println()

        if(!mTab.getCustomers().get(position).getCustomerNotes().equalsIgnoreCase("")){
            holder.mCommentTextView.setVisibility(View.VISIBLE);
            holder.mCommentTextView.setText(mTab.getCustomers().get(position).getCustomerNotes());
        }
        else{
            holder.mCommentTextView.setVisibility(View.GONE);
        }


        holder.mTextView.setText("Customer " + (getItemCount()-position));

        //initialize the last tile as selected
        if (selectedPosition == -11 && position == (getItemCount() - 1)) {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.primary_light));
            selectedPosition = (getItemCount() - 1);
        } else if (selectedPosition != -1 && position == selectedPosition) {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.primary_light));
        } else {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.gray_light));
        }


        holder.rcAdapter = new OrderedItemRecyclerAdapter(context, locationData.getCurrentTab().getCustomers().get(position).getOrder(), R.layout.item_textview_textview2, position, null,
                orderedItemClickListener, longClickListener);
        holder.recyclerView.setAdapter(holder.rcAdapter);

    }

    @Override
    public int getItemCount() {
        return mTab.getCustomers().size();
    }


    public int getSelectedPosition() {
        return selectedPosition;
    }

    /**
     * Setter method to be accessed from the fragment that contains this adapter
     * @param customerPosition the customer position in the array
     * @param item
     */
    public void addItemToCustomer(int customerPosition, OrderedItem item) {

        if (selectedPosition != -1) {
            mTab.getCustomers().get(selectedPosition).addItem(item);
            notifyItemChanged(selectedPosition);
        }
    }

    //Remove a specific item from the items list
    public void removeItemFromCustomer(int customer, int position) {

        //TODO send remove item webservice

        locationData.getCurrentTab().getCustomers().get(customer).getOrder().get(position).setOrderedItemStatus("");
        locationData.getCurrentTab().getCustomers().get(customer).removeItem(position);

        notifyItemChanged(customer);
    }

    //Update the additions for a specific item
    public void setAdditionsForItem(int customer, int position, List<FoodAddition> additions) {

        mTab.getCustomers().get(customer).getOrder().get(position).setAdditions(additions);
        notifyItemChanged(customer);
    }


    public OrderedItem getOrderedItemFromCustomer(int customer, int position){
        return mTab.getCustomers().get(customer).getOrder().get(position);
    }

    public MenuItem getItemFromCustomer(int customer, int position){

        return mTab.getCustomers().get(customer).getOrder().get(position).getMenuItem();
    }

    /**
     * Pass clicks from nested recyclerview through parent recyclerview, to fragment
     */
    OrderedItemClickListener orderedItemClickListener = new OrderedItemClickListener() {
        @Override
        public void recyclerViewListClicked(View v, int parentPosition, int position, OrderedItem item) {
            nestedClickListener.recyclerViewListClicked(v, parentPosition, position, item);
        }
    };

    /**
     * Pass clicks from nested recyclerview through parent recyclerview, to fragment
     */
    RecyclerViewLongClickListener longClickListener = new RecyclerViewLongClickListener() {

        @Override
        public void recyclerViewListLongClicked(View v, int parentPosition, int position, MenuItem item) {
            nestedLongClickListener.recyclerViewListLongClicked(v, parentPosition, position, item);
        }
    };


    private void showCustomerEditDialog(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("What to do...Customer "+(getItemCount()-position));

        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                selectedPosition = position;
                notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Remove", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removeCustomer(position);
            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        builder.show();
    }

    private void sendUpdateAmountBroadcast(){
        Intent updateIntent = new Intent("update-amount");
        LocalBroadcastManager.getInstance(context).sendBroadcast(updateIntent);
    }


    private void showCustomerCommentDialog(final int position){

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        final EditText edittext = new EditText(context);
        edittext.setPadding(20,10,20,10);
        edittext.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        edittext.setSingleLine(false);
        dialog.setTitle("Add comment");


        edittext.setText(mTab.getCustomers().get(position).getCustomerNotes());
        dialog.setView(edittext);

        dialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String commentText = edittext.getText().toString();
                mTab.getCustomers().get(position).setCustomerNotes(commentText);
                notifyItemChanged(position);

            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        dialog.show();
    }

}