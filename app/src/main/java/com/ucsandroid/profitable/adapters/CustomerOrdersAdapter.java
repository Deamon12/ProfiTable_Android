package com.ucsandroid.profitable.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.ucsandroid.profitable.R;
import com.ucsandroid.profitable.Singleton;
import com.ucsandroid.profitable.listeners.OrderedItemClickListener;
import com.ucsandroid.profitable.serverclasses.Customer;
import com.ucsandroid.profitable.serverclasses.FoodAddition;
import com.ucsandroid.profitable.serverclasses.Location;
import com.ucsandroid.profitable.serverclasses.MenuItem;
import com.ucsandroid.profitable.serverclasses.OrderedItem;
import com.ucsandroid.profitable.supportclasses.MyLinearLayoutManager;
import com.ucsandroid.profitable.listeners.RecyclerViewLongClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CustomerOrdersAdapter extends RecyclerView.Adapter<CustomerOrdersAdapter.ViewHolder> {

    private int layout;

    private Location locationData;

    private ViewGroup.LayoutParams layoutParams;
    private static Context context;
    private int lastClickedItem = -11;
    private int selectedPosition = -11;
    private OrderedItemClickListener nestedClickListener;
    private RecyclerViewLongClickListener nestedLongClickListener;


    public CustomerOrdersAdapter(Context context, Location dataSet, int layout, ViewGroup.LayoutParams params, OrderedItemClickListener clickListener,
                                 RecyclerViewLongClickListener longClickListener) {

        this.locationData = dataSet;
        this.context = context;
        this.layout = layout;
        this.layoutParams = params;
        this.nestedClickListener = clickListener;
        this.nestedLongClickListener = longClickListener;

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
            mTextView = (TextView) v.findViewById(R.id.tile_text);
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
     * Update local data and call method to check location status
     */
    public void addCustomer() {

        locationData.getCurrentTab().addCustomer(new Customer(0, locationData.getCurrentTab().getTabId()));
        selectedPosition = 0;
        checkLocationStatus();
        notifyDataSetChanged();
    }

    /**
     * Remove customer from table, update datastructure, update UI
     * @param position
     */
    public void removeCustomer(int position) {

        locationData.getCurrentTab().removeCustomer(position);
        selectedPosition = -1;
        sendUpdateAmountBroadcast();
        notifyDataSetChanged();
        checkLocationStatus();
    }

    /**
     * Evaluate local data, and update server if needed
     */
    private void checkLocationStatus() {

        //No customers, set this table as free
        //Remove tab
        if(locationData.getCurrentTab().getCustomers().size() == 0){
            setLocationStatusVolley(context.getString(R.string.location_available), locationData.getId());
            closeTabAtLocation();
        }
        else if(locationData.getCurrentTab().getCustomers().size() > 0 &&
                locationData.getStatus().equalsIgnoreCase("available")){
            setLocationStatusVolley(context.getString(R.string.location_occupied), locationData.getId());

            //add tab
            if(locationData.getCurrentTab().getTabId() == 0){
                openTabAtLocation();
            }
        }

    }


    public CustomerOrdersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        if (layoutParams != null) {
            v.getLayoutParams().height = layoutParams.height;
            v.getLayoutParams().width = layoutParams.width;
        }

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if(!locationData.getCurrentTab().getCustomers().get(position).getCustomerNotes().equalsIgnoreCase("")){
            holder.mCommentTextView.setVisibility(View.VISIBLE);
            holder.mCommentTextView.setText(locationData.getCurrentTab().getCustomers().get(position).getCustomerNotes());
        }
        else{
            holder.mCommentTextView.setVisibility(View.GONE);
        }

        holder.mTextView.setText("Customer " + (getItemCount()-position));

        //initialize the last tile as selected
        if (selectedPosition == -11 && position == (getItemCount() - 1)) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_light));
            selectedPosition = (getItemCount() - 1);
        } else if (selectedPosition != -1 && position == selectedPosition) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_light));
        } else {
            holder.cardView.setCardBackgroundColor(0);
        }


        holder.rcAdapter = new OrderedItemRecyclerAdapter(context,
                locationData.getCurrentTab().getCustomers().get(position).getOrders(),
                R.layout.item_textview_textview2,
                position,
                null,
                orderedItemClickListener,
                longClickListener);

        holder.recyclerView.setAdapter(holder.rcAdapter);

    }

    @Override
    public int getItemCount() {
        return locationData.getCurrentTab().getCustomers().size();
    }


    public int getSelectedPosition() {
        return selectedPosition;
    }

    /**
     * Setter method to be accessed from the fragment that contains this adapter
     * @param item
     */
    public void addOrderedItemToCustomer(OrderedItem item) {

        if (selectedPosition != -1) {
            locationData.getCurrentTab().getCustomers().get(selectedPosition).addItem(item);
            notifyItemChanged(selectedPosition);
        }
    }

    //TODO send remove item webservice
    //Remove a specific item from the items list
    public void removeItemFromCustomer(int customer, int position) {

        locationData.getCurrentTab().getCustomers().get(customer).getOrders().get(position).setOrderedItemStatus("");
        locationData.getCurrentTab().getCustomers().get(customer).removeItem(position);

        notifyItemChanged(customer);
    }

    //Update the additions for a specific item
    public void setAdditionsForItem(int customer, int position, List<FoodAddition> additions) {

        locationData.getCurrentTab().getCustomers().get(customer).getOrders().get(position).setAdditions(additions);
        notifyItemChanged(customer);
    }


    public OrderedItem getOrderedItemFromCustomer(int customer, int position){
        return locationData.getCurrentTab().getCustomers().get(customer).getOrders().get(position);
    }

    //Pass clicks from nested recyclerview through parent recyclerview, to fragment
    OrderedItemClickListener orderedItemClickListener = new OrderedItemClickListener() {
        @Override
        public void recyclerViewListClicked(View v, int parentPosition, int position, OrderedItem item) {
            nestedClickListener.recyclerViewListClicked(v, parentPosition, position, item);
        }
    };

    //Pass clicks from nested recyclerview through parent recyclerview, to fragment
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
                if(locationData.getCurrentTab().getCustomers().size() == 1){
                    showRemoveLastCustomerConfirm(position);
                }
                else{
                    removeCustomer(position);
                }
            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        builder.show();
    }

    private void showRemoveLastCustomerConfirm(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Removing this customer will make this table available"+(getItemCount()-position));

        TextView textView = new TextView(context);

        textView.setText("Removing this customer will close the table and clear all the data attached to it. Continue?  ");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removeCustomer(position);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

        edittext.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        edittext.setHint("Comment");
        //edittext.setSingleLine(false);
        dialog.setTitle("Add comment to this order");


        edittext.setText(locationData.getCurrentTab().getCustomers().get(position).getCustomerNotes());
        dialog.setView(edittext);

        dialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String commentText = edittext.getText().toString();
                locationData.getCurrentTab().getCustomers().get(position).setCustomerNotes(commentText);
                notifyItemChanged(position);

            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        dialog.show();


    }




    //----- Volley Calls -------//

    private void setLocationStatusVolley(String status, int locationId) {

        //free or occupy
        Uri.Builder builder = Uri.parse("http://52.38.148.241:8080").buildUpon();
        builder.appendPath("com.ucsandroid.profitable")
                .appendPath("rest")
                .appendPath("location")
                .appendPath(status)
                .appendQueryParameter("location_id", locationId+"");

        String myUrl = builder.build().toString();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.PUT,
                myUrl,
                (JSONObject) null,
                statusSuccessListener,
                statusErrorListener);

        Singleton.getInstance().addToRequestQueue(jsObjRequest);
    }

    private Response.Listener statusSuccessListener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {

            try {
                JSONObject theResponse = new JSONObject(response.toString());
                if (theResponse.getBoolean("success") && theResponse.has("result")) {

                } else {
                    System.out.println("Error settings location status"); //todo snackbar?
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    Response.ErrorListener statusErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            System.out.println("ErrorListener updating deviceID");
        }
    };



    private void closeTabAtLocation(){

        System.out.println("Closing tab: "+Singleton.getInstance().getCurrentLocation().getCurrentTab().getTabId());

        int locationId = Singleton.getInstance().getCurrentLocation().getId();
        int tabId = Singleton.getInstance().getCurrentLocation().getCurrentTab().getTabId();

        Uri.Builder builder = Uri.parse("http://52.38.148.241:8080").buildUpon();
        builder.appendPath("com.ucsandroid.profitable")
                .appendPath("rest")
                .appendPath("orders")
                .appendPath("close")
                .appendQueryParameter("location_id", locationId+"")
                .appendQueryParameter("employee_id", tabId+"");

        String myUrl = builder.build().toString();



        StringRequest jsObjRequest = new StringRequest(Request.Method.POST,
                myUrl,
                closeTabSuccessListener,
                errorListener);

        Singleton.getInstance().addToRequestQueue(jsObjRequest);

    }

    private Response.Listener closeTabSuccessListener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {

            //Singleton.getInstance().getCurrentLocation().getCurrentTab().setTabId();
            System.out.println("Volley success: " + response);
        }
    };


    private void openTabAtLocation(){

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String employeeId = settings.getString(context.getString(R.string.employee_id), "");
        int locationId = Singleton.getInstance().getCurrentLocation().getId();

        Uri.Builder builder = Uri.parse("http://52.38.148.241:8080").buildUpon();
        builder.appendPath("com.ucsandroid.profitable")
                .appendPath("rest")
                .appendPath("orders")
                .appendPath("seat")
                .appendQueryParameter("location_id", locationId+"")
                .appendQueryParameter("employee_id", employeeId);

        String myUrl = builder.build().toString();

        System.out.println("SEAT URL: "+myUrl);

        StringRequest jsObjRequest = new StringRequest(Request.Method.POST,
                myUrl,
                openTabSuccessListener,
                errorListener);

        Singleton.getInstance().addToRequestQueue(jsObjRequest);

    }


    private Response.Listener openTabSuccessListener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {

            try {

                JSONObject theResponse = new JSONObject(response.toString());
                System.out.println("Volley success: " + response);

                if (theResponse.getBoolean("success") && theResponse.has("result")) {

                    int tabId = theResponse.getJSONObject("result").getInt("tabId");
                    Singleton.getInstance().getCurrentLocation().getCurrentTab().setTabId(tabId);
                    locationData.getCurrentTab().setTabId(tabId);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Singleton.getInstance().getCurrentLocation().getCurrentTab().setTabId();

        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

            /*if(context.findViewById(R.id.the_coordinator)  != null){
                Snackbar snackbar = Snackbar
                        .make(getActivity().findViewById(R.id.the_coordinator), "Error sending order", Snackbar.LENGTH_LONG);

                snackbar.show();
            }*/
        }
    };







}