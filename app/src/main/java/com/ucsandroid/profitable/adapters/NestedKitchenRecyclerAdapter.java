package com.ucsandroid.profitable.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ucsandroid.profitable.R;
import com.ucsandroid.profitable.Singleton;
import com.ucsandroid.profitable.listeners.NestedClickListener;
import com.ucsandroid.profitable.listeners.OrderedItemClickListener;
import com.ucsandroid.profitable.serverclasses.Customer;
import com.ucsandroid.profitable.serverclasses.OrderedItem;
import com.ucsandroid.profitable.serverclasses.Tab;
import com.ucsandroid.profitable.supportclasses.MyLinearLayoutManager;

import org.json.JSONObject;

import java.util.List;

public class NestedKitchenRecyclerAdapter extends RecyclerView.Adapter<NestedKitchenRecyclerAdapter.ViewHolder> {

    private int layout;

    private List<Tab> tabData;

    private ViewGroup.LayoutParams layoutParams;
    private static Context context;
    private OrderedItemClickListener orderedItemClickListener;
    private NestedClickListener nestedClickListener;

    public NestedKitchenRecyclerAdapter(Context context, List<Tab> dataSet, int layout, ViewGroup.LayoutParams params,
                                        NestedClickListener clickListener, OrderedItemClickListener orderedItemClickListener) {
        tabData = dataSet;
        this.context = context;
        this.layout = layout;
        this.layoutParams = params;
        this.nestedClickListener = clickListener;
        this.orderedItemClickListener = orderedItemClickListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private ImageView doneButton;
        public TextView mCommentTextView;
        public TextView mTextView;
        public RecyclerView recyclerView;
        private CardView cardView;
        private KitchenCustomerRecyclerAdapter mAdapter;

        public ViewHolder(View v) {
            super(v);

            mTextView = (TextView) v.findViewById(R.id.tile_text);

            if (layout == R.layout.tile_kitchen_order) {

                doneButton = (ImageView) v.findViewById(R.id.check_button);
                mCommentTextView = (TextView) v.findViewById(R.id.comment_text);

                cardView = (CardView) v.findViewById(R.id.the_cardview);

                recyclerView = (RecyclerView) v.findViewById(R.id.item_recycler);
                recyclerView.setHasFixedSize(false);
                recyclerView.setLayoutManager(new MyLinearLayoutManager(context));

                v.setOnClickListener(this);
                v.setOnLongClickListener(this);

                cardView.setOnClickListener(this);
                doneButton.setOnClickListener(this);
            } else {

            }
        }

        @Override
        public void onClick(View v) {

            if (v == doneButton) {
                //Do volley and update UI
                nestedClickListener.nestedClickListener(getAdapterPosition(), tabData.get(getAdapterPosition()));
            }
            else if(v == cardView){
                Toast.makeText(context,tabData.get(getAdapterPosition()).allOrdersReady()+"", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public boolean onLongClick(View v) {

            System.out.println("In long click: " + tabData.get(getAdapterPosition()).getTabStatus());
            System.out.println("In long click: " + tabData.get(getAdapterPosition()).allOrdersReady());

            for(Customer customer : tabData.get(getAdapterPosition()).getCustomers()){

                for(OrderedItem item : customer.getOrders()){
                    System.out.println(item.getMenuItem().getName()+" is "+item.getOrderedItemStatus());
                    if(item.getOrderedItemStatus().equalsIgnoreCase("ordered") ||
                            item.getOrderedItemStatus().equalsIgnoreCase("ready")){
                        System.out.println("Attempting to deliver: "+item.getOrderedItemId());
                        doStatusVolleyCall(item.getOrderedItemId(), "delivered");
                    }
                }
            }

            //tabData.remove(getAdapterPosition());
            //notifyDataSetChanged();

            return true;
        }
    }


    public NestedKitchenRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

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


        //Green for ready, Blue for not ready
        if (tabData.get(position).allOrdersReady())
            holder.doneButton.setColorFilter(ContextCompat.getColor(context, R.color.primary));
        else
            holder.doneButton.setColorFilter(ContextCompat.getColor(context, R.color.accent));


        holder.mCommentTextView.setVisibility(View.GONE); //todo: comments not being used


        if (tabData.get(position).getServer() != null)
            holder.mTextView.setText("Server: " + tabData.get(position).getServer().getFirstName()); //tabData.get(position).getTabId()); //(position+1)
        else
            holder.mTextView.setText("Order#  " + tabData.get(position).getTabId());


        holder.mAdapter = new KitchenCustomerRecyclerAdapter(context,
                tabData.get(position).getCustomers(),
                R.layout.tile_recyclerview,
                position,
                null,
                orderedItemClickListener);

        holder.recyclerView.setAdapter(holder.mAdapter);

    }

    @Override
    public int getItemCount() {
        return tabData.size();
    }


    public Tab getDataItem(int position) {
        return tabData.get(position);
    }

    public void setOrderStatus(int tab, int customer, int orderPosition, String orderedItemStatus) {
        tabData.get(tab).getCustomers().get(customer).getOrders().get(orderPosition).setOrderedItemStatus(orderedItemStatus);
    }

    private void doStatusVolleyCall(int orderId, String status) {

        Uri.Builder builder = Uri.parse("http://52.38.148.241:8080").buildUpon();
        builder.appendPath("com.ucsandroid.profitable")
                .appendPath("rest")
                .appendPath("orders")
                .appendPath("item")
                .appendPath(status+"")
                .appendQueryParameter("ordered_item_id", orderId+"");
        String myUrl = builder.build().toString();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.PUT,
                myUrl,
                (JSONObject) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response: "+response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("error: "+error);
                    }
                });

        Singleton.getInstance().addToRequestQueue(jsObjRequest);

    }


}