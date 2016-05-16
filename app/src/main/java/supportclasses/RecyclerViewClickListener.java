package supportclasses;

import android.view.View;

import org.json.JSONObject;

public interface RecyclerViewClickListener {

    void recyclerViewListClicked(View v, int parentPosition, int position, JSONObject item);
}