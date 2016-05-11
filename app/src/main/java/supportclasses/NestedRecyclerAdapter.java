package supportclasses;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ucsandroid.profitable.R;

import java.util.ArrayList;
import java.util.Random;

public class NestedRecyclerAdapter extends RecyclerView.Adapter<NestedRecyclerAdapter.ViewHolder>  {

    private int layout;
    private ArrayList<String> dataSet;
    private ViewGroup.LayoutParams layoutParams;
    private static Context context;
    private int lastClickedItem = -11;
    private int selectedPosition = -11;


    public NestedRecyclerAdapter(Context context, ArrayList dataSet, int layout, ViewGroup.LayoutParams params, RecyclerViewClickListener clickListener) {
        this.dataSet = dataSet;
        this.context = context;
        this.layout = layout;
        this.layoutParams = params;

    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mTextView;
        public RecyclerView recyclerView;
        private CardView cardView;

        public ViewHolder(View v) {
            super(v);

            mTextView = (TextView) v.findViewById(R.id.tile_name_text);
            cardView = (CardView) v.findViewById(R.id.the_cardview);
            recyclerView = (RecyclerView) v.findViewById(R.id.item_recycler);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new MyLinearLayoutManager(context));
            v.setOnClickListener(this);
            cardView.setOnClickListener(this);

        }



        /**
         * Changes the bg color of the currently selected card.
         * Resets the recently selected tile, if one exists.
         * @param v
         */
        @Override
        public void onClick(View v) {

            if(selectedPosition == getAdapterPosition()){
                lastClickedItem = selectedPosition;
                selectedPosition = -1;
            }
            else{
                lastClickedItem = selectedPosition;
                selectedPosition = getAdapterPosition();
            }



            if(lastClickedItem != -1)
                notifyItemChanged(lastClickedItem);
            if (selectedPosition != -1)
                notifyItemChanged(selectedPosition);


        }

    }

    /**
     * Add new tile, and select it
     */
    public void addCustomer(){
        dataSet.add("Customer " + (dataSet.size() + 1));
        selectedPosition = getItemCount()-1;
        notifyDataSetChanged();

    }

    public NestedRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        if(layoutParams == null){

        }
        else{
            //v.getLayoutParams().height = layoutParams.height;
            v.getLayoutParams().width = layoutParams.width;
        }


        ViewHolder vh = new ViewHolder(v);

        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.mTextView.setText(dataSet.get(position));

        //initial loadup
        if(selectedPosition == -11 && position == (getItemCount()-1)){
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.primary_light));
            selectedPosition = (getItemCount()-1);
        }
        else if(selectedPosition != -1 && position == selectedPosition){
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.primary_light));
        }else{
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.gray_light));
        }


        //TODO: This will be a nested JSONARRAY within the dataset
        int count = new Random().nextInt(10);

        //temp, will be included in dataSet
        ArrayList<String> itemSet = new ArrayList<>();
        for(int a = 1; a <= count; a++)
            itemSet.add("Item "+a);

        BasicRecyclerAdapter rcAdapter = new BasicRecyclerAdapter(context, itemSet, R.layout.item_textview_imageview);

        holder.recyclerView.setAdapter(rcAdapter);


    }


    @Override
    public int getItemCount() {
        return dataSet.size();
    }




}