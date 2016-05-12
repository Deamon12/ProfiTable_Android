package supportclasses;

import android.view.View;

public interface RecyclerViewClickListener {

    void recyclerViewListClicked(View v, int parentPosition, int position, String item);
}