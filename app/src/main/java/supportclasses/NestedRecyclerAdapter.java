package supportclasses;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ucsandroid.profitable.R;

public class NestedRecyclerAdapter extends RecyclerView.Adapter<NestedRecyclerAdapter.ViewHolder> {

    private int layout;

    private Table tableData;

    private ViewGroup.LayoutParams layoutParams;
    private static Context context;
    private int lastClickedItem = -11;
    private int selectedPosition = -11;
    private RecyclerViewClickListener nestedClickListener;
    private RecyclerViewLongClickListener nestedLongClickListener;

    private ViewHolder vh;


    public NestedRecyclerAdapter(Context context, Table dataSet, int layout, ViewGroup.LayoutParams params, RecyclerViewClickListener clickListener,
                                 RecyclerViewLongClickListener longClickListener) {
        this.tableData = dataSet;
        this.context = context;
        this.layout = layout;
        this.layoutParams = params;
        this.nestedClickListener = clickListener;
        this.nestedLongClickListener = longClickListener;

    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mTextView;
        public RecyclerView recyclerView;
        private CardView cardView;
        MenuItemRecyclerAdapter rcAdapter;

        public ViewHolder(View v) {
            super(v);

            mTextView = (TextView) v.findViewById(R.id.tile_name_text);
            cardView = (CardView) v.findViewById(R.id.the_cardview);
            recyclerView = (RecyclerView) v.findViewById(R.id.item_recycler);
            recyclerView.setHasFixedSize(false);
            recyclerView.setLayoutManager(new MyLinearLayoutManager(context));
            v.setOnClickListener(this);
            cardView.setOnClickListener(this);

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

    /**
     * Add new order/customer to local data structure
     */
    public void addCustomer() {

        tableData.addOrder(new Order());
        selectedPosition = 0;//getItemCount() - 1;
        notifyDataSetChanged();

    }

    public NestedRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        if (layoutParams == null) {
        } else {
            //v.getLayoutParams().height = layoutParams.height;
            v.getLayoutParams().width = layoutParams.width;
        }
        vh = new ViewHolder(v);


        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        holder.mTextView.setText("Customer " + (position+1));


        //initialize the last tile as selected
        if (selectedPosition == -11 && position == (getItemCount() - 1)) {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.primary_light));
            selectedPosition = (getItemCount() - 1);
        } else if (selectedPosition != -1 && position == selectedPosition) {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.primary_light));
        } else {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.gray_light));
        }


        //Get menuItems from order object
        //Table # from singleton, Order # = position
        //dataset input = Arraylist<MenuItems>


        holder.rcAdapter = new MenuItemRecyclerAdapter(context, tableData.getOrder(position).getItems(), R.layout.item_textview, position, null,
                clickListener, longClickListener);
        holder.recyclerView.setAdapter(holder.rcAdapter);


    }


    @Override
    public int getItemCount() {
        return tableData.getOrders().size();
    }


    public int getSelectedPosition() {
        return selectedPosition;
    }

    /**
     * Setter method to be accessed from the fragment that contains this adapter
     *
     * @param customerPosition the customer position in the array
     * @param item             -  the food as a JSONObject
     */
    public void addItemToCustomer(int customerPosition, MenuItem item) {

        if (selectedPosition != -1) {

            tableData.getOrder(selectedPosition).addMenuItem(item);

            notifyItemChanged(selectedPosition);

        }

    }


    /**
     * Pass clicks from nested recyclerview through parent recyclerview, to fragment
     */
    RecyclerViewClickListener clickListener = new RecyclerViewClickListener() {

        @Override
        public void recyclerViewListClicked(View v, int parentPosition, int position, MenuItem item) {
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



}