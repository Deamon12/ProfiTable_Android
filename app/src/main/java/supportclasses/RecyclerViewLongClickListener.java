package supportclasses;

import android.view.View;

public interface RecyclerViewLongClickListener {

    void recyclerViewListLongClicked(View v, int parentPosition, int position, MenuItem item);
}