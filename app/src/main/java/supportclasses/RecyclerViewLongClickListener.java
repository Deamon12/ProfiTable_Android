package supportclasses;

import android.view.View;

import org.json.JSONObject;

public interface RecyclerViewLongClickListener {

    void recyclerViewListLongClicked(View v, int parentPosition, int position, JSONObject item);
}