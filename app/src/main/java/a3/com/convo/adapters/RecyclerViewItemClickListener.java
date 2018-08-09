package a3.com.convo.adapters;

import android.view.View;

/** This interface handles the behaviors of our clicking on Convo */

public interface RecyclerViewItemClickListener
{
    void onItemClick(View view, int position);
    void onItemLongClick(View view, int position);
    void onItemDelete(View view, int position);
}